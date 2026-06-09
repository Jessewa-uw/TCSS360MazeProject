package model;

import controller.QuestionAssigner;

import java.io.Serial;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;


/**
 * Represents the 4x4 trivia maze game board.
 * Acts as the Model in the MVC pattern — holds all maze state and
 * exposes BFS-based reachability checks to the controller.
 \
 */
public class Maze implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final int SIZE = 5;

    /**
     * Probability that any given wall between two adjacent rooms is
     * opened during generation.
     */
    private static final double DOOR_PROBABILITY = 0.5;

    private final Room[][] myGrid;

    private final Room myEntrance;

    private boolean myRunning;


    /**
     * Constructs a new Maze, allocating all rooms and randomly
     * generating a connected layout. Every run produces a different maze
     * with multiple possible paths from entrance to exit.
     */
    public Maze() throws SQLException {
        myGrid = new Room[SIZE][SIZE];
        allocateRooms();
        generateMaze();
        new QuestionAssigner(getDoors());

        myEntrance = myGrid[0][0];
        myRunning = true;

    }


    /**
     * Allocates a Room for every cell in the grid and marks the
     * entrance and exit rooms.
     */
    private void allocateRooms() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                myGrid[r][c] = new Room(r, c);
            }
        }
        myGrid[0][0].setEntrance();
        myGrid[SIZE - 1][SIZE - 1].setExit();
    }

    /**
     * Randomly opens doors between adjacent rooms, then ensures full
     * connectivity
     */
    private void generateMaze() {
        Random rng = new Random();

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (r < SIZE - 1 && rng.nextDouble() < DOOR_PROBABILITY)
                    createDoor(r, c, r + 1, c);
                if (c < SIZE - 1 && rng.nextDouble() < DOOR_PROBABILITY)
                    createDoor(r, c, r, c + 1);
            }
        }

        ensureConnected(rng);
    }

    /**
     * Opens a shared Door between two adjacent rooms, visible from both sides.
     *
     * @param theRow1 row of the first room
     * @param theColumn1 column of the first room
     * @param theRow2 row of the second room
     * @param theColumn2 column of the second room
     */
    private void createDoor(int theRow1, int theColumn1, int theRow2, int theColumn2) {
        Door door = new Door(getRoom(theRow1, theColumn1), getRoom(theRow2, theColumn2));

        if (theRow2 == theRow1 - 1) {
            myGrid[theRow1][theColumn1].setDoor(Direction.NORTH.ordinal(), door);
            myGrid[theRow2][theColumn2].setDoor(Direction.SOUTH.ordinal(), door);
        } else if (theRow2 == theRow1 + 1) {
            myGrid[theRow1][theColumn1].setDoor(Direction.SOUTH.ordinal(), door);
            myGrid[theRow2][theColumn2].setDoor(Direction.NORTH.ordinal(), door);
        } else if (theColumn2 == theColumn1 - 1) {
            myGrid[theRow1][theColumn1].setDoor(Direction.WEST.ordinal(), door);
            myGrid[theRow2][theColumn2].setDoor(Direction.EAST.ordinal(), door);
        } else {
            myGrid[theRow1][theColumn1].setDoor(Direction.EAST.ordinal(), door);
            myGrid[theRow2][theColumn2].setDoor(Direction.WEST.ordinal(), door);
        }
    }

    /**
     * Ensures every room is reachable from the entrance, opening a random
     * door into any isolated room found by BFS.
     *
     * @param theRandom shared Random instance
     */
    private void ensureConnected(Random theRandom) {
        boolean[][] reached = bfsReachable();

        boolean progress = true;
        while (progress) {
            progress = false;
            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    if (!reached[r][c] && connectToReachedNeighbor(r, c, reached, theRandom)) {
                        reached = bfsReachable();
                        progress = true;
                    }
                }
            }
        }
    }

    /**
     * Performs a BFS from the entrance across all
     * currently open doors and returns a grid marking which rooms are
     * reachable.
     *
     * @return a boolean[SIZE][SIZE] where true means the
     *         room at that position is reachable from the entrance
     */
    private boolean[][] bfsReachable() {
        boolean[][] reached = new boolean[SIZE][SIZE];
        Queue<Room> queue = new LinkedList<>();

        reached[0][0] = true;
        queue.add(myGrid[0][0]);

        while (!queue.isEmpty()) {
            Room room = queue.poll();
            int r = room.getRow();
            int c = room.getCol();

            if (r > 0        && !isWall(r, c, Direction.NORTH.ordinal()) && !reached[r-1][c]) { reached[r-1][c] = true; queue.add(myGrid[r-1][c]); }
            if (r < SIZE - 1 && !isWall(r, c, Direction.SOUTH.ordinal()) && !reached[r+1][c]) { reached[r+1][c] = true; queue.add(myGrid[r+1][c]); }
            if (c > 0        && !isWall(r, c, Direction.WEST.ordinal())  && !reached[r][c-1]) { reached[r][c-1] = true; queue.add(myGrid[r][c-1]); }
            if (c < SIZE - 1 && !isWall(r, c, Direction.EAST.ordinal())  && !reached[r][c+1]) { reached[r][c+1] = true; queue.add(myGrid[r][c+1]); }
        }

        return reached;
    }

    /**
     * Returns true if there is no open door between the room at
     * (r, c) and its neighbor in the given direction.
     * A null door means the wall was never opened during generation.
     * A blocked door means the player ran out of attempts.
     *
     * @param theRow   row of the room to check
     * @param theColumn   column of the room to check
     * @param theDirection direction of the wall to check
     * @return true if the wall is solid (no door, or door is locked)
     */
    private boolean isWall(int theRow, int theColumn, int theDirection) {
        Door door = myGrid[theRow][theColumn].getDoor(theDirection);
        return door == null || door.isBlocked();
    }

    /**
     * Opens a door from the isolated room at (r, c) to a randomly chosen
     * neighbor that is already reachable from the entrance, joining the room to
     * the entrance's connected component.
     *
     * @param theRow       row of the isolated room
     * @param theColumn       column of the isolated room
     * @param theReached current entrance reachability grid
     * @param theRandom     shared instance
     * @return true if a reachable neighbor was found and a door opened; false
     *         if no neighbor is reachable yet (retry after others connect)
     */
    private boolean connectToReachedNeighbor(int theRow, int theColumn, boolean[][] theReached, Random theRandom) {
        List<int[]> neighbors = new ArrayList<>();
        if (theRow > 0        && theReached[theRow - 1][theColumn]) neighbors.add(new int[]{theRow - 1, theColumn});
        if (theRow < SIZE - 1 && theReached[theRow + 1][theColumn]) neighbors.add(new int[]{theRow + 1, theColumn});
        if (theColumn > 0        && theReached[theRow][theColumn - 1]) neighbors.add(new int[]{theRow, theColumn - 1});
        if (theColumn < SIZE - 1 && theReached[theRow][theColumn + 1]) neighbors.add(new int[]{theRow, theColumn + 1});

        if (neighbors.isEmpty()) {
            return false;
        }
        int[] chosen = neighbors.get(theRandom.nextInt(neighbors.size()));
        createDoor(theRow, theColumn, chosen[0], chosen[1]);
        return true;
    }

    /**
     * BFS from current to check if the exit is still reachable.
     * Call after every door is locked — returns false if the player
     * is trapped and the game is over.
     *
     * @param theCurrent the room the player is currently standing in
     * @return true if the exit is reachable, false if trapped
     */
    public boolean checkPossible(Room theCurrent) {
        boolean[][] visited = new boolean[SIZE][SIZE];
        Queue<Room> queue = new LinkedList<>();

        visited[theCurrent.getRow()][theCurrent.getCol()] = true;
        queue.add(theCurrent);

        while (!queue.isEmpty()) {
            Room room = queue.poll();

            if (room.isExit()) {
                return true;
            }

            for (Room neighbor : getOpenNeighbors(room)) {
                int r = neighbor.getRow();
                int c = neighbor.getCol();
                if (!visited[r][c]) {
                    visited[r][c] = true;
                    queue.add(neighbor);
                }
            }
        }

        myRunning = false;
        return false;
    }

    /**
     * Returns all rooms adjacent to room that are reachable through
     * an open, unlocked door. Used exclusively by checkPossible
     * to build the BFS frontier.
     *
     * @param theRoom the room whose open neighbors are requested
     * @return a list of reachable neighboring rooms,
     * may be empty if all surrounding doors are locked or absent
     */
    private List<Room> getOpenNeighbors(Room theRoom) {
        List<Room> neighbors = new ArrayList<>();
        int r = theRoom.getRow();
        int c = theRoom.getCol();

        if (r > 0        && !isWall(r, c, Direction.NORTH.ordinal())) neighbors.add(myGrid[r - 1][c]);
        if (r < SIZE - 1 && !isWall(r, c, Direction.SOUTH.ordinal())) neighbors.add(myGrid[r + 1][c]);
        if (c > 0        && !isWall(r, c, Direction.WEST.ordinal()))  neighbors.add(myGrid[r][c - 1]);
        if (c < SIZE - 1 && !isWall(r, c, Direction.EAST.ordinal()))  neighbors.add(myGrid[r][c + 1]);

        return neighbors;
    }

    /**
     * Returns the entrance room
     * The returned reference is the live room object — door state changes
     * made on it directly affect the maze.
     *
     * @return the entrance
     */
    public Room getEntrance() {
        return myEntrance;
    }


    /**
     * Returns the room at the given grid coordinates.
     *
     * @param theRow row index
     * @param theColumn column index
     * @return the Room at grid[r][c]
     * @throws IllegalArgumentException if r or c is out of bounds
     */
    public Room getRoom(int theRow, int theColumn) {
        if (theRow < 0 || theRow >= SIZE || theColumn < 0 || theColumn >= SIZE)
            throw new IllegalArgumentException("Room out of bounds: (" + theRow + ", " + theColumn + ")");
        return myGrid[theRow][theColumn];
    }

    public List<Door> getDoors() {
        Set<Door> doors = new LinkedHashSet<>();
        for (int r = 0; r < SIZE; r++){
            for (int c = 0; c < SIZE; c++){
                doors.addAll(getRoom(r, c).getDoors());
            }
        }
        return new ArrayList<>(doors);
    }


    /**
     * @return true while the game is in progress;
     *         false after a win or a game-over condition
     */
    public boolean isRunning() {
        return myRunning;
    }

    /**
     * Sets whether the game is currently running. Typically called by the
     * controller to halt the game loop after a win or game-over event.
     *
     * @param theRunning false to end the game, true to resume
     */
    public void setRunning(boolean theRunning) {
        this.myRunning = theRunning;
    }


}