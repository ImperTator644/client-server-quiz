package entities.client;

import entities.Entity;
import jsonParse.StartConfigurationParserFromString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

@Slf4j
public class Client extends Entity {
    private final Scanner scanner;
    private final StartConfigurationParserFromString startConf;

    public Client(Socket socket){
        super(socket);
        startConf = receivePropertiesFromJsonString();
        log.info("Properties received {} and {}", Objects.requireNonNull(startConf).getAnswerTime(), startConf.getQuizName());
        System.out.println("Starting quiz " + startConf.getQuizName());
        scanner = new Scanner(System.in);
    }

    private StartConfigurationParserFromString receivePropertiesFromJsonString(){
        try{
            return new StartConfigurationParserFromString(reader.readLine());
        }
        catch (IOException e){
            log.error("Error reading question {}", e.getMessage());
            closeEverything();
            return null;
        }
    }

    public void handleQuestions(){
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
                while(System.currentTimeMillis() - messageReceivedTime < startConf.getAnswerTime()) {
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
