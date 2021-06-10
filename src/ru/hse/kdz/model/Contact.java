package ru.hse.kdz.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.util.Objects;

@JsonAutoDetect
public class Contact {
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty secondName;
    private final SimpleStringProperty patronymic;
    private final SimpleStringProperty mobilePhone;
    private final SimpleStringProperty homePhone;
    private final SimpleStringProperty address;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final SimpleObjectProperty<LocalDate> birthday;
    private final SimpleStringProperty comment;

    public Contact() {
        firstName = new SimpleStringProperty("");
        secondName = new SimpleStringProperty("");
        patronymic = new SimpleStringProperty("");
        mobilePhone = new SimpleStringProperty("");
        homePhone = new SimpleStringProperty("");
        address = new SimpleStringProperty("");
        birthday = new SimpleObjectProperty<>();
        comment = new SimpleStringProperty("");
    }

    public Contact(String firstName, String secondName, String patronymic,
                   String mobilePhone, String homePhone, String address,
                   LocalDate birthday, String comment) {
        this.firstName = new SimpleStringProperty(firstName);
        this.secondName = new SimpleStringProperty(secondName);
        this.patronymic = new SimpleStringProperty(patronymic);
        this.mobilePhone = new SimpleStringProperty(mobilePhone);
        this.homePhone = new SimpleStringProperty(homePhone);
        this.address = new SimpleStringProperty(address);
        this.birthday = new SimpleObjectProperty<>(birthday);
        this.comment = new SimpleStringProperty(comment);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public String getSecondName() {
        return secondName.get();
    }

    public String getPatronymic() {
        return patronymic.get();
    }

    public String getHomePhone() {
        return homePhone.get();
    }

    public String getMobilePhone() {
        return mobilePhone.get();
    }

    public String getAddress() {
        return address.get();
    }

    public LocalDate getBirthday() {
        return birthday.get();
    }

    public String getComment() {
        return comment.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public void setSecondName(String secondName) {
        this.secondName.set(secondName);
    }

    public void setPatronymic(String patronymic) {
        this.patronymic.set(patronymic);
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone.set(mobilePhone);
    }

    public void setHomePhone(String homePhone) {
        this.homePhone.set(homePhone);
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday.set(birthday);
    }

    public void setComment(String comment) {
        this.comment.set(comment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Contact contact = (Contact) o;
        return Objects.equals(firstName.getValue(), contact.firstName.getValue()) &&
                Objects.equals(secondName.getValue(), contact.secondName.getValue()) &&
                Objects.equals(patronymic.getValue(), contact.patronymic.getValue());

    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, secondName, patronymic);
    }
}
