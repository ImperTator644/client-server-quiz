package udp.client;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UDPClientMain {

    static JSONObject port = new JSONObject();
    private static final String PATH = "src/main/resources/udp-client-properties.json";
    public static void main(String[] args) {
        try {
            port = new JSONObject(String.valueOf(Files.readString(Paths.get(PATH))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Integer intPort = port.getInt("port");
        if (intPort > 65534) {
            intPort = 32767;
        }
        UDPClient client = new UDPClient(intPort);
        port.clear();
        port.put("port", ++intPort);
        try (PrintWriter out = new PrintWriter(new FileWriter(PATH))) {
            out.write(port.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        client.handleQuestions();
    }
}
