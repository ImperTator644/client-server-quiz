package udp.client;

import jsonParse.StartConfigurationParserFromString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

@Slf4j
public class UDPClient {
    private DatagramSocket socket;
    private InetAddress address;
    private final Scanner scanner;
    private final StartConfigurationParserFromString startConf;

    private byte[] buf = new byte[256];

    public UDPClient() {
        startConf = receivePropertiesFromJsonString();
        scanner = new Scanner(System.in);
        try {
            socket = new DatagramSocket(4445);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private StartConfigurationParserFromString receivePropertiesFromJsonString(){
        return new StartConfigurationParserFromString(clientRead());
    }

    public void handleQuestions(){
        log.info("Got into handleQuestion method");
        long messageReceivedTime = 0;
        while(socket.isConnected()){
            System.out.println(clientRead());
            messageReceivedTime = System.currentTimeMillis();
            log.info("Question received");
            try{
                while(System.currentTimeMillis() - messageReceivedTime < startConf.getAnswerTime()) {
                    if(System.in.available() > 0) {
                        clientSend(scanner.nextLine());
                        break;
                    }
                }
                clientSend("\n");
            }
            catch (IOException e){
                log.error("Error sending answer {}", e.getMessage());
            }
        }
    }

    private String clientRead() {
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
        return currentMessage;
    }

    private void clientSend(String msg) {
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
