package model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for Question and its concrete subclasses, focusing on the
 * answer-checking rules (which differ by type) and the type/prompt accessors.
 * Pure model logic — no database required.
 */
class QuestionTest {


    @Test
    void baseCheckAnswerIsExactCaseSensitiveMatch() {
        Question base = new Question("prompt", "Yes", 1) {
            @Override
            public String getType() {
                return "base";
            }
        };
        assertTrue(base.checkAnswer("Yes"));
        assertFalse(base.checkAnswer("yes"), "base check is case-sensitive");
        assertFalse(base.checkAnswer(" Yes "), "base check does not trim");
    }

    @Test
    void shortAnswerIgnoresCaseAndTrimsWhitespace() {
        SAQuestion q = new SAQuestion("Capital of France?", "Paris", 2);
        assertTrue(q.checkAnswer("paris"));
        assertTrue(q.checkAnswer("  PARIS  "));
        assertFalse(q.checkAnswer("London"));
    }

    @Test
    void trueFalseIgnoresCaseButDoesNotTrim() {
        TFQuestion q = new TFQuestion("The sky is blue.", "True", 3);
        assertTrue(q.checkAnswer("true"));
        assertTrue(q.checkAnswer("TRUE"));
        assertFalse(q.checkAnswer(" true "));
        assertFalse(q.checkAnswer("False"));
    }

    @Test
    void multipleChoiceChecksAnswerAndExposesChoices() {
        List<String> choices = List.of("Red", "Green", "Blue");
        MCQuestion q = new MCQuestion("Pick a color", "Green", choices, 4);
        assertTrue(q.checkAnswer("  green  "));
        assertFalse(q.checkAnswer("Yellow"));
        assertEquals(choices, q.getChoices());
    }

    @Test
    void getTypeReportsTheConcreteKind() {
        assertEquals("Short Answer", new SAQuestion("p", "a", 1).getType());
        assertEquals("True/False", new TFQuestion("p", "a", 1).getType());
        assertEquals("Multiple Choices",
                new MCQuestion("p", "a", List.of("a", "b"), 1).getType());
    }

    @Test
    void getMyPromptReturnsThePrompt() {
        Question q = new SAQuestion("What is 2 + 2?", "4", 5);
        assertEquals("What is 2 + 2?", q.getMyPrompt());
    }
}
