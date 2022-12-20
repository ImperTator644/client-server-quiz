package entities.client;

import entities.MainInterface;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;

@Slf4j
public class ClientMain implements MainInterface {
    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket(PROXY, PORT_NUMBER);
        } catch (IOException e) {
            log.error("Error creating a socket for client {}", e.getMessage());
        }
        Client client = new Client(socket);
        client.handleQuestions();
    }
}
