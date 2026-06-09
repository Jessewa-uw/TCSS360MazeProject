package model;

/**
 * True/False question where the player selects one answer
 */
public class TFQuestion extends Question {
    /**
     * Constructs a true false question with a prompt, correct answer, and
     * database ID
     * @param thePrompt question shown to the player
     * @param theAnswer the correct answer
     * @param theID database ID
     */
    public TFQuestion(String thePrompt, String theAnswer, int theID) {
        super(thePrompt, theAnswer, theID);
    }

    /**
     * Returns true if the guessed answer matches the correct answer
     * @param theAnswer the answer guessed by player
     * @return true if the answer matches
     */
    @Override
    public boolean checkAnswer(String theAnswer) {
        return myAnswer.equalsIgnoreCase(theAnswer);
    }

    /**
     * Returns label of question type
     * @return "True/False"
     */
    @Override
    public String getType() {
        return "True/False";
    }


}
