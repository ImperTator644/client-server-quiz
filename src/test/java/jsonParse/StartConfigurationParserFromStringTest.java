package jsonParse;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class StartConfigurationParserFromStringTest {
    private static final String PROPERTIES_FILE = "src/main/resources/start-properties.json";
    private StartConfigurationParserFromString startConfigurationParserFromString;

    @BeforeEach
    void setUp() throws IOException {
        InputStream inputStream = new FileInputStream(PROPERTIES_FILE);
        startConfigurationParserFromString = new StartConfigurationParserFromString(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
    }

    @Test
    public void should_return_time_8000 () {
        assertEquals(startConfigurationParserFromString.getAnswerTime(), 8000);
    }
    @Test
    public void should_return_name_KOLOKWIUM_1 () {
        assertEquals(startConfigurationParserFromString.getQuizName(), "KOLOKWIUM_1");
    }
}