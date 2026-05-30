package MazeProject.src;


public class Door {

    private final Room myOrigin;
    private final Room myDestination;
    private Question myQuestion;

    private boolean myLocked = true;

    protected boolean myExit     = false;
    protected boolean myEntrance = false;

    public Door(Room origin, Room destination) {
        myOrigin      = origin;
        myDestination = destination;
    }

    public boolean isLocked() {
        return myLocked;
    }

    public void setUnlocked() {
        myLocked = false;
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

    public void     setMyQuestion(Question q) { myQuestion    = q;  }
    public Question getMyQuestion()           { return myQuestion;  }
    public Room     getMyDestination()        { return myDestination; }
    public Room     getMyOrigin()             { return myOrigin;     }
}
