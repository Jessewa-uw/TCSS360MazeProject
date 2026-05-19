public class TFQuestion extends Question {

    public TFQuestion(String thePrompt, String theAnswer, int theID) {
        super(thePrompt, theAnswer, theID);
    }

    @Override
    public boolean checkAnswer(String theAnswer) {
        return false;
    }

    @Override
    public String getType() {
        return "True/False";
    }


}
