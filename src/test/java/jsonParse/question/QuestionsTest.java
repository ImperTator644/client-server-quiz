package jsonParse.question;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuestionsTest {
    private Questions questions;

    @BeforeEach
    void setUp() {
        questions = Questions.getInstance();
    }

    @Test
    void should_return_text(){
        assertEquals(questions.getText(0), "Jaka jest najdluzsza rzeka w Polsce?");
        assertEquals(questions.getText(1), "Jaki jest najwyzszy szczyt swiata?");
    }

    @Test
    void should_return_answers(){
        assertAll(
                () -> assertEquals(questions.getAnswers(0)[0], "Odra"),
                () -> assertEquals(questions.getAnswers(0)[1], "Wisla"),
                () -> assertEquals(questions.getAnswers(0)[2], "Bug"),
                () -> assertEquals(questions.getAnswers(0)[3], "Radomka")
        );
        assertAll(
                () -> assertEquals(questions.getAnswers(1)[0], "Rysy"),
                () -> assertEquals(questions.getAnswers(1)[1], "Mont Blanc"),
                () -> assertEquals(questions.getAnswers(1)[2], "Mount Everest"),
                () -> assertEquals(questions.getAnswers(1)[3], "K2")
        );
    }

    @Test
    void should_return_index_of_correct_answer(){
        assertEquals(questions.getCorrectAnswer(0), 2);
        assertEquals(questions.getCorrectAnswer(1), 3);
    }
}