package database;

import constances.DBConstance;
import entities.QuestionDTO;
import jsonParse.question.Questions;

import java.sql.*;

import static constances.DBConstance.*;

public class QuizDatabase {
    private Questions questions;

    public QuizDatabase() {
        questions = Questions.getInstance();
        try {
            Class.forName(DBConstance.DBDRIVER).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAllQuestions() {
        try (Connection connection = DriverManager.getConnection(DBURL_WITH_NAME, DBUSER, DBPASS); Statement statement = connection.createStatement()) {
            String sql = "USE QUIZ";
            statement.execute(sql);
            for (int i = 0; i < questions.getQuestionsCount(); i++) {
                sql = "INSERT INTO questions(id_pytania, pytanie, odp1, odp2, odp3, odp4, correct) " +
                        "values (" + i + ",'" + questions.getText(i) + "','" + questions.getAnswers(i)[0] + "','" + questions.getAnswers(i)[1] + "','" + questions.getAnswers(i)[2] + "','" + questions.getAnswers(i)[3] + "','" + questions.getCorrectAnswer(i) + "')";
                statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public QuestionDTO getQuestion(int questionId) {
        try (Connection connection = DriverManager.getConnection(DBURL_WITH_NAME, DBUSER, DBPASS); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM questions q WHERE q.id_pytania=" + questionId);
            QuestionDTO questionDTO = new QuestionDTO();
            while(resultSet.next()){
                questionDTO.setQuestion(resultSet.getString("pytanie"));
                questionDTO.setOdp1(resultSet.getString("odp1"));
                questionDTO.setOdp2(resultSet.getString("odp2"));
                questionDTO.setOdp3(resultSet.getString("odp3"));
                questionDTO.setOdp4(resultSet.getString("odp4"));
                questionDTO.setCorrect(resultSet.getInt("correct"));
            }
            return  questionDTO;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addAnswer(int idPytania, int idStudenta, String odpowiedz, boolean isCorrect){
        try (Connection connection = DriverManager.getConnection(DBURL_WITH_NAME, DBUSER, DBPASS); Statement statement = connection.createStatement()) {
            String sql = "INSERT INTO answers (answers.id_pytania, answers.id_studenta, answers.student_answer, answers.correct)" +
                    "VALUES (" + idPytania + "," + idStudenta + ",'" + odpowiedz + "'," + isCorrect + ")";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
