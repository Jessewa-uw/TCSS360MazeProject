package model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Abstract class for all questions used to lock the maze doors
 * Questions are Serializable so they care preserved when game is saved
 */
public abstract class Question implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Question displayed to player
     */
    protected final String myPrompt;
    /**
     * The correct answer string
     */
    protected final String myAnswer;
    /**
     * ID for the database table
     */
    protected final int id;

    /**
     * Constructs a question with a prompt, correct answer, and database ID
     * @param thePrompt the question to be answered by the player
     * @param theAnswer the correct answer string
     * @param theID ID for database table
     */
    public Question(String thePrompt, String theAnswer, int theID) {
        this.myPrompt = thePrompt;
        this.myAnswer = theAnswer;
        this.id = theID;
    }

    /**
     * Returns question displayed to the player
     * @return prompt string
     */
    public String getMyPrompt() {
        return myPrompt;
    }

    /**
     * Returns true if the guessed answer matches the correct answer
     * @param userAnswer the answer guessed by player
     * @return true if guessed answer matches stored answer
     */
    public boolean checkAnswer(String userAnswer){ return myAnswer.equals(userAnswer); }

    /**
     * Returns a label giving the type of question
     * @return string identifying question type
     */
    public abstract String getType();

    /**
     * Returns a string containing the prompt and correct answer
     * @return a string containing question and answer
     */
    public String toString() {
        return "\tPrompt: " + this.myPrompt + "\tAnswer: " + this.myAnswer;
    }

}
