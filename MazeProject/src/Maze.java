package MazeProject.src;

import java.io.*;
import java.util.*;


public class Maze implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final int SIZE = 4;

    private static final double DOOR_PROBABILITY = 0.5;

    private final Room[][] grid;
    private final Room entrance;
    private final Room exit;
    private boolean running;
    private boolean possible;
    private boolean won;

    public Maze() {
        grid = new Room[SIZE][SIZE];
        allocateRooms();
        generateMaze();

        entrance = grid[0][0];
        exit     = grid[SIZE - 1][SIZE - 1];
        running  = true;
        possible = true;
        won      = false;
    }

    private void allocateRooms() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                grid[r][c] = new Room(r, c);

        grid[0][0].setEntrance();
        grid[SIZE - 1][SIZE - 1].setExit();
    }

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

    private void createDoor(int r1, int c1, int r2, int c2) {
        Door door = new Door(getRoom(r1, c1), getRoom(r2, c2));

        if (r2 == r1 - 1) {
            grid[r1][c1].setDoor(Direction.NORTH, door);
            grid[r2][c2].setDoor(Direction.SOUTH, door);
        } else if (r2 == r1 + 1) {
            grid[r1][c1].setDoor(Direction.SOUTH, door);
            grid[r2][c2].setDoor(Direction.NORTH, door);
        } else if (c2 == c1 - 1) {
            grid[r1][c1].setDoor(Direction.WEST, door);
            grid[r2][c2].setDoor(Direction.EAST, door);
        } else {
            grid[r1][c1].setDoor(Direction.EAST, door);
            grid[r2][c2].setDoor(Direction.WEST, door);
        }
    }

    private void ensureConnected(Random rng) {
        boolean[][] reached = bfsReachable();

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (!reached[r][c]) {
                    openRandomNeighborDoor(r, c, rng);
                    reached = bfsReachable();
                }
            }
        }
    }

    private boolean[][] bfsReachable() {
        boolean[][] reached = new boolean[SIZE][SIZE];
        Queue<Room> queue = new LinkedList<>();

        reached[0][0] = true;
        queue.add(grid[0][0]);

        while (!queue.isEmpty()) {
            Room room = queue.poll();
            int r = room.getRow();
            int c = room.getCol();

            if (r > 0        && !isWall(r, c, Direction.NORTH) && !reached[r-1][c]) { reached[r-1][c] = true; queue.add(grid[r-1][c]); }
            if (r < SIZE - 1 && !isWall(r, c, Direction.SOUTH) && !reached[r+1][c]) { reached[r+1][c] = true; queue.add(grid[r+1][c]); }
            if (c > 0        && !isWall(r, c, Direction.WEST)  && !reached[r][c-1]) { reached[r][c-1] = true; queue.add(grid[r][c-1]); }
            if (c < SIZE - 1 && !isWall(r, c, Direction.EAST)  && !reached[r][c+1]) { reached[r][c+1] = true; queue.add(grid[r][c+1]); }
        }

        return reached;
    }

    private boolean isWall(int r, int c, Direction dir) {
        Door door = grid[r][c].getDoor(dir);
        return door == null || door.isLocked();
    }

    private void openRandomNeighborDoor(int r, int c, Random rng) {
        List<int[]> neighbors = new ArrayList<>();
        if (r > 0)        neighbors.add(new int[]{r - 1, c});
        if (r < SIZE - 1) neighbors.add(new int[]{r + 1, c});
        if (c > 0)        neighbors.add(new int[]{r, c - 1});
        if (c < SIZE - 1) neighbors.add(new int[]{r, c + 1});

        Collections.shuffle(neighbors, rng);
        int[] chosen = neighbors.getFirst();
        createDoor(r, c, chosen[0], chosen[1]);
    }

    public boolean checkPossible(Room current) {
        boolean[][] visited = new boolean[SIZE][SIZE];
        Queue<Room> queue = new LinkedList<>();

        visited[current.getRow()][current.getCol()] = true;
        queue.add(current);

        while (!queue.isEmpty()) {
            Room room = queue.poll();

            if (room.isExit()) {
                possible = true;
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

        possible = false;
        running  = false;
        return false;
    }

    private List<Room> getOpenNeighbors(Room room) {
        List<Room> neighbors = new ArrayList<>();
        int r = room.getRow();
        int c = room.getCol();

        if (r > 0        && !isWall(r, c, Direction.NORTH)) neighbors.add(grid[r - 1][c]);
        if (r < SIZE - 1 && !isWall(r, c, Direction.SOUTH)) neighbors.add(grid[r + 1][c]);
        if (c > 0        && !isWall(r, c, Direction.WEST))  neighbors.add(grid[r][c - 1]);
        if (c < SIZE - 1 && !isWall(r, c, Direction.EAST))  neighbors.add(grid[r][c + 1]);

        return neighbors;
    }

    public Room getEntrance() { return entrance; }
    public Room getExit()     { return exit;     }

    public Room getRoom(int r, int c) {
        if (r < 0 || r >= SIZE || c < 0 || c >= SIZE)
            throw new IllegalArgumentException("Room out of bounds: (" + r + ", " + c + ")");
        return grid[r][c];
    }

    public Room[][] getGrid() {
        Room[][] copy = new Room[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) copy[r] = Arrays.copyOf(grid[r], SIZE);
        return copy;
    }

    public boolean isRunning()  { return running;  }
    public boolean isPossible() { return possible; }
    public boolean isWon()      { return won;      }

    public void setRunning(boolean running)   { this.running  = running;  }
    public void setPossible(boolean possible) { this.possible = possible; }
    public void setWon(boolean won)           { this.won      = won;      }
}