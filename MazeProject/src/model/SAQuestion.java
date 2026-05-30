package model;

public class SAQuestion extends Question {


    public SAQuestion(String thePrompt, String theAnswer, int theID) {
        super(thePrompt, theAnswer, theID);
    }

    @Override
    public boolean checkAnswer(String userAnswer) {
        return myAnswer.equalsIgnoreCase(userAnswer.trim());
    }

    @Override
    public String getType() {
        return "Short Answer";
    }
}
