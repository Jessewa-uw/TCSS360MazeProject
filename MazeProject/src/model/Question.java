package model;

import java.io.Serial;
import java.io.Serializable;

public abstract class Question implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    protected final String myPrompt;
    protected final String myAnswer;
    protected final int id;


    public Question(String thePrompt, String theAnswer, int theID) {
        this.myPrompt = thePrompt;
        this.myAnswer = theAnswer;
        this.id = theID;
    }

    public String getMyPrompt() {
        return myPrompt;
    }

    public boolean checkAnswer(String userAnswer){ return myAnswer.equals(userAnswer); }

    public abstract String getType();

    public String toString() {
        return "\tPrompt: " + this.myPrompt + "\tAnswer: " + this.myAnswer;
    }

}
