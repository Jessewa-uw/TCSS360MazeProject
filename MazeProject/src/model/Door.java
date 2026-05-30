package model;


public class Door {

    // current room
    private final Room myOrigin;

    // door leading to
    private final Room myDestination;

    // question locking door
    private Question myQuestion;

    // tracks door state
    private boolean myLocked = true;

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
        return myLocked;
    }

    public void setUnlocked(){
        myLocked = false;
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
