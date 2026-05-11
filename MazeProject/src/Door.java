


public class Door {

    // current room
    private Room myOrigin;

    // door leading to
    private Room myDestination;

    // question locking door
    private Question myQuestion;

    // tracks door state
    private boolean myOpen = false;

    // tracks if door is exit to game
    protected boolean myExit = false;


    public Door(Room origin, Room destination, Question question) {
        myOrigin = origin;
        myDestination = destination;
        myQuestion = question;
    }
    protected boolean unlocked(){
        return myOpen;
    };

    public void setMyQuestion(Question myQuestion) {
        this.myQuestion = myQuestion;
    }

    public Question getMyQuestion() {
        return myQuestion;
    }

    public void setMyDestination(Room myDestination) {
        this.myDestination = myDestination;
    }

    public Room getMyDestination() {
        return myDestination;
    }

    public Room getMyOrigin() {
        return myOrigin;
    }

    public void setMyOrigin(Room myOrigin) {
        this.myOrigin = myOrigin;
    }
}
