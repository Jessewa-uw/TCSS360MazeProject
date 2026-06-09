package controller;


import model.*;

import java.sql.SQLException;
import java.util.*;

/**
 * Assigns trivia questions to maze doors by drawing from a shuffled pool
 */
public class QuestionAssigner {
    /**
     * Loads all available questions, shuffles them, and provides one to each door
     * @param theDoors the list of doors to assign questions to
     * @throws SQLException if the database can't be reached or doesn't have questions
     */
    public QuestionAssigner(List<Door> theDoors) throws SQLException {
        List<Question> questions = retrieveQuestions();
        if (questions.isEmpty()) {
            throw new SQLException("No questions found in database.");
        }

        for (int i = 0; i < theDoors.size(); i++) {
            theDoors.get(i).setMyQuestion(questions.get(i % questions.size()));
        }
    }

    /**
     * Retrieves the questions from the database in a random order
     * @return a shuffled list of the questions in the database
     * @throws SQLException if the repository cannot load the questions
     */
    public List<Question> retrieveQuestions() throws SQLException {
        QuestionRepository repo = new QuestionRepository();
        List<Question> q = new ArrayList<>(repo.loadAll());
        Collections.shuffle(q);
        return q;
    }


}
