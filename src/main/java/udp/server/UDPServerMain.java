package udp.server;

import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Executors;

public class UDPServerMain {
    static JSONObject port = new JSONObject();
    private static final String PATH = "src/main/resources/udp-server-properties.json";
    public static void main(String[] args) {
        setUpProperties();
        UDPServer udpServer = new UDPServer(Executors.newFixedThreadPool(10));
        udpServer.run();
    }

    private static void setUpProperties() {
        port.clear();
        port.put("port", 1);
        try (PrintWriter out = new PrintWriter(new FileWriter(PATH))) {
            out.write(port.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
