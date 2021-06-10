package ru.hse.kdz.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.hse.kdz.model.Contact;
import ru.hse.kdz.model.PhoneBook;
import ru.hse.kdz.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RootController {

    @FXML
    public TableView<Contact> tableView;
    @FXML
    public TextField textFieldSearch;
    @FXML
    public Button btnSearch;
    @FXML
    public MenuButton menuBtnFile, menuBtnSettings;
    @FXML
    public Button menuBtnHelp;
    @FXML
    public MenuItem itemRemoveContact, itemEditContact, itemCreateContact, itemExit;
    @FXML
    public MenuItem itemImport, itemExport;

    private Stage rootStage;
    private final Stage contactStage;
    private final Stage infoStage;
    public static ContactController contactController;
    private static PhoneBook phoneBook;
    private final FileChooser fileChooser = new FileChooser();

    public RootController() throws IOException {
        // Создание нового модального окна контакта
        contactStage = new Stage();
        contactStage.initModality(Modality.WINDOW_MODAL);
        Parent parentContact = FXMLLoader.load(getClass().getResource("/layout/contact.fxml"));
        Scene parentScene = new Scene(parentContact);
        // Устанавливаем сцену с fxml файла
        contactStage.setScene(parentScene);
        // Создание нового модального окна с информацией
        infoStage = new Stage();
        infoStage.initModality(Modality.WINDOW_MODAL);
        Parent parentInfo = FXMLLoader.load(getClass().getResource("/layout/infoAboutMe.fxml"));
        Scene sceneInfo = new Scene(parentInfo);
        // Устанавливаем сцену с fxml файла
        infoStage.setScene(sceneInfo);
        // Устанавливаем допустимые расширения для импорта и экспорта файлов
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("json", "*.json"));
    }

    /**
     * Инициализация таблички с контактами
     *
     * @param contacts список контактов
     */
    public void initContacts(ObservableList<Contact> contacts) {
        if (contacts != null) {
            tableView.setItems(contacts);
        }
    }

    /**
     * Инициализация главного Stage и назначение родителя для модальных окон
     *
     * @param rootStage корневой Stage
     */
    public void setRootStage(Stage rootStage) {
        this.rootStage = rootStage;
        contactStage.initOwner(this.rootStage);
        infoStage.initOwner(this.rootStage);
    }

    /**
     * Инициализация телефонной книги в контроллере таблички
     *
     * @param phoneBook телефонная книга
     */
    public void setPhoneBook(PhoneBook phoneBook) {
        RootController.phoneBook = phoneBook;
        contactController.setPhoneBook(phoneBook);
    }

    @FXML
    public void initialize() {
        // Событие при нажатии на кнопку создания контакта
        itemCreateContact.setOnAction(mouseEvent -> {
            contactController.labelContact.setText("Создание нового контакта");
            // Создание нового контакта с пустыми полями
            contactController.initContactWindowDetails(new Contact());
            // Устанавливаем размеры для окна
            Utils.setSizeWindow(contactStage, 600, 1000, 400, 600);
            contactStage.showAndWait();
            // Если контакт был создан записываем в телефонную книгу
            if (contactController.getContact() != null) {
                try {
                    phoneBook.addContact(contactController.getContact());
                    tableView.setItems(FXCollections.observableList(phoneBook.getAllContacts()));
                    tableView.refresh();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            // Так как уже записали контакт делаем его null
            contactController.setNullContact();
        });
        // Событие при нажатии на кнопку редактирования контакта
        itemEditContact.setOnAction(mouseEvent -> {
            contactController.labelContact.setText("Редактирование существующего контакта");
            // Получаем индекс выделенной строки
            int indexEditContact = tableView.getSelectionModel().getSelectedIndex();
            Contact editContact = null;
            // Если мы удаляем из найденных по поиску контактов (берем по индексу из списка отфильтрованных)
            if (phoneBook.getContactFilteredList() != null) {
                editContact = phoneBook.getContactFilteredList().get(indexEditContact);
                contactController.initContactWindowDetails(editContact);

            } else {
                try {
                    editContact = phoneBook.getAllContacts().get(indexEditContact);
                    contactController.initContactWindowDetails(editContact);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            // Устанавливаем размеры для окна
            Utils.setSizeWindow(contactStage, 600, 1000, 400, 600);
            contactStage.showAndWait();
            // Если контакт был изменен меняем в телефонной книге
            if (contactController.getContact() != null) {
                try {
                    if (editContact != null) {
                        phoneBook.changeContact(contactController.getContact(), editContact);
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            try {
                tableView.setItems(FXCollections.observableList(phoneBook.getAllContacts()));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            tableView.refresh();
            // Так как уже перезаписали контакт делаем его null
            contactController.setNullContact();
        });
        // Событие при нажатии на кнопку удаления контакта
        itemRemoveContact.setOnAction(mouseEvent -> {
            int indexRemoveContact = tableView.getSelectionModel().getSelectedIndex();
            Contact removeContact;
            if (phoneBook.getContactFilteredList() != null) {
                removeContact = phoneBook.getContactFilteredList().get(indexRemoveContact);
                try {
                    phoneBook.deleteContact(removeContact);
                    ArrayList<Contact> contacts = new ArrayList<>(phoneBook.getContactFilteredList());
                    contacts.remove(removeContact);
                    tableView.setItems(FXCollections.observableList(contacts));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else {
                try {
                    removeContact = phoneBook.getAllContacts().get(indexRemoveContact);
                    phoneBook.deleteContact(removeContact);
                    tableView.setItems(FXCollections.observableList(phoneBook.getAllContacts()));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            tableView.refresh();
        });
        // При нажатии на кнопку выход , выходим из приложения
        itemExit.setOnAction(mouseEvent -> rootStage.close());
        // При нажатии на кнопку справки открываем соответствующее модальное окно
        menuBtnHelp.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> infoStage.showAndWait());

        // Событие при нажатии на кнопку экспорта контактов
        itemExport.setOnAction(mouseEvent -> {
            // Определяем текущий Stage
            Stage currentStage = (Stage) menuBtnFile.getScene().getWindow();
            File file = fileChooser.showSaveDialog(currentStage);
            // Если путь был выбран
            if (file != null) {
                try {
                    // Пытаемся выгрузить контакты в файл
                    phoneBook.exportContacts(file);
                } catch (IOException | SQLException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    Utils.createAlert(alert, ex.getMessage(), "Ошибка при экспорте контактов");
                }
            }
        });
        // Событие при нажатии на кнопку экспорта контактов
        itemImport.setOnAction(mouseEvent -> {
            Stage currentStage = (Stage) menuBtnFile.getScene().getWindow();
            File file = fileChooser.showOpenDialog(currentStage);
            // Если путь был выбран
            if (file != null) {
                try {
                    // Попытка импорта контактов из файла в таблицу
                    phoneBook.importContacts(file);
                } catch (IOException | SQLException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    Utils.createAlert(alert, ex.getMessage(), "Ошибка при импорте контактов");
                }
            }
            try {
                tableView.setItems(FXCollections.observableList(phoneBook.getAllContacts()));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            tableView.refresh();
        });
        // Событие, прослушивающее выделение строк таблицы
        tableView.getSelectionModel().selectedItemProperty().addListener((observableValue, contact, t1) -> {
            // если не выделена ни одна строка
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                // кнопка редактирование не активна
                itemEditContact.setDisable(false);
                // кнопка удаления не активна
                itemRemoveContact.setDisable(false);
                // если выделена какая-то строка
            } else {
                // кнопка редактирование активна
                itemEditContact.setDisable(true);
                // кнопка удаления активна
                itemRemoveContact.setDisable(true);
            }
        });
        // Событие, срабатывающее при нажатии на кнопку поиска или Enter
        btnSearch.setOnAction(mouseEvent -> {
            try {
                filter(textFieldSearch.getText());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        textFieldSearch.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                try {
                    filter(textFieldSearch.getText());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }

    /**
     * Фильтрует контакты в таблице по введенному в строку поиска текста
     *
     * @param searchText введенная строка
     */
    public void filter(String searchText) throws SQLException {
        String text = searchText.toLowerCase();
        phoneBook.filterContacts(text);
        tableView.setItems(phoneBook.getContactFilteredList());
        tableView.refresh();
    }
}