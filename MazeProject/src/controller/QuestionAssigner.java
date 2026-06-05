package controller;


import model.*;

import java.sql.SQLException;
import java.util.*;

public class QuestionAssigner {

    public QuestionAssigner(List<Door> theDoors) throws SQLException {
        List<Question> questions = retrieveQuestions();
        if (questions.isEmpty()) {
            throw new SQLException("No questions found in database.");
        }

        for (int i = 0; i < theDoors.size(); i++) {
            theDoors.get(i).setMyQuestion(questions.get(i % questions.size()));
        }
    }

    public List<Question> retrieveQuestions() throws SQLException {
        QuestionRepository repo = new QuestionRepository();
        List<Question> q = new ArrayList<>(repo.loadAll());
        Collections.shuffle(q);
        return q;
    }


}
