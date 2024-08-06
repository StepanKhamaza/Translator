package ru.example.translator.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.example.translator.controller.TranslationController;
import ru.example.translator.entity.TranslatedData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class TranslationRepository {
    private static final Logger logger = LoggerFactory.getLogger(TranslationController.class);

    private static final String URL = "jdbc:postgresql://localhost:5432/database_name";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private static Connection connection;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }

        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public TranslationRepository() {
    }

    public void save(TranslatedData translatedData) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO " +
                            "translations(ip, text, translatedText) VALUES(?, ?, ?)");

            preparedStatement.setString(1, translatedData.getIp());
            preparedStatement.setString(2, translatedData.getText());
            preparedStatement.setString(3, translatedData.getTranslatedText());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public List<TranslatedData> findAll() {
        List<TranslatedData> list = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            String SQL = "SELECT * FROM translations";
            ResultSet resultSet = statement.executeQuery(SQL);

            while (resultSet.next()) {
                TranslatedData translatedData = new TranslatedData();

                translatedData.setIp(resultSet.getString("ip"));
                translatedData.setText(resultSet.getString("text"));
                translatedData.setTranslatedText(resultSet.getString("translatedText"));

                list.add(translatedData);
            }

        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return list;
    }

    public TranslatedData findById(long id) {
        TranslatedData translatedData = null;
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("SELECT * FROM translations WHERE id=?");

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                translatedData = new TranslatedData();
                translatedData.setId(resultSet.getLong("id"));
                translatedData.setIp(resultSet.getString("ip"));
                translatedData.setText(resultSet.getString("text"));
                translatedData.setTranslatedText(resultSet.getString("translatedText"));
            }

        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return translatedData;
    }

    public void update(long id, TranslatedData translatedData) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("UPDATE translations " +
                            "SET ip=?, text=?, translatedText=? WHERE ID=?");

            preparedStatement.setString(1, translatedData.getIp());
            preparedStatement.setString(2, translatedData.getText());
            preparedStatement.setString(3, translatedData.getTranslatedText());
            preparedStatement.setLong(4, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public void delete(long id) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("DELETE FROM translations WHERE id=?");

            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }
}
