
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.hse.kdz.model.Contact;
import ru.hse.kdz.model.PhoneBook;
import ru.hse.kdz.utils.SqlCommands;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PhoneBookTest {
    PhoneBook phoneBook1;
    Connection connection1;

    PhoneBookTest() throws SQLException {
        String dbURL1 = "jdbc:derby:memory:t11;create=true";
        connection1 = DriverManager.getConnection(dbURL1);
        phoneBook1 = new PhoneBook(connection1);
        try {
            Statement statement = connection1.createStatement();
            statement.execute(SqlCommands.CREATE_TABLE);
        } catch (SQLException ignored) {

        }
    }

    @AfterEach
    void removeAndCloseConnections() throws SQLException {
        connection1.close();
        try {
            DriverManager.getConnection("jdbc:derby:memory:t11;drop=true");
        } catch (SQLException ignored) {

        }
    }

    @Test
    @DisplayName("Создание таблицы по некорректному sql запросу")
    void initPhoneBookWithNullStatement() {
        Statement statement = null;
        Statement statement2 = null;
        assertThrows(NullPointerException.class, () -> statement.execute(SqlCommands.CREATE_TABLE));
        assertThrows(NullPointerException.class, () -> statement2.execute(SqlCommands.CREATE_TABLE));
    }

    @Test
    @DisplayName("Создание таблицы по некорректному sql запросу")
    void initPhoneBookWithIncorrectSQL() throws SQLException {
        Statement statement1 = connection1.createStatement();
        assertThrows(SQLSyntaxErrorException.class, () -> statement1.execute("jkvndfnvkdfvk"));
        assertThrows(SQLSyntaxErrorException.class, () -> statement1.execute("jk2132131"));
        assertThrows(SQLSyntaxErrorException.class, () -> statement1.execute("JJHHJJ*HHJJHJJ"));
    }


    @Test
    @DisplayName("Проверка что подключение к базе выполнено и таблица создана")
    void initPhoneBookWithTableInDataBase() throws SQLException {
        List<Contact> listContacts = phoneBook1.getAllContacts();
        assertEquals(0, listContacts.size());
    }

    @Test
    @DisplayName("Добавление контакта")
    void addContact() throws SQLException {
        assertEquals(0, phoneBook1.getAllContacts().size());
        phoneBook1.addContact(new Contact());
        assertEquals(1, phoneBook1.getAllContacts().size());
        Contact contact = new Contact("f1", "s1", "p1", "89166302387", "", "", null, "");
        phoneBook1.addContact(contact);
        assertEquals(2, phoneBook1.getAllContacts().size());
        assertEquals(contact, phoneBook1.getAllContacts().get(1));
    }

    @Test
    @DisplayName("Замена контакта на другой")
    void changeContact() throws SQLException {
        Contact contact1 = new Contact("Никита", "Ткаченко", "",
                "89858448793", "", "", null, "");
        Contact contactChanging = new Contact("Василий", "Грозный", "",
                "89858448794", "", "", null, "");
        phoneBook1.addContact(contactChanging);
        List<Contact> listContacts = phoneBook1.getAllContacts();
        assertEquals("Василий", listContacts.get(0).getFirstName());
        assertEquals("Грозный", listContacts.get(0).getSecondName());
        phoneBook1.changeContact(contact1, contactChanging);
        listContacts = phoneBook1.getAllContacts();
        assertEquals("Никита", listContacts.get(0).getFirstName());
        assertEquals("Ткаченко", listContacts.get(0).getSecondName());
    }

    @Test
    @DisplayName("Удаление контакта")
    void deleteContact() throws SQLException {
        Contact contact1 = new Contact("Никита", "Ткаченко", "",
                "89858448793", "", "", null, "");
        Contact contact2 = new Contact("Василий", "Грозный", "",
                "89858448794", "", "", null, "");
        Contact contact3 = new Contact("Аркадий", "Шоленко", "",
                "89858448795", "", "", null, "");
        phoneBook1.addContact(contact1);
        phoneBook1.addContact(contact2);
        phoneBook1.addContact(contact3);
        assertEquals(3, phoneBook1.getAllContacts().size());
        phoneBook1.deleteContact(contact1);
        phoneBook1.deleteContact(contact2);
        phoneBook1.deleteContact(contact3);
        assertEquals(0, phoneBook1.getAllContacts().size());
    }

    @Test
    @DisplayName("Попытка добавления повторяющихся контактов в телефонную книгу")
    void checkAddRepeatContacts() throws SQLException {
        List<Contact> checkContacts = new ArrayList<>();
        Contact repeatContact = new Contact("Никита", "Ткаченко", "",
                "89858448793", "", "", null, "");
        checkContacts.add(repeatContact);
        checkContacts.add(repeatContact);
        checkContacts.add(repeatContact);
        StringBuilder sb = new StringBuilder();
        PreparedStatement preparedStatement = connection1
                .prepareStatement(SqlCommands.CHECK_NAME_CONTACT_CREATE);
        phoneBook1.addLegalContacts(checkContacts, sb, preparedStatement);
        assertEquals(1, phoneBook1.getAllContacts().size());
    }

    @Test
    @DisplayName("Попытка добавления неккоректных контактов в телефонную книгу")
    void checkAddIllegalContacts() throws SQLException {
        List<Contact> checkContacts = new ArrayList<>();
        Contact illegalContact1 = new Contact("Никита1", "Ткаченко", "",
                "89858448793", "", "", null, "");
        Contact illegalContact2 = new Contact("Никита", "Ткаченко2", "",
                "89858448793", "", "", null, "");
        Contact illegalContact3 = new Contact("Василий", "Шошков", "",
                "8985844879398", "", "", null, "");
        Contact illegalContact4 = new Contact("Игорь", "Клириков", "",
                "", "8у9858448793", "", null, "");
        Contact legalContact = new Contact("Василий", "Дубров", "",
                "", "89858448793", "", null, "");
        checkContacts.add(illegalContact1);
        checkContacts.add(illegalContact2);
        checkContacts.add(illegalContact3);
        checkContacts.add(illegalContact4);
        checkContacts.add(legalContact);
        StringBuilder sb = new StringBuilder();
        PreparedStatement preparedStatement = connection1
                .prepareStatement(SqlCommands.CHECK_NAME_CONTACT_CREATE);
        phoneBook1.addLegalContacts(checkContacts, sb, preparedStatement);
        List<Contact> resContacts = phoneBook1.getAllContacts();
        assertEquals(1, resContacts.size());
        assertEquals("Василий", resContacts.get(0).getFirstName());

    }

    @Test
    @DisplayName("Попытка добавления корректных контактов в телефонную книгу")
    void checkAddLegalContacts() throws SQLException {
        List<Contact> checkContacts = new ArrayList<>();
        Contact legalContact1 = new Contact("Василий", "Дубров", "",
                "", "89858448793", "", null, "");
        Contact legalContact2 = new Contact("Никита", "Ткаченко", "",
                "89858448793", "", "", null, "");
        checkContacts.add(legalContact1);
        checkContacts.add(legalContact2);
        StringBuilder sb = new StringBuilder();
        PreparedStatement preparedStatement = connection1
                .prepareStatement(SqlCommands.CHECK_NAME_CONTACT_CREATE);
        phoneBook1.addLegalContacts(checkContacts, sb, preparedStatement);
        assertEquals(2, phoneBook1.getAllContacts().size());
        assertEquals(0, sb.length());
    }


    @Test
    @DisplayName("Фильтрация контактов по строке")
    void filterContacts() throws SQLException {
        List<Contact> checkContacts = new ArrayList<>();
        Contact legalContact1 = new Contact("Василий", "Дубров", "",
                "", "89858448793", "", null, "");
        Contact legalContact2 = new Contact("Виталий", "Ткаченко", "",
                "89858448793", "", "", null, "");
        checkContacts.add(legalContact1);
        checkContacts.add(legalContact2);
        PreparedStatement preparedStatement = connection1
                .prepareStatement(SqlCommands.CHECK_NAME_CONTACT_CREATE);
        phoneBook1.addLegalContacts(checkContacts, new StringBuilder(), preparedStatement);
        phoneBook1.filterContacts("вас");
        assertEquals(1, phoneBook1.getContactFilteredList().size());
        phoneBook1.filterContacts("a");
        assertEquals(0, phoneBook1.getContactFilteredList().size());
        phoneBook1.filterContacts("в");
        assertEquals(2, phoneBook1.getContactFilteredList().size());
        phoneBook1.filterContacts("");
        assertEquals(2, phoneBook1.getContactFilteredList().size());
        phoneBook1.filterContacts("дубров");
        assertEquals(1, phoneBook1.getContactFilteredList().size());
        phoneBook1.filterContacts("ткач");
        assertEquals(1, phoneBook1.getContactFilteredList().size());
    }
}