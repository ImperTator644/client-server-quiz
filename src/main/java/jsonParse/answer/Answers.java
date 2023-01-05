package jsonParse.answer;


import entities.client.ClientAnswers;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.FileWriter;

@Slf4j
public class Answers {
    private static final String ANSWERS_FILE = "src/main/resources/answers.json";
    private final JSONObject allAnswers;
    private static Answers instance = null;

    public Answers() {
        allAnswers = new JSONObject();
    }

    public static Answers getInstance() {
        if (instance == null) {
            instance = new Answers();
        }
        return instance;
    }

    public void addClientAnswers(ClientAnswers client, int idClient) {
        this.allAnswers.put(String.valueOf(idClient), client.getClientAnswer());
    }

    public void saveFile() {
        try {
            FileWriter file = new FileWriter(ANSWERS_FILE);
            allAnswers.write(file);
            log.info("Saving file {}", file);
            file.flush();
            file.close();
        } catch (Exception e) {
            log.error("Problem with saving file {}", e.getMessage());
        }
    }
}
