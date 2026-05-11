public class TFQuestion extends Question {

    public TFQuestion(String thePrompt, String theAnswer) {
        super(thePrompt, theAnswer);
    }

    @Override
    public String getMyPrompt() {
        return myPrompt;
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
