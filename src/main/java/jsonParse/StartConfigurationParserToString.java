package jsonParse;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class StartConfigurationParserToString {
    private static final String PROPERTIES_FILE = "src/main/resources/start-properties.json";

    private static StartConfigurationParserToString instance = null;
    @Getter
    private final String jsonString;

    private StartConfigurationParserToString (){
        jsonString = readJsonFromFile();
    }

    public static StartConfigurationParserToString getInstance() {
        if(instance == null){
            instance = new StartConfigurationParserToString();
        }
        return instance;
    }
    private String readJsonFromFile() {
        try {
            return Files.readAllLines(Paths.get(PROPERTIES_FILE))
                    .stream()
                    .map(String::trim)
                    .reduce(String::concat)
                    .orElse(null);
        } catch (IOException e) {
            log.error("Problem with input stream {}", e.getMessage());
            return null;
        }
    }
}
