package ru.hse.kdz;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.hse.kdz.controllers.RootController;
import ru.hse.kdz.model.PhoneBook;
import ru.hse.kdz.utils.SqlCommands;
import ru.hse.kdz.utils.Utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class AppFX extends Application {

    private PhoneBook phoneBook = null;

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("PhoneBook GUI");
            InputStream iconStream = getClass().getResourceAsStream("/images/phoneBook.jpg");
            Image image = new Image(iconStream);
            primaryStage.getIcons().add(image);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/table.fxml"));
            Parent root = loader.load();
            Scene rootScene = new Scene(root);
            primaryStage.setScene(rootScene);
            RootController rootController = loader.getController();
            rootController.setRootStage(primaryStage);
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            String dbURL = "jdbc:derby:phonebook;create=true";
            Connection connection = DriverManager.getConnection(dbURL);
            phoneBook = new PhoneBook(connection);
            try{
                Statement statement = connection.createStatement();
                statement.execute(SqlCommands.CREATE_TABLE);
                rootController.initContacts(new FilteredList<>(FXCollections.observableArrayList()));
            }catch (SQLException ignored){
                phoneBook.uploadInfoContacts();
                rootController.initContacts(new FilteredList<>(phoneBook.uploadInfoContacts()));
            }
            rootController.setPhoneBook(phoneBook);
            Utils.setSizeWindow(primaryStage, 1200, 2200, 600, 1100);
            primaryStage.show();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            Utils.createAlert(alert, ex.getMessage(), "Ошибка при открытии приложения");
        }
    }

    /**
     * Метод срабатывающий при закрытии приложения
     */
    @Override
    public void stop() throws SQLException {
        phoneBook.closeConnection();

    }


}