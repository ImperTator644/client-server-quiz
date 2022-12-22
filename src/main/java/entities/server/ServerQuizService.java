package entities.server;

import entities.Entity;
import jsonParse.question.QuestionDataBase;
import jsonParse.StartConfigurationParserToString;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

@Slf4j
public class ServerQuizService extends Entity implements Runnable {
    private static final String DOUBLE_TAB = "\t\t";
    private final QuestionDataBase questions;
    private final String quizProperties;
    private final int id;
    private static int nextId = 0;

    public ServerQuizService(Socket socket) {
        super(socket);
        this.questions = QuestionDataBase.getInstance();
        quizProperties = StartConfigurationParserToString.getInstance()
                .getJsonString();
        this.id = nextId++;
    }

    @Override
    public void run() {
        int currentQuestion = 0;
        String clientResponse;
        sendQuizProperties();
        while (socket.isConnected() && currentQuestion != questions.getQuestionsCount()) {
            try {
                this.writer.write(questions.getText(currentQuestion) + DOUBLE_TAB);
                int iter = 1;
                for(String answer : questions.getAnswers(currentQuestion)){
                    this.writer.write(iter++ + ". " + answer + DOUBLE_TAB);
                }
                this.writer.newLine();
                this.writer.flush();
                log.info("Message sent succesfully {}", questions.getText(currentQuestion));
            } catch (IOException e) {
                log.error("Error sending question {}", e.getMessage());
                closeEverything();
            }
            try {
                clientResponse = this.reader.readLine();
                log.info("Received a message from client {}", clientResponse);
            } catch (IOException e) {
                log.error("Error getting response from client {}", e.getMessage());
                closeEverything();
            }
            currentQuestion++;
        }
    }
    private void sendQuizProperties() {
        try {
            this.writer.write(quizProperties);
            this.writer.newLine();
            this.writer.flush();
            log.info("Properties sent succesfully {}", quizProperties);
        } catch (IOException e) {
            log.error("Error sending properties {}", e.getMessage());
            closeEverything();
        }
    }
}
