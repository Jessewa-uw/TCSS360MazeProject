package MazeProject.src;

public class MCQuestion extends Question {
    private String[] choices;

    public MCQuestion(String prompt, String answer, String[] choices) {
        super(prompt, answer);
        this.choices = choices;

    }

    @Override
    public boolean checkAnswer(String userAnswer) {
        return getMyAnswer().equalsIgnoreCase(userAnswer.trim());
    }

    public String getMyPrompt() {
        return myPrompt;
    }

    public String getType() {
        return "Multiple Choices";
    }

    public String[] getChoices() {
        return choices;
    }

}
