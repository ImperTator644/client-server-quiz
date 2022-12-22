package jsonParse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StartConfigurationParserToStringTest {
    private static final String JSON_STRING = "{\"answer-time\":\"8000\",\"quiz-name\":\"KOLOKWIUM_1\"}";
    private StartConfigurationParserToString startConfigurationParserToString;

    @BeforeEach
    void setUp() {
        startConfigurationParserToString = StartConfigurationParserToString.getInstance();
    }

    @Test
    public void should_return_string_from_json(){
        assertEquals(startConfigurationParserToString.getJsonString(), JSON_STRING);
    }
}