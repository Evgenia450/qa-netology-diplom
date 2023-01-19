package ru.netology.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.*;
import ru.netology.helpers.DataHelper;
import ru.netology.helpers.SQLHelper;
import ru.netology.pages.CreditFormPage;
import ru.netology.pages.DashboardPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreditTest {
    static DashboardPage dashboardPage;
    static CreditFormPage creditForm;
    final String approved = "APPROVED";
    final String declined = "DECLINED";
    final int expiryYears = 5;
    final int cardOwnerMaxLength = 27;

    final String ordersTable = "order_entity";
    final String creditRequestsTable = "credit_request_entity";

    @BeforeAll
    static void setTimeout() {
        Configuration.timeout = 10000;
    }

    @BeforeEach
    void openHost() {
        open("http://localhost:8080");

        dashboardPage = new DashboardPage();
        creditForm = dashboardPage.openCreditForm();
    }

    @Test
    @DisplayName("1. Payment approved (Happy path)")
    void shouldApprovePaymentWithValidData() {
        long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
        long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

        creditForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        creditForm.getContinueButton().click();

        creditForm.checkSuccessNotification();
        assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
        assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
        assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
    }

    @Test
    @DisplayName("2. Send form with max expiry year")
    void shouldSendFormWithMaxExpiryYear() {
        long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
        long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

        creditForm.fillTheForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(expiryYears));
        creditForm.getContinueButton().click();

        creditForm.checkSuccessNotification();
        assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
        assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
        assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
    }

    @Test
    @DisplayName("3. Send form with max-1 expiry year")
    void shouldSendFormWithExpiryYearBelowMax() {
        long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
        long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

        creditForm.fillTheForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(expiryYears - 1));
        creditForm.getContinueButton().click();

        creditForm.checkSuccessNotification();
        assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
        assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
        assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
    }


    @Test
    @DisplayName("4. Send form with expiry date in current month")
    void shouldSendFormWithExpiryDateInCurrentMonth() {
        long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
        long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

        creditForm.fillTheForm(DataHelper.generateCardDataExpireInShiftedMonthFromCurrent(0));
        creditForm.getContinueButton().click();

        creditForm.checkSuccessNotification();
        assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
        assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
        assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
    }

    @Test
    @DisplayName("5. Send form with expiry date in next month")
    void shouldSendFormWithExpiryDateInNextMonth() {
        long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
        long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

        creditForm.fillTheForm(DataHelper.generateCardDataExpireInShiftedMonthFromCurrent(1));
        creditForm.getContinueButton().click();

        creditForm.checkSuccessNotification();
        assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
        assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
        assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
    }

    @Test
    @DisplayName("6. Send form with card owner length in 26 symbols")
    void shouldSendFormWithCardOwnerInLessThanMaxSymbols() {
        long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
        long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

        creditForm.fillTheForm(new DataHelper.CardData(DataHelper.getApprovedCardNumber(),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                DataHelper.generateCardOwnerWithFixedLength("en",cardOwnerMaxLength - 1),
                DataHelper.generateNumericCode(3)));
        creditForm.getContinueButton().click();

        creditForm.checkSuccessNotification();
        assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
        assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
        assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
    }

    @Test
    @DisplayName("7. Send form with card owner max length")
    void shouldSendFormWithCardOwnerMaxLength() {
        long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
        long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

        creditForm.fillTheForm(new DataHelper.CardData(DataHelper.getApprovedCardNumber(),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                DataHelper.generateCardOwnerWithFixedLength("en",cardOwnerMaxLength),
                DataHelper.generateNumericCode(3)));
        creditForm.getContinueButton().click();

        creditForm.checkSuccessNotification();
        assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
        assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
        assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
    }

    @Test
    @DisplayName("8. Send form with card owner with hyphen")
    void shouldSendFormWithCardOwnerWithHyphen() {
        long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
        long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

        creditForm.fillTheForm(new DataHelper.CardData(DataHelper.getApprovedCardNumber(),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                DataHelper.getCardOwnerWithHyphen(),
                DataHelper.generateNumericCode(3)));
        creditForm.getContinueButton().click();

        creditForm.checkSuccessNotification();
        assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
        assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
        assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
    }

    @Test
    @DisplayName("9. Send form with zero CVC")
    void shouldSendFormWithZeroCVC() {
        long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
        long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

        creditForm.fillTheForm(DataHelper.generateCardDataWithZeroCVC(expiryYears));
        creditForm.getContinueButton().click();

        creditForm.checkSuccessNotification();
        assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
        assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
        assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
    }

    @Test
    @DisplayName("10. Send form with declined card")
    void shouldSendFormWithDeclinedCard() {
        long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
        long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

        creditForm.fillTheForm(DataHelper.generateCardDataWithDeclinedCard(expiryYears));
        creditForm.getContinueButton().click();

        creditForm.checkErrorNotification();
        assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
        assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
        assertEquals(declined, SQLHelper.getLastStatusFromCreditsTable());
    }

    @Test
    @DisplayName("11. Should not send form without card number")
    void shouldNotSendFormWithoutCardNumber() {
        creditForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        creditForm.clearField(creditForm.getCard());
        creditForm.getContinueButton().click();

        creditForm.checkCardErrorIndication();
    }

    @Test
    @DisplayName("12. Should not send form without month")
    void shouldNotSendFormWithoutMonth() {
        creditForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        creditForm.clearField(creditForm.getMonth());
        creditForm.getContinueButton().click();

        creditForm.checkMonthWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("13. Should not send form without year")
    void shouldNotSendFormWithoutYear() {
        creditForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        creditForm.clearField(creditForm.getYear());
        creditForm.getContinueButton().click();

        creditForm.checkYearWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("14. Should not send form without card owner")
    void shouldNotSendFormWithoutCardOwner() {
        creditForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        creditForm.clearField(creditForm.getCardOwner());
        creditForm.getContinueButton().click();

        creditForm.checkCardOwnerErrorEmpty();
    }

    @Test
    @DisplayName("15. Should not send form without cvc/cvv")
    void shouldNotSendFormWithoutCVC() {
        creditForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        creditForm.clearField(creditForm.getCvc());
        creditForm.getContinueButton().click();

        creditForm.checkCVCErrorIndication();
    }

    @Test
    @DisplayName("16. Should not send empty form")
    void shouldNotSendEmptyForm() {
        creditForm.getContinueButton().click();

        creditForm.checkCardErrorIndication();
        creditForm.checkMonthWrongFormatOrEmpty();
        creditForm.checkYearWrongFormatOrEmpty();
        creditForm.checkCardOwnerErrorEmpty();
        creditForm.checkCVCErrorIndication();
    }

    @Test
    @DisplayName("17. Should not send form with incomplete card number")
    void shouldNotSendFormWithIncompleteCardNumber() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithIncompleteNumber(expiryYears, 15));
        creditForm.getContinueButton().click();

        creditForm.checkCardErrorIndication();
    }

    @Test
    @DisplayName("18. Should not enter more symbols, than card number length")
    void shouldNotEnterMoreSymbolsInCardNumber() {
        String cardNumberExt = DataHelper.generateNumericCode(17);
        String expected = cardNumberExt.substring(0, 16);

        creditForm.fillField(creditForm.getCard(), cardNumberExt);
        String actual = creditForm.getFieldValue(creditForm.getCard()).replaceAll("\\s+","");

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("19. Should not enter invalid symbols in card number")
    void shouldNotEnterInvalidCardNumber() {
        creditForm.fillField(creditForm.getCard(), DataHelper.getInvalidSymbolsForNumericFields());
        String actual = creditForm.getFieldValue(creditForm.getCard()).replaceAll("\\s+","");

        assertEquals("", actual);
    }

    @Test
    @DisplayName("20. Should not send form with card of zeroes")
    void shouldNotSendFormWithZeroCard() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithZeroCard(expiryYears));
        creditForm.getContinueButton().click();

        creditForm.checkCardErrorIndication();
    }

    @Test
    @DisplayName("21. Should not send form with zero month")
    void shouldNotSendFormWithZeroMonth() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithZeroMonth(expiryYears));
        creditForm.getContinueButton().click();

        creditForm.checkMonthWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("22. Should not send form with wrong month")
    void shouldNotSendFormWithWrongMonth() {
        creditForm.fillTheForm(new DataHelper.CardData(DataHelper.generateNumericCode(16),
                DataHelper.generateWrongMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                DataHelper.generateCardOwner("en"),
                DataHelper.generateNumericCode(3)));
        creditForm.getContinueButton().click();

        creditForm.checkMonthWrongExpiryDate();
    }

    @Test
    @DisplayName("23. Should not send form with incomplete month")
    void shouldNotSendFormWithIncompleteMonth() {
        creditForm.fillTheForm(new DataHelper.CardData(DataHelper.generateNumericCode(16),
                DataHelper.generateNumericCode(1),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                DataHelper.generateCardOwner("en"),
                DataHelper.generateNumericCode(3)));
        creditForm.getContinueButton().click();

        creditForm.checkMonthWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("24. Should not enter more symbols, than month length")
    void shouldNotEnterMoreSymbolsInMonth() {
        String monthExt = DataHelper.generateNumericCode(3);
        String expected = monthExt.substring(0, 2);

        creditForm.fillField(creditForm.getMonth(), monthExt);
        String actual = creditForm.getFieldValue(creditForm.getMonth());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("25. Should not enter invalid symbols in month")
    void shouldNotEnterInvalidSymbolsInMonth() {
        creditForm.fillField(creditForm.getMonth(), DataHelper.getInvalidSymbolsForNumericFields());
        String actual = creditForm.getFieldValue(creditForm.getMonth());

        assertEquals("", actual);
    }

    @Test
    @DisplayName("26. Should not send form with zero year")
    void shouldNotSendFormWithZeroYear() {
        creditForm.fillTheForm(new DataHelper.CardData(DataHelper.generateNumericCode(16),
                DataHelper.generateMonth(),
                "00",
                DataHelper.generateCardOwner("en"),
                DataHelper.generateNumericCode(3)));
        creditForm.getContinueButton().click();

        creditForm.checkYearErrorExpired();
    }

    @Test
    @DisplayName("27. Should not send form with incomplete year")
    void shouldNotSendFormWithIncompleteYear() {
        creditForm.fillTheForm(new DataHelper.CardData(DataHelper.generateNumericCode(16),
                DataHelper.generateMonth(),
                DataHelper.generateNumericCode(1),
                DataHelper.generateCardOwner("en"),
                DataHelper.generateNumericCode(3)));
        creditForm.getContinueButton().click();

        creditForm.checkYearWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("28. Should not enter more symbols, than year length")
    void shouldNotEnterMoreSymbolsInYear() {
        String yearExt = DataHelper.generateNumericCode(3);
        String expected = yearExt.substring(0, 2);

        creditForm.fillField(creditForm.getYear(), yearExt);
        String actual = creditForm.getFieldValue(creditForm.getYear());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("29. Should not enter invalid symbols in year")
    void shouldNotEnterInvalidSymbolsInYear() {
        creditForm.fillField(creditForm.getYear(), DataHelper.getInvalidSymbolsForNumericFields());
        String actual = creditForm.getFieldValue(creditForm.getYear());

        assertEquals("", actual);
    }

    @Test
    @DisplayName("30. Should not send form with previous year")
    void shouldNotSendFormWithPrevYear() {
        creditForm.fillTheForm(new DataHelper.CardData(DataHelper.generateNumericCode(16),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(-1),
                DataHelper.generateCardOwner("en"),
                DataHelper.generateNumericCode(3)));
        creditForm.getContinueButton().click();

        creditForm.checkYearErrorExpired();
    }

    @Test
    @DisplayName("31. Should not send form with more than expiry years")
    void shouldNotSendFormWithMoreThanExpiryYear() {
        creditForm.fillTheForm(new DataHelper.CardData(DataHelper.generateNumericCode(16),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears + 1),
                DataHelper.generateCardOwner("en"),
                DataHelper.generateNumericCode(3)));
        creditForm.getContinueButton().click();

        creditForm.checkYearWrongExpiryDate();
    }

    @Test
    @DisplayName("32. Should not send form with card expired in previous month")
    void shouldNotSendFormWithExpiryDateInPrevMonth() {
        creditForm.fillTheForm(DataHelper.generateCardDataExpireInShiftedMonthFromCurrent(-1));
        creditForm.getContinueButton().click();

        creditForm.checkMonthWrongExpiryDate();
    }

    @Test
    @DisplayName("33. Should not send form with incomplete card owner")
    void shouldNotSendFormWithIncompleteCardOwner() {
        creditForm.fillTheForm(new DataHelper.CardData(DataHelper.getApprovedCardNumber(),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                DataHelper.generateNameOnly("en"),
                DataHelper.generateNumericCode(3)));
        creditForm.getContinueButton().click();

        creditForm.checkCardOwnerAsWrittenOnCard();
    }

    @Test
    @DisplayName("34. Should not send form with cyrillic symbols in card owner")
    void shouldNotSendFormWithCyrillicCardOwner() {
        creditForm.fillTheForm(new DataHelper.CardData(DataHelper.getApprovedCardNumber(),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                DataHelper.generateCardOwner("ru"),
                DataHelper.generateNumericCode(3)));
        creditForm.getContinueButton().click();

        creditForm.checkCardOwnerWrongFormat();
    }

    @Test
    @DisplayName("35. Should not send form with invalid symbols in card owner")
    void shouldNotSendFormWithInvalidCardOwner() {
        creditForm.fillTheForm(new DataHelper.CardData(DataHelper.getApprovedCardNumber(),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                DataHelper.getInvalidSymbolsForCharacterFields(),
                DataHelper.generateNumericCode(3)));
        creditForm.getContinueButton().click();

        creditForm.checkCardOwnerWrongFormat();
    }

    @Test
    @DisplayName("36. Should not send form with spaces in card owner")
    void shouldNotSendFormWithSpacesInCardOwner() {
        creditForm.fillTheForm(new DataHelper.CardData(DataHelper.getApprovedCardNumber(),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                "      ",
                DataHelper.generateNumericCode(3)));
        creditForm.getContinueButton().click();

        creditForm.checkCardOwnerErrorEmpty();
    }
    @Test
    @DisplayName("37. Should not send form card owner more than 27 symbols")
    void shouldNotSendFormWithOverlongCardOwner() {
        creditForm.fillTheForm(new DataHelper.CardData(DataHelper.getApprovedCardNumber(),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                DataHelper.generateOverlongCardOwner("en"),
                DataHelper.generateNumericCode(3)));
        creditForm.getContinueButton().click();

        creditForm.checkCardOwnerAsWrittenOnCard();
    }


    @Test
    @DisplayName("38. Should not send form with incomplete CVC")
    void shouldNotSendFormWithIncompleteCVC() {
        creditForm.fillTheForm(new DataHelper.CardData(DataHelper.generateNumericCode(16),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                DataHelper.generateCardOwner("en"),
                DataHelper.generateNumericCode(2)));
        creditForm.getContinueButton().click();

        creditForm.checkCVCErrorIndication();
    }

    @Test
    @DisplayName("39. Should not enter more symbols, than CVC length")
    void shouldNotEnterMoreSymbolsInCVC() {
        String cvcExt = DataHelper.generateNumericCode(4);
        String expected = cvcExt.substring(0, 3);

        creditForm.fillField(creditForm.getCvc(), cvcExt);
        String actual = creditForm.getFieldValue(creditForm.getCvc());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("40. Should not enter invalid symbols in CVC")
    void shouldNotEnterInvalidSymbolsInCVC() {
        creditForm.fillField(creditForm.getCvc(), DataHelper.getInvalidSymbolsForNumericFields());
        String actual = creditForm.getFieldValue(creditForm.getCvc());

        assertEquals("", actual);
    }

    /*
    @AfterAll
    @SneakyThrows
    static void cleanDB() {
        SQLHelper.cleanDatabase();
    }

     */
}
