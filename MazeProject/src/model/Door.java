package model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a passage between two adjacent rooms in the maze
 * Doors are Serializable so that a game in progress, including the states
 * and questions, can be saved and restored
 */
public class Door implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // current room
    private final Room myOrigin;

    // door leading to
    private final Room myDestination;

    // question locking door
    private Question myQuestion;

    // tracks door state
    private DoorState myState = DoorState.UNANSWERED;

    // tracks if door is exit to game
    protected boolean myExit = false;

    // tracks if door is entrance to the game
    protected boolean myEntrance = false;

    /**
    Constructs a Door object with two parameters.

     @param theOrigin starting room
     @param theDestination destination room

    **/
    public Door(Room theOrigin, Room theDestination) {
        myOrigin = theOrigin;
        myDestination = theDestination;
    }

    /**
     * Returns true if the door is locked and requires player to
     * answer the attached question
     * @return true if the state is unanswered
     */
    public boolean isLocked(){
        return myState == DoorState.UNANSWERED;
    }

    /**
     * Unlocks the door which sets the state to open
     */
    public void unlock(){
        myState = DoorState.OPEN;
    }

    /**
     * Permanently blocks the door, making it treated as a wall
     */
    public void block(){
        myState = DoorState.BLOCKED;
    }

    /**
     * Returns true if the door is permanently blocked and cannot be
     * passed through
     * @return true if the door state is blocked
     */
    public boolean isBlocked(){
        return myState == DoorState.BLOCKED;
    }

    /**
     * Returns true if the door is unlocked and player can go through
     * @return true if the door state is open
     */
    public boolean isOpen(){
        return myState == DoorState.OPEN;
    }

    /**
     * Marks door as the maze exit
     */
    public void setExit(){
        myExit = true;
    }

    /**
     * Marks door as maze entrance
     */
    public void setEntrance(){
        myEntrance = true;
    }
    /**
     * Returns the room on the other side of this door from the given room.
     *
     * @param theFrom the room the player is currently in
     * @return the room on the other side
     */
    public Room getOtherSide(Room theFrom) {
        return theFrom.equals(myOrigin) ? myDestination : myOrigin;
    }

    /**
     * Assigns the question the player must answer to unlock the door
     * @param theQuestion the question to be attached to the door
     */
    public void setMyQuestion(Question theQuestion) {
        this.myQuestion = theQuestion;
    }

    /**
     * Returns the question attached to the door
     * @return the attached question
     */
    public Question getMyQuestion() {
        return myQuestion;
    }

    /**
     * Returns the destination room the door is connected to
     * @return the destination room
     */
    public Room getMyDestination() {
        return myDestination;
    }

    /**
     * Returns the origin room the door is connected to
     * @return the origin room
     */
    public Room getMyOrigin() {
        return myOrigin;
    }

}
