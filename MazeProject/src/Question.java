package MazeProject.src;

public abstract class Question {
    protected String myPrompt;
    private final String myAnswer;

    public Question(String thePrompt, String theAnswer) {
        this.myPrompt = thePrompt;
        this.myAnswer = theAnswer;
    }

    public String getMyPrompt() {
        return myPrompt;
    }

    protected String getMyAnswer() { return myAnswer; }

    public abstract boolean checkAnswer(String userAnswer);

    public abstract String getType();


}
