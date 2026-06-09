package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import javax.swing.JComponent;

import model.Door;
import model.Maze;
import model.Room;
import model.Direction;

/**
 * Controls player movement and game state interactions.
 * It processes directional input of every meaningful game event:
 * player movement, door attempts, victory, and game over.
 */
public class MazeController{
/**
 * Interface for reacting to game events
 */
    public interface MazeControllerListener {
    /**
     * Invoked after player successfully moves into a new room
     * @param newRoom the room the player has entered.
     */
    void onPlayerMoved(Room newRoom);

    /**
     * Invoked after player enters the exit room, signalling completed maze
     */
    void onGameWon();

    /**
     * Invoked when the game ends in defeat: no path
      * @param reason Description of why the game ended
     */
    void onGameOver(String reason);

    /**
     * Invoked when the player attempts to move in a direction that is blocked
      * @param direction the direction the player tried to move in
     */
    void onInvalidMove(Direction direction);

    /**
     * Invoked when the player reaches a locked door and an answer is attempted
      * @param door the attempted door
     * @param correct if the answer to the question was correct or not
     */
    void onDoorAttempt(Door door, boolean correct);
    }
    private final Maze maze;
    private Room currentRoom;
    private final MazeControllerListener listener;

    /**
     * Constructs a new MazeController and places the player at the maze entrance
     * @param maze the maze model to control
     * @param listener the event listener that will receive game state callbacks
     */
    public MazeController(Maze maze, MazeControllerListener listener) {
        this.maze = maze;
        this.listener = listener;
        this.currentRoom = maze.getEntrance();
    }

    /**
     * Registers a KeyAdapter on the given Swing component that maps
     * WASD and arrow keys to the four cardinal Directions
     * @param component the Swing component to attach key listener to
     */
    public void attachKeyListener(JComponent component) {
        component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if      (code == KeyEvent.VK_W || code == KeyEvent.VK_UP)    move(Direction.NORTH);
                else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) move(Direction.EAST);
                else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN)  move(Direction.SOUTH);
                else if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT)  move(Direction.WEST);
            }
        });
    }

    /**
     * Attempts to move player a single step in the given direction
     * @param direction the cardinal direction to move toward
     */
    public void move(Direction direction) {
        if (!maze.isRunning()) return;

        Door door = currentRoom.getDoor(direction.ordinal());

        if (door == null || door.isBlocked()) {
            listener.onInvalidMove(direction);
            return;
        }
        if (door.isOpen()) {
            advancePlayer(door);
            return;
        }
        listener.onDoorAttempt(door, false);
    }

    /**
     * Unlocks the given door and advances the player through when
     * a correct answer is given
     * @param door the door whose question was answered correctly
     */
    public void submitCorrectAnswer(Door door) {
        door.unlock();
        advancePlayer(door);
    }

    /**
     * Handles a wrong answer when attempting to unlock the door
     * @param door the door whose question was answered incorrectly
     */
    public void submitWrongAnswer(Door door) {
        if (!maze.checkPossible(currentRoom)) {
            maze.setRunning(false);
            listener.onGameOver("No path remains... You are trapped!");
        }
    }

    /**
     * Returns the room the player is currently in
     * @return the current Room
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Places the player in the given room. Used when restoring a saved game so
     * the player resumes where they left off rather than at the entrance.
     *
     * @param room the room to start in
     */
    public void setCurrentRoom(Room room) {
        currentRoom = room;
    }
    private void advancePlayer(Door door) {
        currentRoom = door.getMyDestination().equals(currentRoom)
                ? door.getMyOrigin()
                : door.getMyDestination();
        listener.onPlayerMoved(currentRoom);

        if (currentRoom.isExit()) {
            maze.setRunning(false);
            listener.onGameWon();
        }
    }
}
