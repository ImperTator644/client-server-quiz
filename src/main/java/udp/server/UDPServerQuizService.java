package udp.server;

import entities.client.ClientAnswers;
import jsonParse.StartConfigurationParserToString;
import jsonParse.answer.Answers;
import jsonParse.question.QuestionDataBase;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@Slf4j
public class UDPServerQuizService implements Runnable{

    private static final String DOUBLE_TAB = "\t\t";
    private final DatagramSocket socket;
    private final QuestionDataBase questions;
    private final ClientAnswers clientAnswers;
    private final Answers answers;
    private final String quizProperties;
    private final int id;
    private static int nextId = 0;
    private int score;

    private byte[] buf = new byte[256];
    String currentMessage;

    public UDPServerQuizService(DatagramSocket socket) {
        this.socket = socket;
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
            serverSend(questions.getText(currentQuestion) + DOUBLE_TAB);
            int iter = 1;
            for (String answer : questions.getAnswers(currentQuestion)) {
                serverSend(iter++ + ". " + answer + DOUBLE_TAB);
            }
            serverSend("\n");
            log.info("Message sent successfully {}", questions.getText(currentQuestion));
            clientResponse = serverRead();
            log.info("Received a message from client {}", clientResponse);
            checkAnswer(clientResponse, currentQuestion);
            currentQuestion++;
        }
        clientAnswers.saveAnswers();
        answers.addClientAnswers(clientAnswers, id);
        answers.saveFile();

        serverSend("Your score is: " + score + "/" + questions.getQuestionsCount());
        serverSend("\n");
        log.info("Score sent successfully {}", score);
    }

    private void sendQuizProperties() {
        serverSend(quizProperties);
        serverSend("\n");
        log.info("Properties sent successfully {}", quizProperties);
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

    private String serverRead() {
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        packet = new DatagramPacket(buf, buf.length, address, port);
        currentMessage = new String(packet.getData(), 0, packet.getLength());
        return currentMessage;
    }

    private void serverSend(String msg) {
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        packet = new DatagramPacket(buf, buf.length, address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
