public class SAQuestion extends Question {


    public SAQuestion(String thePrompt, String theAnswer) {
        super(thePrompt, theAnswer);
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
