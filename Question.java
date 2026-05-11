public abstract class Question {
    protected String myPrompt;
    protected String myAnswer;

    public Question(String thePrompt, String theAnswer) {
        this.myPrompt = thePrompt;
        this.myAnswer = theAnswer;
    }

    public String getMyPrompt() {
        return myPrompt;
    }

    public abstract boolean checkAnswer(String userAnswer);

    public abstract String getType();


}
