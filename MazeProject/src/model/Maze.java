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

    private final Room[][] grid;

    private final Room entrance;

    private boolean running;


    /**
     * Constructs a new Maze, allocating all rooms and randomly
     * generating a connected layout. Every run produces a different maze
     * with multiple possible paths from entrance to exit.
     */
    public Maze() throws SQLException {
        grid = new Room[SIZE][SIZE];
        allocateRooms();
        generateMaze();
        new QuestionAssigner(getDoors());

        entrance = grid[0][0];
        running  = true;

    }


    /**
     * Allocates a Room for every cell in the grid and marks the
     * entrance and exit rooms.
     */
    private void allocateRooms() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                grid[r][c] = new Room(r, c);
            }
        }
        grid[0][0].setEntrance();
        grid[SIZE - 1][SIZE - 1].setExit();
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
     * @param r1 row of the first room
     * @param c1 column of the first room
     * @param r2 row of the second room
     * @param c2 column of the second room
     */
    private void createDoor(int r1, int c1, int r2, int c2) {
        Door door = new Door(getRoom(r1, c1), getRoom(r2, c2));

        if (r2 == r1 - 1) {
            grid[r1][c1].setDoor(Direction.NORTH.ordinal(), door);
            grid[r2][c2].setDoor(Direction.SOUTH.ordinal(), door);
        } else if (r2 == r1 + 1) {
            grid[r1][c1].setDoor(Direction.SOUTH.ordinal(), door);
            grid[r2][c2].setDoor(Direction.NORTH.ordinal(), door);
        } else if (c2 == c1 - 1) {
            grid[r1][c1].setDoor(Direction.WEST.ordinal(), door);
            grid[r2][c2].setDoor(Direction.EAST.ordinal(), door);
        } else {
            grid[r1][c1].setDoor(Direction.EAST.ordinal(), door);
            grid[r2][c2].setDoor(Direction.WEST.ordinal(), door);
        }
    }

    /**
     * Ensures every room is reachable from the entrance, opening a random
     * door into any isolated room found by BFS.
     *
     * @param rng shared Random instance
     */
    private void ensureConnected(Random rng) {
        boolean[][] reached = bfsReachable();

        boolean progress = true;
        while (progress) {
            progress = false;
            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    if (!reached[r][c] && connectToReachedNeighbor(r, c, reached, rng)) {
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
        queue.add(grid[0][0]);

        while (!queue.isEmpty()) {
            Room room = queue.poll();
            int r = room.getRow();
            int c = room.getCol();

            if (r > 0        && !isWall(r, c, Direction.NORTH.ordinal()) && !reached[r-1][c]) { reached[r-1][c] = true; queue.add(grid[r-1][c]); }
            if (r < SIZE - 1 && !isWall(r, c, Direction.SOUTH.ordinal()) && !reached[r+1][c]) { reached[r+1][c] = true; queue.add(grid[r+1][c]); }
            if (c > 0        && !isWall(r, c, Direction.WEST.ordinal())  && !reached[r][c-1]) { reached[r][c-1] = true; queue.add(grid[r][c-1]); }
            if (c < SIZE - 1 && !isWall(r, c, Direction.EAST.ordinal())  && !reached[r][c+1]) { reached[r][c+1] = true; queue.add(grid[r][c+1]); }
        }

        return reached;
    }

    /**
     * Returns true if there is no open door between the room at
     * (r, c) and its neighbor in the given direction.
     * A null door means the wall was never opened during generation.
     * A blocked door means the player ran out of attempts.
     *
     * @param r   row of the room to check
     * @param c   column of the room to check
     * @param dir direction of the wall to check
     * @return true if the wall is solid (no door, or door is locked)
     */
    private boolean isWall(int r, int c, int dir) {
        Door door = grid[r][c].getDoor(dir);
        return door == null || door.isBlocked();
    }

    /**
     * Opens a door from the isolated room at (r, c) to a randomly chosen
     * neighbor that is already reachable from the entrance, joining the room to
     * the entrance's connected component.
     *
     * @param r       row of the isolated room
     * @param c       column of the isolated room
     * @param reached current entrance reachability grid
     * @param rng     shared instance
     * @return true if a reachable neighbor was found and a door opened; false
     *         if no neighbor is reachable yet (retry after others connect)
     */
    private boolean connectToReachedNeighbor(int r, int c, boolean[][] reached, Random rng) {
        List<int[]> neighbors = new ArrayList<>();
        if (r > 0        && reached[r - 1][c]) neighbors.add(new int[]{r - 1, c});
        if (r < SIZE - 1 && reached[r + 1][c]) neighbors.add(new int[]{r + 1, c});
        if (c > 0        && reached[r][c - 1]) neighbors.add(new int[]{r, c - 1});
        if (c < SIZE - 1 && reached[r][c + 1]) neighbors.add(new int[]{r, c + 1});

        if (neighbors.isEmpty()) {
            return false;
        }
        int[] chosen = neighbors.get(rng.nextInt(neighbors.size()));
        createDoor(r, c, chosen[0], chosen[1]);
        return true;
    }

    /**
     * BFS from current to check if the exit is still reachable.
     * Call after every door is locked — returns false if the player
     * is trapped and the game is over.
     *
     * @param current the room the player is currently standing in
     * @return true if the exit is reachable, false if trapped
     */
    public boolean checkPossible(Room current) {
        boolean[][] visited = new boolean[SIZE][SIZE];
        Queue<Room> queue = new LinkedList<>();

        visited[current.getRow()][current.getCol()] = true;
        queue.add(current);

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

        running  = false;
        return false;
    }

    /**
     * Returns all rooms adjacent to room that are reachable through
     * an open, unlocked door. Used exclusively by checkPossible
     * to build the BFS frontier.
     *
     * @param room the room whose open neighbors are requested
     * @return a list of reachable neighboring rooms,
     * may be empty if all surrounding doors are locked or absent
     */
    private List<Room> getOpenNeighbors(Room room) {
        List<Room> neighbors = new ArrayList<>();
        int r = room.getRow();
        int c = room.getCol();

        if (r > 0        && !isWall(r, c, Direction.NORTH.ordinal())) neighbors.add(grid[r - 1][c]);
        if (r < SIZE - 1 && !isWall(r, c, Direction.SOUTH.ordinal())) neighbors.add(grid[r + 1][c]);
        if (c > 0        && !isWall(r, c, Direction.WEST.ordinal()))  neighbors.add(grid[r][c - 1]);
        if (c < SIZE - 1 && !isWall(r, c, Direction.EAST.ordinal()))  neighbors.add(grid[r][c + 1]);

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
        return entrance;
    }


    /**
     * Returns the room at the given grid coordinates.
     *
     * @param r row index
     * @param c column index
     * @return the Room at grid[r][c]
     * @throws IllegalArgumentException if r or c is out of bounds
     */
    public Room getRoom(int r, int c) {
        if (r < 0 || r >= SIZE || c < 0 || c >= SIZE)
            throw new IllegalArgumentException("Room out of bounds: (" + r + ", " + c + ")");
        return grid[r][c];
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
        return running;
    }

    /**
     * Sets whether the game is currently running. Typically called by the
     * controller to halt the game loop after a win or game-over event.
     *
     * @param running false to end the game, true to resume
     */
    public void setRunning(boolean running) {
        this.running = running;
    }


}