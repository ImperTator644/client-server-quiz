package jsonParse.question;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class QuestionDataBase {

    private static final String QUESTIONS_FILE = "src/main/resources/questions.json";
    private final List<JSONObject> questions;
    private static QuestionDataBase instance = null;

    private QuestionDataBase() {
        JSONObject wholeFile = readJsonFromFile();
        questions = new ArrayList<>();
        fillQuestionList(Objects.requireNonNull(wholeFile));
    }

    public static QuestionDataBase getInstance() {
        if (instance == null) {
            instance = new QuestionDataBase();
        }
        return instance;
    }

    private JSONObject readJsonFromFile() {
        try {
            return new JSONObject(Objects.requireNonNull(Files.readAllLines(Paths.get(QUESTIONS_FILE))
                    .stream()
                    .map(String::trim)
                    .reduce(String::concat)
                    .orElse(null)));
        } catch (IOException e) {
            log.error("Problem with input stream {}", e.getMessage());
            return null;
        }
    }

    private void fillQuestionList(JSONObject file) {
        for (int i = 0; i < file.getInt("size"); i++) {
            questions.add(file.getJSONObject(Integer.toString(i+1)));
            log.info("Question added to list {}", questions.get(i));
        }
    }

    public String getText(int questionNumber){
        return questions
                .get(questionNumber)
                .getString("text");
    }

    public String[] getAnswers(int questionNumber){
        return toStringArray(questions
                .get(questionNumber)
                .getJSONArray("answers"));
    }

    public int getCorrectAnswer(int questionNumber){
        return questions
                .get(questionNumber)
                .getInt("correct");
    }

    public int getQuestionsCount(){
        return questions.size();
    }

    private String[] toStringArray(JSONArray array) {
        if(array==null)
            return new String[0];
        String[] arr=new String[array.length()];
        for(int i=0; i<arr.length; i++) {
            arr[i]=array.optString(i);
        }
        return arr;
    }


}
