package ru.hse.kdz.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import ru.hse.kdz.utils.Utils;


public class InfoAboutMeController {
    @FXML
    public Button likeButton;

    @FXML
    public void initialize() {
        likeButton.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            try {
                // Находим картинку в ресурсах
                Image imageOk = new Image(getClass().getResourceAsStream("/images/likeButton.jpg"));
                // Создаем ImageView
                ImageView imageView = new ImageView(imageOk);
                imageView.setFitHeight(100);
                imageView.setFitWidth(50);
                // Устаналиваем картинку у кнопки
                likeButton.graphicProperty().setValue(imageView);
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                Utils.createAlert(alert, "Ошибка при добавлении изображения", ex.getMessage());
            }

        });
    }
}
