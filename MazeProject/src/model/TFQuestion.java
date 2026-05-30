package model;

public class TFQuestion extends Question {

    public TFQuestion(String thePrompt, String theAnswer, int theID) {
        super(thePrompt, theAnswer, theID);
    }

    @Override
    public boolean checkAnswer(String theAnswer) {
        return myAnswer.equalsIgnoreCase(theAnswer);
    }

    @Override
    public String getType() {
        return "True/False";
    }


}
