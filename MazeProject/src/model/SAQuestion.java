package model;

/**
 * A short answer question where the player types an answer to be
 * matched against the correct answer
 */
public class SAQuestion extends Question {

    /**
     * Constructs a short answer question with a prompt, correct answer,
     * and database ID
     * @param thePrompt question for the player to answer
     * @param theAnswer correct answer string
     * @param theID database ID
     */
    public SAQuestion(String thePrompt, String theAnswer, int theID) {
        super(thePrompt, theAnswer, theID);
    }

    /**
     * Returns true if the guessed answer matches the correct answer
     * @param theUserAnswer the answer guessed by player
     * @return true if the answer matches
     */
    @Override
    public boolean checkAnswer(String theUserAnswer) {
        return myAnswer.equalsIgnoreCase(theUserAnswer.trim());
    }

    /**
     * Returns question type
     * @return "Short Answer"
     */
    @Override
    public String getType() {
        return "Short Answer";
    }
}
