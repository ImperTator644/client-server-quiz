package jsonParse;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

@Slf4j
public class StartConfigurationParserFromString {
    private static final String ANSWER_TIME = "answer-time";
    private static final String QUIZ_NAME = "quiz-name";

    @Getter
    private final int answerTime;
    @Getter
    private final String quizName;
    private final JSONObject propertiesFile;

    public StartConfigurationParserFromString(String jsonString){
        propertiesFile = new JSONObject(jsonString);
        answerTime = propertiesFile.getInt(ANSWER_TIME);
        quizName = propertiesFile.getString(QUIZ_NAME);
    }
}
