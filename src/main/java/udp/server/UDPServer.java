package udp.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

@Slf4j
public class UDPServer extends Thread {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private String currentMessage;
    private final ExecutorService executorService;

    public UDPServer(ExecutorService executorService) {
        this.executorService = executorService;
        try {
            socket = new DatagramSocket(4445);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        running = true;

        //Utworzyc klase ServerQuizService, przekazac do niej jako konstruktor socket
        //uzyc w srodku socketa do wysylania i odbierania danych
        //uzyc executor service

        while (!socket.isClosed()) {
            UDPServerQuizService serverQuizService = new UDPServerQuizService(socket);

            executorService.execute(serverQuizService);
        }

        socket.close();
    }

    private void closeServer() {
        socket.close();
    }


}
