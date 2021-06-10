package ru.hse.kdz.utils;

public final class SqlCommands {
    public static final String CREATE_TABLE = "CREATE TABLE CONTACTS"
            + " (id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
            + "firstName varchar(100) not null, "
            + "secondName varchar(100) not null, "
            + "patronymic varchar(100) not null, "
            + "address varchar(100) not null, "
            + "birthday date, mobilePhone varchar(13) not null, "
            + "homePhone varchar(13) not null, comment varchar(1000) not null, UNIQUE (id))";

    public static final String CHECK_NAME_CONTACT_CREATE = "SELECT COUNT(*) "
            + "FROM CONTACTS WHERE FIRSTNAME = ? AND SECONDNAME = ? AND PATRONYMIC = ?";
    public static final String GET_ALL_CONTACTS = "SELECT * from CONTACTS";

    public static final String ADD_CONTACT = "INSERT INTO CONTACTS (FIRSTNAME, SECONDNAME, PATRONYMIC, "
            + "ADDRESS, BIRTHDAY, MOBILEPHONE, HOMEPHONE, COMMENT) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String EDIT_CONTACT =
            "UPDATE CONTACTS SET FIRSTNAME = ?, SECONDNAME = ?, PATRONYMIC = ?,"
                    + "ADDRESS = ?, BIRTHDAY = ?, MOBILEPHONE = ?, HOMEPHONE =?, COMMENT =? "
                    + "where FIRSTNAME = ? AND SECONDNAME = ? AND PATRONYMIC = ?";

    public static final String DELETE_CONTACT = "DELETE FROM CONTACTS WHERE FIRSTNAME = ? "
            + "AND SECONDNAME = ? AND PATRONYMIC = ?";

}
