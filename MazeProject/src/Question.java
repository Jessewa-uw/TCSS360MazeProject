public abstract class Question {
    protected final String myPrompt;
    protected final String myAnswer;
    protected final int id;


    public Question(String thePrompt, String theAnswer, int theID) {
        this.myPrompt = thePrompt;
        this.myAnswer = theAnswer;
        this.id = theID;
    }

    public String getMyPrompt() {
        return myPrompt;
    }

    public boolean checkAnswer(String userAnswer){ return myAnswer.equals(userAnswer); };

    public abstract String getType();


}
