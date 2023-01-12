package udp.client;

import jsonParse.StartConfigurationParserFromString;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

@Slf4j
public class UDPClient {
    private DatagramSocket socket;
    private final Scanner scanner;
    private StartConfigurationParserFromString startConf;
    private Integer sendPort;

    private byte[] buf = new byte[256];
    private static final String SERVER_PATH = "src/main/resources/udp-server-properties.json";
    private static final String CLIENT_PATH = "src/main/resources/udp-client-properties.json";
    static JSONObject port = new JSONObject();

    public UDPClient(Integer port) {
        setUpProperties();
        scanner = new Scanner(System.in);
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        setupSendPort();
    }

    private void setUpProperties() {
        port.clear();
        port.put("port", 32767);
        try (PrintWriter out = new PrintWriter(new FileWriter(CLIENT_PATH))) {
            out.write(port.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupSendPort() {
        try {
            port = new JSONObject(String.valueOf(Files.readString(Paths.get(SERVER_PATH))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sendPort = port.getInt("port");
        sendPort++;
    }

    private StartConfigurationParserFromString receivePropertiesFromJsonString(){
        String readedValue = clientRead();
        return new StartConfigurationParserFromString(readedValue);
    }

    public void handleQuestions(){
        log.info("Got into handleQuestion method");
        clientSendStart();

        startConf = receivePropertiesFromJsonString();
        long messageReceivedTime = 0;
        while(!socket.isClosed()){
            System.out.println(clientRead());
            messageReceivedTime = System.currentTimeMillis();
            log.info("Question received");
            try{
                while(System.currentTimeMillis() - messageReceivedTime < startConf.getAnswerTime()) {
                    if(System.in.available() > 0) {
                        String fromConsole = scanner.nextLine();
                        clientSend(fromConsole);
                        while (!clientRead().contains("ACK")) {
                            clientSend(fromConsole);
                        }
                        break;
                    }
                }
            }
            catch (IOException e){
                log.error("Error sending answer {}", e.getMessage());
            }
        }
    }

    private String clientRead() {
        buf = new byte[256];
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
        log.debug("Client received message: {}", currentMessage);
        return currentMessage.trim();
    }

    private void clientSend(String msg) {
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
            log.debug("Client send message: {}", msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void clientSendStart() {
        DatagramPacket packet;


        buf = "start".getBytes();
        InetAddress address;
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        int port = 4444;
        packet = new DatagramPacket(buf, buf.length, address, port);
        try {
            socket.send(packet);
            log.info("Client send message: {}", "start");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
