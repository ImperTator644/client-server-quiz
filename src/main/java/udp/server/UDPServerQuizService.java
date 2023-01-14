package udp.server;

import entities.client.ClientAnswers;
import jsonParse.StartConfigurationParserToString;
import jsonParse.answer.Answers;
import jsonParse.question.Questions;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;

@Slf4j
public class UDPServerQuizService implements Runnable{

    private static final String DOUBLE_TAB = "\t\t";
    private final DatagramSocket socket;
    private final Questions questions;
    private final ClientAnswers clientAnswers;
    private final Answers answers;
    private final String quizProperties;
    private final int id;
    private static int nextId = 0;
    private int score;

    private byte[] buf = new byte[256];

    public UDPServerQuizService() {
        try {
            this.socket = new DatagramSocket(4446);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        this.questions = Questions.getInstance();
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
        while (!socket.isClosed() && currentQuestion != questions.getQuestionsCount()) {
            String questionWithAnswers;
            questionWithAnswers = (questions.getText(currentQuestion) + DOUBLE_TAB);
            int iter = 1;
            for (String answer : questions.getAnswers(currentQuestion)) {
                questionWithAnswers += (iter++ + ". " + answer + DOUBLE_TAB);
            }
            serverSend(questionWithAnswers);
            log.info("Message sent successfully {}", questions.getText(currentQuestion));
            clientResponse = serverRead();
            while (clientResponse.isBlank()) {
                clientResponse = serverRead();
            }
            serverSend("ACK");
            log.info("Received a message from client {}", clientResponse);
            checkAnswer(clientResponse.substring(0,1), currentQuestion);
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
        String currentMessage = new String(packet.getData(), 0, packet.getLength());
        log.debug("Server received message: {}", currentMessage);
        return currentMessage.trim();
    }

    private void serverSend(String msg) {
        DatagramPacket packet;

        buf = msg.getBytes();
        InetAddress address;
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        int port = 4445;
        packet = new DatagramPacket(buf, buf.length, address, port);
        try {
            socket.send(packet);
            log.debug("Server send message: {}", msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
