package udp.server;

import java.util.concurrent.Executors;

public class UDPServerMain {

    public static void main(String[] args) {
        UDPServer udpServer = new UDPServer(Executors.newFixedThreadPool(10));
        udpServer.run();
    }
}
