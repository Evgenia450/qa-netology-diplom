package ru.netology.helpers;

import com.github.javafaker.Faker;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class DataHelper {
    static Random random = new Random();

    private DataHelper() {
    }

    // вложенный класс
    // все данные карты
    @Value
    public static class CardData {
        private String cardNumber;
        private String month;
        private String year;
        private String cardOwner;
        private String cvc;
    }

    public static String getApprovedCardNumber() {
        return "4444 4444 4444 4441";
    }

    public static String getDeclinedCardNumber() {
        return "4444 4444 4444 4442";
    }

    public static String getInvalidSymbolsForNumericFields() {
        Faker fakerRU = new Faker(new Locale("ru"));
        Faker fakerEN = new Faker(new Locale("en"));
        return fakerRU.lorem().characters(5,true,false) +
                fakerEN.lorem().characters(5,true,false) +
                "~`@\"#№$;%^:&?*()_-+=/\\{}[]|<>'";
    }

    public static String getInvalidSymbolsForCharacterFields() {
        return generateNumericCode(10) + "~`@\"#№$;%^:&?*()_+=/\\{}[]|<>'";
    }

    public static String getCardOwnerWithHyphen() {
        return "KALINA-NIKOLAY IVANOV";
    }

    public static String generateCardOwner(String locale) {
        Faker faker = new Faker(new Locale(locale));
        return faker.name().firstName().toUpperCase(Locale.ROOT) + " " + faker.name().lastName().toUpperCase(Locale.ROOT);
    }

    public static String generateNameOnly(String locale) {
        Faker faker = new Faker(new Locale(locale));
        return faker.name().firstName().toUpperCase(Locale.ROOT);
    }

    public static String generateCardOwnerWithFixedLength(String locale, int length) {
        Faker faker = new Faker(new Locale(locale));
        String first = faker.lorem().characters(length / 2, false, false);
        String second = faker.lorem().characters(length - first.length() - 1, false, false);

        return first + " " + second;
    }

    public static String generateMonth() {
        return String.format("%02d", (random.nextInt(12) + 1));
    }

    public static String generateWrongMonth() {
        return String.format("%02d", (random.nextInt(99 - 13) + 13));
    }

    public static String generateShiftedYearFromCurrent(int shift) {
        LocalDate date = LocalDate.now().plusYears(shift);
        return DateTimeFormatter.ofPattern("yy").format(date);
    }

    public static String generateNumericCode(int length) {
        Faker faker = new Faker();
        return faker.number().digits(length);
    }

    public static CardData generateValidCardData(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithDeclinedCard(int expiryYears) {
        return new CardData(getDeclinedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithCardOwnerFixedLength(int cardOwnerLength, int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwnerWithFixedLength("en", cardOwnerLength),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithInvalidCardOwnerLocale(String locale, int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner(locale),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithInvalidCardOwnerSymbols(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                getInvalidSymbolsForCharacterFields(),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithCardOwnerSpaces(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                "          ",
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithIncompleteCardOwner(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateNameOnly("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithHyphenCardOwner(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                getCardOwnerWithHyphen(),
                generateNumericCode(3));
    }

    // для генерации срока действия, истекающего месяцем ранее / в текущем месяце / в следующем месяце
    public static CardData generateCardDataWithShiftedMonthFromCurrent(int shiftInMonths) {
        LocalDate date = LocalDate.now().plusMonths(shiftInMonths);
        return new CardData(getApprovedCardNumber(),
                DateTimeFormatter.ofPattern("MM").format(date),
                DateTimeFormatter.ofPattern("yy").format(date),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    // для генерации срока действия, истекающего через 4-5-6 лет (или годом ранее)
    public static CardData generateCardDataWithShiftedYearFromCurrent(int shiftInYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(shiftInYears),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithZeroCVC(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                "000");
    }

    public static CardData generateCardDataWithIncompleteNumber(int expiryYears, int length) {
        return new CardData(generateNumericCode(length),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithWrongMonth(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateWrongMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithMonthInvalidLength(int monthLength, int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateNumericCode(monthLength),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithYearInvalidLength(int yearLength) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateNumericCode(yearLength),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithCVCInvalidLength(int CVCLength, int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                generateNumericCode(CVCLength));
    }

    public static CardData generateCardDataWithZeroCard(int expiryYears) {
        return new CardData("0000 0000 0000 0000",
                generateMonth(),
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithZeroYear() {
        return new CardData(getApprovedCardNumber(),
                generateMonth(),
                "00",
                generateCardOwner("en"),
                generateNumericCode(3));
    }

    public static CardData generateCardDataWithZeroMonth(int expiryYears) {
        return new CardData(getApprovedCardNumber(),
                "00",
                generateShiftedYearFromCurrent(random.nextInt(expiryYears) + 1),
                generateCardOwner("en"),
                generateNumericCode(3));
    }

}
