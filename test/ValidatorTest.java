import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import ru.hse.kdz.utils.Utils;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    @org.junit.jupiter.api.Test
    @DisplayName("Валидация пустого имени")
    void validateEmptyName() {
        String name = "";
        Assertions.assertFalse(Utils.checkCorrectFieldsName(name));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Валидация имени с неккоректными символами")
    void validateNameWithInvalidSymbols() {
        String name1 = "3478294785392578";
        String name2 = "34HJHJHHHJ782947HJJHJJ85392578";
        String name3 = "Vitya1";
        String name4 = "...../..,";
        String name5 = "vitya!";
        assertFalse(Utils.checkCorrectFieldsName(name1));
        assertFalse(Utils.checkCorrectFieldsName(name2));
        assertFalse(Utils.checkCorrectFieldsName(name3));
        assertFalse(Utils.checkCorrectFieldsName(name4));
        assertFalse(Utils.checkCorrectFieldsName(name5));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Валидация корректного имени")
    void validateCorrectNames() {
        String name1 = "Тумаченко";
        String name2 = "владислав";
        String name3 = "Vitya";
        String name4 = "Kolobkov";
        String name5 = "vitya";
        String name6 = "abcdefghijklmnopqrstuvwxyz";
        String name7 = "ZYXWVUTSRQPONMLKJIHGFEDCBA";
        String name8 = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
        String name9 = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        assertTrue(Utils.checkCorrectFieldsName(name1));
        assertTrue(Utils.checkCorrectFieldsName(name2));
        assertTrue(Utils.checkCorrectFieldsName(name3));
        assertTrue(Utils.checkCorrectFieldsName(name4));
        assertTrue(Utils.checkCorrectFieldsName(name5));
        assertTrue(Utils.checkCorrectFieldsName(name6));
        assertTrue(Utils.checkCorrectFieldsName(name7));
        assertTrue(Utils.checkCorrectFieldsName(name8));
        assertTrue(Utils.checkCorrectFieldsName(name9));

    }

    @org.junit.jupiter.api.Test
    @DisplayName("Валидация номера неккоректного размера")
    void validateIncorrectSizeNumbers() {
        String number1 = "456789765435678976546789";
        String number2 = "9";
        String number3 = "";
        String number4 = "9999999999";
        String number5 = "999999999911";
        assertFalse(Utils.checkCorrectFieldsName(number1));
        assertFalse(Utils.checkCorrectFieldsName(number2));
        assertFalse(Utils.checkCorrectFieldsName(number3));
        assertFalse(Utils.checkCorrectFieldsName(number4));
        assertFalse(Utils.checkCorrectFieldsName(number5));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Валидация номера с неккоректными символами")
    void validateIncorrectSymbolsInNumbers() {
        String number1 = "kKdl;scs";
        String number2 = "l23434334434";
        String number3 = "lllllllllll";
        String number4 = "llllllllll0";
        String number5 = "...,,,///][";
        assertFalse(Utils.checkNumbers(number1));
        assertFalse(Utils.checkNumbers(number2));
        assertFalse(Utils.checkNumbers(number3));
        assertFalse(Utils.checkNumbers(number4));
        assertFalse(Utils.checkNumbers(number5));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Валидация корректного номера")
    void validateCorrectNumbers() {
        String number1 = "00000000000";
        String number2 = "01234567891";
        String number3 = "89858542891";
        assertTrue(Utils.checkNumbers(number1));
        assertTrue(Utils.checkNumbers(number2));
        assertTrue(Utils.checkNumbers(number3));

    }


}