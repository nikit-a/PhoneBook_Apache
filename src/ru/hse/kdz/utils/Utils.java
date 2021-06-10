package ru.hse.kdz.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public final class Utils {
    /**
     * Проверка корректности имен
     *
     * @param strField имя
     * @return true-если поле корректно, иначе false
     */
    public static boolean checkCorrectFieldsName(String strField) {
        return !"".equals(strField) && strField.matches("[a-zA-ZА-ЯЁа-яё]+");
    }

    /**
     * Проверка номеров
     *
     * @param strNumber номер
     * @return true-если поле корректно, иначе false
     */
    public static boolean checkNumbers(String strNumber) {
        return strNumber.matches("[0-9]+") && strNumber.length() == 11;
    }

    /**
     * Установка красного стиля border для поля
     *
     * @param currentField текущее поле
     */
    public static void showErrorHint(TextField currentField) {
        currentField.setText("");
        currentField.setStyle("-fx-border-color:red");
    }

    /**
     * Восстановление дефолтного стиля border для поля
     *
     * @param currentField текущее поле
     */
    public static void restoreBorder(TextField currentField) {
        currentField.setStyle("");
    }

    /**
     * Создание Alert
     *
     * @param alert       alert
     * @param infoMessage сообщение для информирования пользователя
     * @param placeError  место где возникла ошибка
     */
    public static void createAlert(Alert alert, String infoMessage, String placeError) {
        alert.setTitle(placeError);
        alert.setHeaderText(null);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }

    /**
     * Установка размеров для stage
     *
     * @param stage     stage
     * @param maxHeight максимальная высота
     * @param maxWidth  максимальная ширина
     * @param minHeight минимальная высота
     * @param minWidth  минимальная ширина
     */
    public static void setSizeWindow(Stage stage, double maxHeight, double maxWidth,
                                     double minHeight, double minWidth) {
        stage.setMaxHeight(maxHeight);
        stage.setMaxWidth(maxWidth);
        stage.setMinHeight(minHeight);
        stage.setMinWidth(minWidth);
    }

}
