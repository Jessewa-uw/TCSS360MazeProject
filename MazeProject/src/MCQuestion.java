public class MCQuestion extends Question {
    private final String[] choices;

    public MCQuestion(String thePrompt, String theAnswer, String[] theChoices, int theID) {
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

    public String[] getChoices() {
        return choices;
    }

}
