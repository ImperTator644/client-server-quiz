package entities.server;

import database.QuizDatabase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

@Slf4j
@RequiredArgsConstructor
public class  Server {

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private QuizDatabase quizDatabase = new QuizDatabase();

    public void startServer() {
        log.info("Server started");
        quizDatabase.saveAllQuestions();
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                log.info("New student has connected");

                ServerQuizService serverQuizService = new ServerQuizService(socket);

                executorService.execute(serverQuizService);
                log.info("Executor started client {}", serverQuizService);
            }
        }
        catch (IOException ioException){
            log.error("Error creating a connection with client {}", ioException.getMessage());
            closeEverything();
        }
    }

    private void closeEverything() {
        executorService.shutdown();
        try{
            serverSocket.close();
        }
        catch (IOException ioException){
            log.error("Error closing server socket {}", ioException.getMessage());
        }
    }
}
