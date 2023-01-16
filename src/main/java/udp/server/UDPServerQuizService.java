package udp.server;

import entities.client.ClientAnswers;
import jsonParse.StartConfigurationParserToString;
import jsonParse.answer.Answers;
import jsonParse.question.Questions;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import udp.client.UDPClient;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    private Integer sendPort;

    private static final String CLIENT_PATH = "src/main/resources/udp-client-properties.json";

    private byte[] buf = new byte[256];
    static JSONObject port = new JSONObject();
    private static final String PATH = "src/main/resources/udp-server-properties.json";

    public UDPServerQuizService() {
        Integer port = getServerPort();

        try {
            this.socket = new DatagramSocket(port);
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

        setupSendPort();
    }

    private Integer getServerPort() {
        try {
            port = new JSONObject(String.valueOf(Files.readString(Paths.get(PATH))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Integer intPort = port.getInt("port");
        if (intPort > 32767) {
            intPort = 1;
        }
        port.clear();
        port.put("port", ++intPort);
        try (PrintWriter out = new PrintWriter(new FileWriter(PATH))) {
            out.write(port.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return intPort;
    }

    private void setupSendPort() {
        try {
            port = new JSONObject(String.valueOf(Files.readString(Paths.get(CLIENT_PATH))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sendPort = port.getInt("port");
        sendPort--;
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

        packet = new DatagramPacket(buf, buf.length, address, sendPort);
        try {
            socket.send(packet);
            log.debug("Server send message: {}", msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
