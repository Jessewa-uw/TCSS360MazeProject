


public class Door {

    // current room
    private final Room myOrigin;

    // door leading to
    private final Room myDestination;

    // question locking door
    private Question myQuestion;

    // tracks door state
    private static boolean myOpen = false;

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
    protected boolean isLocked(){
        return myOpen;
    }

    public static void setUnlocked(){
        myOpen = true;
    }

    public void setExit(){
        myExit = true;
    }
    public void setEntrance(){
        myEntrance = true;
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
