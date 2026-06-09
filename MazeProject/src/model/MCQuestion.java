package model;

import java.util.List;

/**
 * A multiple choice question that presents the player with a set of answer
 * options where exactly one is correct
 */
public class MCQuestion extends Question {
    /**
     * List of answer options
     */
    private final List<String> choices;

    /**
     * Constructs a multiple choice question with the prompt, correct
     * answer, answer choices, and the database ID
     * @param thePrompt the question for the player to answer
     * @param theAnswer the correct answer string
     * @param theChoices the list of answer options that are displayed
     * @param theID the unique ID of the database
     */
    public MCQuestion(String thePrompt, String theAnswer, List<String> theChoices, int theID) {
        super(thePrompt, theAnswer, theID);
        this.choices = theChoices;

    }

    /**
     * Returns true if the guessed answer matches the correct answer
     * @param userAnswer the answer string submitted by the player
     * @return true if the answer matches
     */
    @Override
    public boolean checkAnswer(String userAnswer) {
        return myAnswer.equalsIgnoreCase(userAnswer.trim());
    }

    /**
     * Returns a label identifying the question type
     * @return "Multiple Choices"
     */
    public String getType() {
        return "Multiple Choices";
    }

    /**
     * Returns list of answer options for the question
     * @return list of answer choices
     */
    public List<String> getChoices() {
        return choices;
    }

    /**
     * Returns a string containing the prompt, choices, and correct answer
     * @return a string containing all parts of the question
     */
    @Override
    public String toString() {
        return "\tPrompt: " + this.myPrompt + "\tOptions: " + this.choices.toString() + "\tAnswer: " + this.myAnswer;
    }

}
