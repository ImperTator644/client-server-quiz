package entities.server;

import constances.DBConstance;
import entities.MainInterface;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executors;

@Slf4j
public class ServerMain implements MainInterface {

    public static void main(String[] args) {
        prepareDatabase();
        try {
            ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
            Server server = new Server(serverSocket, Executors.newFixedThreadPool(THREAD_NUMBER));
            server.startServer();
        } catch (IOException e) {
            log.error("Problem creating a server {}", e.getMessage());
        }
    }

    private static void prepareDatabase() {
        try {
            Class.forName(DBConstance.DBDRIVER).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try(Connection conn = DriverManager.getConnection(DBConstance.DBURL_WITHOUT_NAME, DBConstance.DBUSER, DBConstance.DBPASS);
            Statement stmt = conn.createStatement();
        ) {
            String sql = "DROP DATABASE IF EXISTS QUIZ";
            stmt.executeUpdate(sql);
            log.info("Database dropped successfully...");
            sql = "CREATE DATABASE QUIZ";
            stmt.executeUpdate(sql);
            sql = "USE QUIZ";
            stmt.execute(sql);
            sql = "CREATE TABLE questions (id_pytania int primary key, pytanie text, odp1 varchar(50), odp2 varchar(50), odp3 varchar(50), odp4 varchar(50), correct int)";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE answers (id_studenta int, id_pytania int, student_answer varchar(50), correct tinyint, primary key (id_studenta, id_pytania))";
            stmt.executeUpdate(sql);
            log.info("Database created successfully...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
