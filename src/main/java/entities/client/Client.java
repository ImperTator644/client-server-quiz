package entities.client;

import entities.Entity;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

@Slf4j
public class Client extends Entity {

    private Scanner scanner;

    public Client(Socket socket){
        super(socket);
        scanner = new Scanner(System.in);
    }

    public void handleQuestion(){
        log.info("Got into handleQuestion method");
        long messageReceivedTime = 0;
        while(socket.isConnected()){
            try{
                System.out.println(reader.readLine());
                messageReceivedTime = System.currentTimeMillis();
                log.info("Question received");
            }
            catch (IOException e){
                log.error("Error reading question {}", e.getMessage());
                closeEverything();
            }
            try{
                while(System.currentTimeMillis() - messageReceivedTime < 5000) {
                    if(System.in.available() > 0) {
                        writer.write(scanner.nextLine());
                        break;
                    }
                }
                writer.newLine();
                writer.flush();
            }
            catch (IOException e){
                log.error("Error sending answer {}", e.getMessage());
                closeEverything();
            }
        }
    }

    @Override
    protected void closeEverything(){
        super.closeEverything();
        scanner.close();
    }
}
