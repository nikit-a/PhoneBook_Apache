package ru.hse.kdz.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import ru.hse.kdz.model.Contact;
import ru.hse.kdz.model.PhoneBook;
import ru.hse.kdz.utils.Utils;

import java.sql.SQLException;
import java.time.LocalDate;

public class ContactController {

    @FXML
    public Label labelContact;
    @FXML
    public TextField fieldFirstName;
    @FXML
    public TextField fieldSecondName;
    @FXML
    public TextField fieldPatronymic;
    @FXML
    public TextField fieldMobilePhone;
    @FXML
    public TextField fieldHomePhone;
    @FXML
    public TextField fieldAddress;
    @FXML
    public DatePicker fieldBirthday;
    @FXML
    public TextArea fieldNotes;
    @FXML
    public Button btnCancel;
    @FXML
    public Button btnSave;

    private static PhoneBook phoneBook;
    private Contact contact;

    public Contact getContact() {
        return contact;
    }

    public void setNullContact() {
        contact = null;
    }

    /**
     * Инициализирует телефонную книгу в классе контроллера
     *
     * @param phoneBook телефонная книга
     */
    public void setPhoneBook(PhoneBook phoneBook) {
        ContactController.phoneBook = phoneBook;
    }

    @FXML
    public void initialize() {
        // Инициализирует контроллер контактов в контроллере таблицы
        RootController.contactController = this;
        // Прослушиватель фокуса у поля редактирования имени
        fieldFirstName.focusedProperty().addListener((ov, oldValue, newV) -> {
            // Фокус потерян
            if (!newV) {
                if (!Utils.checkCorrectFieldsName(fieldFirstName.getText())) {
                    Utils.showErrorHint(fieldFirstName);
                }
                // Фокус активен
            } else {
                Utils.restoreBorder(fieldFirstName);
            }
        });
        // Прослушиватель фокуса у поля редактирования фамилии
        fieldSecondName.focusedProperty().addListener((ov, oldValue, newV) -> {
            // Фокус потерян
            if (!newV) {
                if (!Utils.checkCorrectFieldsName(fieldSecondName.getText())) {
                    Utils.showErrorHint(fieldSecondName);
                }
                // Фокус активен
            } else {
                Utils.restoreBorder(fieldSecondName);
            }
        });
        // Прослушиватель фокуса у поля редактирования мобильного телефона
        fieldMobilePhone.focusedProperty().addListener((ov, oldValue, newV) -> {
            // Фокус потерян
            if (!newV) {
                if (!Utils.checkNumbers(fieldMobilePhone.getText())) {
                    Utils.showErrorHint(fieldMobilePhone);
                }
                // Фокус активен
            } else {
                Utils.restoreBorder(fieldMobilePhone);
            }
        });
        // Прослушиватель фокуса у поля редактирования домашнего телефона
        fieldHomePhone.focusedProperty().addListener((ov, oldValue, newV) -> {
            // Фокус потерян
            if (!newV) {
                if (!Utils.checkNumbers(fieldHomePhone.getText())) {
                    Utils.showErrorHint(fieldHomePhone);
                }
                // Фокус активен
            } else {
                Utils.restoreBorder(fieldHomePhone);
            }
        });
        // Событие нажатия на кнопку отмены контакта
        btnCancel.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> btnCancel.getScene().getWindow().hide());
        // Событие нажатия на кнопку сохранения контакта
        btnSave.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            String firstName = fieldFirstName.getText();
            String secondName = fieldSecondName.getText();
            String patronymic = fieldPatronymic.getText();
            String mobilePhone = fieldMobilePhone.getText();
            String homePhone = fieldHomePhone.getText();
            String address = fieldAddress.getText();
            LocalDate birthday = fieldBirthday.getValue();
            String note = fieldNotes.getText();
            // Если имя, фамилия и один из номеров непустые
            // (если строка неккоректная при смене фокуса она становится пустой)
            if (!firstName.isEmpty() && !secondName.isEmpty() &&
                    (!mobilePhone.isEmpty() || !homePhone.isEmpty())) {
                // Поле, отвечающее за возможность добавить в таблицу
                // Возможность пропадает если ФИО контактов совпадает
                boolean opportunityAdd = true;
                ObservableList<Contact> tableContacts = null;
                try {
                    tableContacts = phoneBook.getAllContacts();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                // Цикл по всем контактам таблицы
                if (tableContacts != null) {
                    for (Contact tableContact : tableContacts) {
                        if (tableContact.getFirstName().equals(firstName) &&
                                tableContact.getSecondName().equals(secondName) &&
                                tableContact.getPatronymic().equals(patronymic)) {
                            // Если нашелся хотя бы 1 контакт с таким же ФИО => нельзя добавлять в таблицу
                            opportunityAdd = false;
                            break;
                        }
                    }
                }
                // Создаем/редактируем контакт если такого нет в таблице и данные валидны
                if (opportunityAdd) {
                    contact = new Contact(firstName, secondName, patronymic, mobilePhone,
                            homePhone, address, birthday, note);
                    // Иначе создаем Alert, который оповещает пользователя о дублирующихся контактах
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    Utils.createAlert(alert, secondName + " " + firstName + " " + patronymic
                            , "Такой контакт уже существует");
                }
                btnSave.getScene().getWindow().hide();
                // Если создаем/редактируем контакт с невалидными данными
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                Utils.createAlert(alert, "Наводя на поля, будет выведена соответствующая " +
                                "\n подсказка о валидном вводе в текущее поле",
                        "Ошибка в вводе данных о контакте");
                if (firstName.isEmpty()) {
                    Utils.showErrorHint(fieldFirstName);
                }
                if (secondName.isEmpty()) {
                    Utils.showErrorHint(fieldSecondName);
                }
                if (mobilePhone.isEmpty() && homePhone.isEmpty()) {
                    Utils.showErrorHint(fieldMobilePhone);
                    Utils.showErrorHint(fieldHomePhone);
                }
            }

        });


    }

    /**
     * Инициализация контакта и стилей его границ
     *
     * @param contact Контакт с пустыми полями, если создаем,
     *                с текущими заполненными, если редактируем
     */
    public void initContactWindowDetails(Contact contact) {
        Utils.restoreBorder(fieldFirstName);
        Utils.restoreBorder(fieldSecondName);
        Utils.restoreBorder(fieldMobilePhone);
        Utils.restoreBorder(fieldHomePhone);
        fieldFirstName.setText(contact.getFirstName());
        fieldSecondName.setText(contact.getSecondName());
        fieldPatronymic.setText(contact.getPatronymic());
        fieldMobilePhone.setText(contact.getMobilePhone());
        fieldHomePhone.setText(contact.getHomePhone());
        fieldAddress.setText(contact.getAddress());
        fieldNotes.setText(contact.getComment());
        fieldBirthday.setValue(contact.getBirthday());

    }
}
