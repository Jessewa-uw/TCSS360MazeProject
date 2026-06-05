package model;

import java.util.List;

public class MCQuestion extends Question {
    private final List<String> choices;

    public MCQuestion(String thePrompt, String theAnswer, List<String> theChoices, int theID) {
        super(thePrompt, theAnswer, theID);
        this.choices = theChoices;

    }

    @Override
    public boolean checkAnswer(String userAnswer) {
        return myAnswer.equalsIgnoreCase(userAnswer.trim());
    }

    public String getType() {
        return "Multiple Choices";
    }

    public List<String> getChoices() {
        return choices;
    }

    @Override
    public String toString() {
        return "\tPrompt: " + this.myPrompt + "\tOptions: " + this.choices.toString() + "\tAnswer: " + this.myAnswer;
    }

}
