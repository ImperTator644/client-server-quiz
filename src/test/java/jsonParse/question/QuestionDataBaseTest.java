package jsonParse.question;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuestionDataBaseTest {
    private QuestionDataBase questionDataBase;

    @BeforeEach
    void setUp() {
        questionDataBase = QuestionDataBase.getInstance();
    }

    @Test
    void should_return_text(){
        assertEquals(questionDataBase.getText(0), "Jaka jest najdluzsza rzeka w Polsce?");
        assertEquals(questionDataBase.getText(1), "Jaki jest najwyzszy szczyt swiata?");
    }

    @Test
    void should_return_answers(){
        assertAll(
                () -> assertEquals(questionDataBase.getAnswers(0)[0], "Odra"),
                () -> assertEquals(questionDataBase.getAnswers(0)[1], "Wisla"),
                () -> assertEquals(questionDataBase.getAnswers(0)[2], "Bug"),
                () -> assertEquals(questionDataBase.getAnswers(0)[3], "Radomka")
        );
        assertAll(
                () -> assertEquals(questionDataBase.getAnswers(1)[0], "Rysy"),
                () -> assertEquals(questionDataBase.getAnswers(1)[1], "Mont Blanc"),
                () -> assertEquals(questionDataBase.getAnswers(1)[2], "Mount Everest"),
                () -> assertEquals(questionDataBase.getAnswers(1)[3], "K2")
        );
    }

    @Test
    void should_return_index_of_correct_answer(){
        assertEquals(questionDataBase.getCorrectAnswer(0), 2);
        assertEquals(questionDataBase.getCorrectAnswer(1), 3);
    }
}