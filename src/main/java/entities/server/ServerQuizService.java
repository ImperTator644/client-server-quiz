package entities.server;

import entities.Entity;
import jsonParse.answer.Answers;
import entities.client.ClientAnswers;
import jsonParse.question.QuestionDataBase;
import jsonParse.StartConfigurationParserToString;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

@Slf4j
public class ServerQuizService extends Entity implements Runnable {
    private static final String DOUBLE_TAB = "\t\t";
    private final QuestionDataBase questions;
    private final ClientAnswers clientAnswers;
    private final Answers answers;
    private final String quizProperties;
    private final int id;
    private static int nextId = 0;
    private int score;

    public ServerQuizService(Socket socket) {
        super(socket);
        this.questions = QuestionDataBase.getInstance();
        this.answers = Answers.getInstance();
        this.clientAnswers = new ClientAnswers();
        quizProperties = StartConfigurationParserToString.getInstance()
                .getJsonString();
        this.id = nextId++;
        this.score = 0;
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
                for (String answer : questions.getAnswers(currentQuestion)) {
                    this.writer.write(iter++ + ". " + answer + DOUBLE_TAB);
                }
                this.writer.newLine();
                this.writer.flush();
                log.info("Message sent successfully {}", questions.getText(currentQuestion));
            } catch (IOException e) {
                log.error("Error sending question {}", e.getMessage());
                closeEverything();
            }
            try {
                clientResponse = this.reader.readLine();
                log.info("Received a message from client {}", clientResponse);
                checkAnswer(clientResponse, currentQuestion);
            } catch (IOException e) {
                log.error("Error getting response from client {}", e.getMessage());
                closeEverything();
            }
            currentQuestion++;
        }
        clientAnswers.saveAnswers();
        answers.addClientAnswers(clientAnswers, id);
        answers.saveFile();
    }

    private void sendQuizProperties() {
        try {
            this.writer.write(quizProperties);
            this.writer.newLine();
            this.writer.flush();
            log.info("Properties sent successfully {}", quizProperties);
        } catch (IOException e) {
            log.error("Error sending properties {}", e.getMessage());
            closeEverything();
        }
    }

    private void checkAnswer(String response, int questionNr) {
        log.info("Checking the answer");
        if (!response.equals("") && questions.getCorrectAnswer(questionNr) == Integer.parseInt(response)) {
            score++;
            clientAnswers.addAnswer(response, "correct");
        } else {
            clientAnswers.addAnswer(response, "incorrect");
        }
    }
}
