package model;

import java.io.Serial;
import java.io.Serializable;

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
    Contructs a Door objec with two parameters.

     @param origin starting room
     @param destination destination room

    **/
    public Door(Room origin, Room destination) {
        myOrigin = origin;
        myDestination = destination;
    }
    public boolean isLocked(){
        return myState == DoorState.UNANSWERED;
    }

    public void unlock(){
        myState = DoorState.OPEN;
    }

    public void block(){
        myState = DoorState.BLOCKED;
    }
    public boolean isBlocked(){
        return myState == DoorState.BLOCKED;
    }
    public boolean isOpen(){
        return myState == DoorState.OPEN;
    }

    public void setExit(){
        myExit = true;
    }
    public void setEntrance(){
        myEntrance = true;
    }
    /**
     * Returns the room on the other side of this door from the given room.
     *
     * @param from the room the player is currently in
     * @return the room on the other side
     */
    public Room getOtherSide(Room from) {
        return from.equals(myOrigin) ? myDestination : myOrigin;
    }

    public void setMyQuestion(Question myQuestion) {
        this.myQuestion = myQuestion;
    }

    public Question getMyQuestion() {
        return myQuestion;
    }

    public Room getMyDestination() {
        return myDestination;
    }

    public Room getMyOrigin() {
        return myOrigin;
    }

}
