package model;

import java.io.Serial;
import java.io.Serializable;

/**
 * A serializable snapshot of a game in progress: the maze (including every
 * door's answered/blocked state) plus where the player is standing. This is the
 * object written to and read from a save file, closing the gap that the
 * player's position lives in the controller rather than the maze.
 */
public class GameState implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Maze myMaze;
    private final Room myCurrentRoom;

    public GameState(Maze theMaze, Room theCurrentRoom) {
        myMaze = theMaze;
        myCurrentRoom = theCurrentRoom;
    }

    public Maze getMaze() {
        return myMaze;
    }

    /**
     * The player's room. Because the maze and this room are written together,
     * deserialization preserves their shared identity — this is the same
     * instance as the corresponding cell in {@link #getMaze()}'s grid.
     */
    public Room getCurrentRoom() {
        return myCurrentRoom;
    }
}
