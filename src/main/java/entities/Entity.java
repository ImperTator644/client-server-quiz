package entities;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

@Slf4j
public abstract class Entity {
    protected final Socket socket;
    protected BufferedReader reader;
    protected BufferedWriter writer;

    public Entity(Socket socket){
        this.socket = socket;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException ioException) {
            log.error("Error getting reader and writer {}", ioException.getMessage());
            closeEverything();
        }
    }

    protected void closeEverything() {
        try {
            if (this.reader != null) reader.close();
            if (this.writer != null) writer.close();
            if (this.socket != null) socket.close();
        } catch (IOException ioException) {
            log.error("Error closing everything in client handler {}", ioException.getMessage());
        }
    }
}
