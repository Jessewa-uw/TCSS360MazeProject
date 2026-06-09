package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Question;
import model.SAQuestion;
import model.TFQuestion;
import model.MCQuestion;

/**
 * Data access object responsible for loading the questions from the SQLite database
 */
public final class QuestionRepository {
    private static final String DB_URL = "jdbc:sqlite:MazeProjectDB.db";

    /**
     * Loads every question from the database and returns them as a list
     * @return a list of all questions found in the database
     * @throws SQLException if the database cannot be reached or an unrecognized type is found
     */
    public List<Question> loadAll() throws SQLException {
        List<Question> questions = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {


            Map<Integer, List<String>> optionsByQuestion = loadOptions(conn);


            String sql = "SELECT Question_id, Question, Answer, Question_type FROM QA";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int id = rs.getInt("Question_id");
                    String prompt = rs.getString("Question");
                    String answer = rs.getString("Answer");
                    String type = rs.getString("Question_type");

                    switch (type) {
                        case "SA" -> questions.add(new SAQuestion(prompt, answer, id));
                        case "TF" -> questions.add(new TFQuestion(prompt, answer, id));
                        case "MC" -> {
                            List<String> opts = optionsByQuestion.getOrDefault(id, List.of());
                            questions.add(new MCQuestion(
                                    prompt, answer, opts, id
                            ));
                        }
                        default -> throw new SQLException("Unknown question type: " + type);
                    }
                }
            }
        }

        return Collections.unmodifiableList(questions);
    }

    /**
     * Gets all rows from the table and groups them by question id
     * @param theConn a database connection
     * @return map to the ordered list of options for the question
     * @throws SQLException if the table cannot be reached
     */
    private Map<Integer, List<String>> loadOptions(Connection theConn) throws SQLException {
        Map<Integer, List<String>> result = new HashMap<>();
        String sql = "SELECT Question_id, Option_Text FROM options";

        try (PreparedStatement ps = theConn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int qid = rs.getInt("Question_id");
                String optionText = rs.getString("Option_Text");
                result.computeIfAbsent(qid, k -> new ArrayList<>()).add(optionText);
            }
        }

        return result;
    }
}