package entities.client;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class ClientAnswers {

    private final JSONObject clientAllAnswers;
    private final List<JSONObject> clientAnswer;

    public ClientAnswers() {
        clientAnswer = new ArrayList<>();
        clientAllAnswers = new JSONObject();
    }

    public void addAnswer(String response, String score) {
        log.info("Saving the answer");
        JSONObject answer = new JSONObject();
        answer.put("result", score);
        answer.put("answer", response);
        clientAnswer.add(answer);
    }

    public void saveAnswers() {
        for (int i = 0; i < clientAnswer.size(); i++) {
            clientAllAnswers.put(String.valueOf(i + 1), clientAnswer.get(i));
        }
    }

    public JSONObject getClientAnswer() {
        return clientAllAnswers;
    }

}
