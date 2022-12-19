package entities.question;

import lombok.Getter;

public class QuestionDataBase {

    @Getter
    private final String[] questions = new String[]{"Pytanie1", "Pytanie2", "Pytanie3"};

    private static QuestionDataBase instance = null;

    private QuestionDataBase(){}

    public static QuestionDataBase getInstance(){
        if(instance == null){
            instance = new QuestionDataBase();
        }
        return instance;
    }
}
