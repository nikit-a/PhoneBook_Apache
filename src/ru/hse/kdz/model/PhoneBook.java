package ru.hse.kdz.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import ru.hse.kdz.utils.SqlCommands;
import ru.hse.kdz.utils.Utils;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhoneBook {
    private FilteredList<Contact> contactFilteredList;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Connection connection;


    public PhoneBook(Connection connection) {
        this.connection = connection;
    }

    /**
     * Подгрузка контактов с базы данных
     *
     * @return весь полученный список контактов
     * @throws SQLException в случае неудачного извлечения
     */
    public ObservableList<Contact> uploadInfoContacts() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SqlCommands.GET_ALL_CONTACTS);
        List<Contact> contacts = new ArrayList<>();
        // Проходим по всем результатам и сохраняем.
        while (resultSet.next()) {
            LocalDate date = null;
            if (resultSet.getDate("birthday") != null) {
                date = resultSet.getDate("birthday").toLocalDate();
            }
            Contact contact = new Contact(resultSet.getString("firstName"),
                    resultSet.getString("secondName"),
                    resultSet.getString("patronymic"),
                    resultSet.getString("mobilePhone"),
                    resultSet.getString("homePhone"),
                    resultSet.getString("address"),
                    date,
                    resultSet.getString("comment"));
            contacts.add(contact);
        }
        resultSet.close();
        return FXCollections.observableArrayList(contacts);
    }

    /**
     * Запись в файл контактов
     *
     * @param file файл, куда записывать контакты
     * @throws IOException исключение, возникающее при работе с файлом
     */
    public void exportContacts(File file) throws IOException, SQLException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, getAllContacts());
    }

    /**
     * Загрузка контактов со стороннего файла
     *
     * @param file сам файл
     * @throws IOException исключение, возникающее при работе с файлом
     */
    public void importContacts(File file) throws IOException, SQLException {
        StringBuilder sb = new StringBuilder();
        List<Contact> contactsImport = mapper.readValue(file, new TypeReference<>() {
        });
        PreparedStatement preparedStatement = connection
                .prepareStatement(SqlCommands.CHECK_NAME_CONTACT_CREATE);
        addLegalContacts(contactsImport, sb, preparedStatement);
        if (sb.length() > 0) {
            Utils.createAlert(new Alert(Alert.AlertType.WARNING),
                    sb.toString(), "В файле контакт(ы) неккоретны или содержатся в таблице");
        }
    }

    /**
     * Добавляем только контакты прошедшее валидацию и проверку на дубликаты
     *
     * @param checkContacts список для проверки контактов перед добавлением
     * @param sb            Стрингбилдер для записи ошибок
     */
    public void addLegalContacts(List<Contact> checkContacts, StringBuilder sb, PreparedStatement prepState) throws SQLException {
        for (Contact contact : checkContacts) {
            prepState.setString(1, contact.getFirstName());
            prepState.setString(2, contact.getSecondName());
            prepState.setString(3, contact.getPatronymic());
            if (containsContact(prepState)) {
                sb.append("Контакт ")
                        .append(contact.getSecondName())
                        .append(" ").append(contact.getFirstName())
                        .append(" ").append(contact.getPatronymic())
                        .append(" уже есть в базе данных!\n");
            } else if (Utils.checkCorrectFieldsName(contact.getFirstName()) &&
                    Utils.checkCorrectFieldsName(contact.getSecondName()) &&
                    (Utils.checkNumbers(contact.getMobilePhone()) || Utils.checkNumbers(contact.getHomePhone()))) {
                addContact(contact);
            } else {
                sb.append("Контакт ");
                sb.append(contact.getSecondName());
                sb.append(" ");
                sb.append(contact.getFirstName());
                sb.append(" ");
                sb.append(contact.getPatronymic());
                sb.append(" имеет некорректные поля\n");
            }
        }
    }

    /**
     * Проверка на наличие контакта в базе данных
     *
     * @param preparedStatement стейтмент
     * @return true - контакт уже есть в базе данных, иначе false
     * @throws SQLException в случае ошибки при работе с базой
     */
    private boolean containsContact(PreparedStatement preparedStatement) throws SQLException {
        ResultSet resultSet = preparedStatement.executeQuery();
        int numberOfRows = 0;
        if (resultSet.next()) {
            numberOfRows = resultSet.getInt(1);
        }
        return numberOfRows != 0;
    }

    /**
     * Добавление контакта в телефонную книгу
     *
     * @param contact контакт для добавления
     */
    public void addContact(Contact contact) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SqlCommands.ADD_CONTACT);
        initStatement(contact, preparedStatement);
        preparedStatement.executeUpdate();
    }

    /**
     * Инициализация стейтмента контактом
     *
     * @param contact           сам контакт
     * @param preparedStatement подготовленный стейтмент
     * @throws SQLException ошибка при работе с базой
     */
    private void initStatement(Contact contact, PreparedStatement preparedStatement)
            throws SQLException {
        preparedStatement.setString(1, contact.getFirstName());
        preparedStatement.setString(2, contact.getSecondName());
        preparedStatement.setString(3, contact.getPatronymic());
        preparedStatement.setString(4, contact.getAddress());
        Date date = contact.getBirthday() == null ? null : Date.valueOf(contact.getBirthday());
        preparedStatement.setDate(5, date);
        preparedStatement.setString(6, contact.getMobilePhone());
        preparedStatement.setString(7, contact.getHomePhone());
        preparedStatement.setString(8, contact.getComment());
    }

    /**
     * Для замены контакта в телефонной книги на другой
     *
     * @param contact         новый контакт
     * @param changingContact контакт для замены
     */
    public void changeContact(Contact contact, Contact changingContact) throws SQLException {
        PreparedStatement preparedStatement =
                connection.prepareStatement(SqlCommands.EDIT_CONTACT);
        // Инициализирует все значения.
        initStatement(contact, preparedStatement);
        preparedStatement.setString(9, changingContact.getFirstName());
        preparedStatement.setString(10, changingContact.getSecondName());
        preparedStatement.setString(11, changingContact.getPatronymic());
        preparedStatement.executeUpdate();
    }

    /**
     * Удаление контакта из телефонной книги
     *
     * @param removeContact контакт для удаления
     */
    public void deleteContact(Contact removeContact) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SqlCommands.DELETE_CONTACT);
        preparedStatement.setString(1, removeContact.getFirstName());
        preparedStatement.setString(2, removeContact.getSecondName());
        preparedStatement.setString(3, removeContact.getPatronymic());
        preparedStatement.executeUpdate();
    }


    /**
     * Возвращает список всех контактов
     *
     * @return список всех контактов
     */
    public ObservableList<Contact> getAllContacts() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SqlCommands.GET_ALL_CONTACTS);
        List<Contact> contacts = new ArrayList<>();
        // Проходим по всем результатам и сохраняем.
        while (resultSet.next()) {
            LocalDate date = resultSet.getDate("birthday") == null ? null :
                    resultSet.getDate("birthday").toLocalDate();
            Contact contact = new Contact(resultSet.getString("firstName"),
                    resultSet.getString("secondName"),
                    resultSet.getString("patronymic"),
                    resultSet.getString("mobilePhone"),
                    resultSet.getString("homePhone"),
                    resultSet.getString("address"),
                    date,
                    resultSet.getString("comment"));
            contacts.add(contact);
        }
        resultSet.close();
        return FXCollections.observableArrayList(contacts);
    }

    /**
     * Фильтрует контакты в зависимости от строки поиска
     *
     * @param searchText строчка из поиска
     */
    public void filterContacts(String searchText) throws SQLException {
        contactFilteredList = new FilteredList<>(getAllContacts());
        contactFilteredList.setPredicate(t -> t.getFirstName().toLowerCase().startsWith(searchText) ||
                t.getSecondName().toLowerCase().startsWith(searchText) ||
                t.getPatronymic().toLowerCase().startsWith(searchText));
    }

    /**
     * Возвращает лист фильтрованных контактов после поиска
     *
     * @return список подходящих под поиск контактов
     */
    public FilteredList<Contact> getContactFilteredList() {
        return contactFilteredList;
    }

    /**
     * Закрываем соединения с базой
     *
     * @throws SQLException исключение, порожденное при закрытии соединений
     */
    public void closeConnection() throws SQLException {
        try {
            DriverManager.getConnection("jdbc:derby:phonebook;shutdown=true");
        } catch (SQLException e) {
            if ("08006".equals(e.getSQLState())) {
                System.out.println("Корректное завершение программы");
            }
        }
        connection.close();
    }

}
