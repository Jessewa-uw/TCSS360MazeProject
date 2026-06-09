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
      * @param theReason Description of why the game ended
     */
    void onGameOver(String theReason);

    /**
     * Invoked when the player attempts to move in a direction that is blocked
      * @param theDirection the direction the player tried to move in
     */
    void onInvalidMove(Direction theDirection);

    /**
     * Invoked when the player reaches a locked door and an answer is attempted
      * @param theDoor the attempted door
     * @param theCorrect if the answer to the question was correct or not
     */
    void onDoorAttempt(Door theDoor, boolean theCorrect);
    }
    private final Maze myMaze;
    private Room myCurrentRoom;
    private final MazeControllerListener myListener;

    /**
     * Constructs a new MazeController and places the player at the maze entrance
     * @param theMaze the maze model to control
     * @param theListener the event listener that will receive game state callbacks
     */
    public MazeController(Maze theMaze, MazeControllerListener theListener) {
        this.myMaze = theMaze;
        this.myListener = theListener;
        this.myCurrentRoom = theMaze.getEntrance();
    }

    /**
     * Registers a KeyAdapter on the given Swing component that maps
     * WASD and arrow keys to the four cardinal Directions
     * @param theComponent the Swing component to attach key listener to
     */
    public void attachKeyListener(JComponent theComponent) {
        theComponent.addKeyListener(new KeyAdapter() {
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
     * @param theDirection the cardinal direction to move toward
     */
    public void move(Direction theDirection) {
        if (!myMaze.isRunning()) return;

        Door door = myCurrentRoom.getDoor(theDirection.ordinal());

        if (door == null || door.isBlocked()) {
            myListener.onInvalidMove(theDirection);
            return;
        }
        if (door.isOpen()) {
            advancePlayer(door);
            return;
        }
        myListener.onDoorAttempt(door, false);
    }

    /**
     * Unlocks the given door and advances the player through when
     * a correct answer is given
     * @param theDoor the door whose question was answered correctly
     */
    public void submitCorrectAnswer(Door theDoor) {
        theDoor.unlock();
        advancePlayer(theDoor);
    }

    /**
     * Handles a wrong answer when attempting to unlock the door
     * @param theDoor the door whose question was answered incorrectly
     */
    public void submitWrongAnswer(Door theDoor) {
        if (!myMaze.checkPossible(myCurrentRoom)) {
            myMaze.setRunning(false);
            myListener.onGameOver("No path remains... You are trapped!");
        }
    }

    /**
     * Returns the room the player is currently in
     * @return the current Room
     */
    public Room getMyCurrentRoom() {
        return myCurrentRoom;
    }

    /**
     * Places the player in the given room. Used when restoring a saved game so
     * the player resumes where they left off rather than at the entrance.
     *
     * @param theRoom the room to start in
     */
    public void setMyCurrentRoom(Room theRoom) {
        myCurrentRoom = theRoom;
    }
    private void advancePlayer(Door theDoor) {
        myCurrentRoom = theDoor.getMyDestination().equals(myCurrentRoom)
                ? theDoor.getMyOrigin()
                : theDoor.getMyDestination();
        myListener.onPlayerMoved(myCurrentRoom);

        if (myCurrentRoom.isExit()) {
            myMaze.setRunning(false);
            myListener.onGameWon();
        }
    }
}
