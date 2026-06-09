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
    /**
     * The full maze including door states
     */
    private final Maze myMaze;
    /**
     * The room the player is occupying when saving the game
     */
    private final Room myCurrentRoom;

    /**
     * Constructs a snapshot of the game state from the current maze and
     * player position
     * @param theMaze the maze to snapshot
     * @param theCurrentRoom the room the player is currently in
     */
    public GameState(Maze theMaze, Room theCurrentRoom) {
        myMaze = theMaze;
        myCurrentRoom = theCurrentRoom;
    }

    /**
     * Returns the maze captured at the time of save
     * @return the saved maze
     */
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
