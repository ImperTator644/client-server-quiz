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
    private byte[] buf = new byte[256];
    private final ExecutorService executorService;

    public UDPServer(ExecutorService executorService) {
        this.executorService = executorService;
        try {
            socket = new DatagramSocket(4444);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {

        while (!socket.isClosed()) {
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
            String received = new String(packet.getData(), 0, packet.getLength());
            log.info("Server received message: {}", received);
            if (received.contains("start")) {
                UDPServerQuizService serverQuizService = new UDPServerQuizService();

                executorService.execute(serverQuizService);
                log.info("Executor started client {}", serverQuizService);
            }
        }


        socket.close();
    }
}
