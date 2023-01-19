package ru.netology.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import ru.netology.helpers.DataHelper;
import ru.netology.helpers.SQLHelper;
import ru.netology.pages.DashboardPage;
import ru.netology.pages.PaymentFormPage;
import io.qameta.allure.selenide.AllureSelenide;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentTest {
    static DashboardPage dashboardPage;
    static PaymentFormPage paymentForm;
    final String approved = "APPROVED";
    final String declined = "DECLINED";
    final int expiryYears = 5;
    final int cardOwnerMaxLength = 27;

    final String ordersTable = "order_entity";
    final String paymentsTable = "payment_entity";

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    void openHost() {
        open("http://localhost:8080");

        dashboardPage = new DashboardPage();
        paymentForm = dashboardPage.openPaymentForm();
    }

    @Nested
    class IncreasedTimeout{
        @BeforeEach
        void setTimeout() {
            Configuration.timeout = 10000;
        }

        @Test
        @DisplayName("1. Payment approved (Happy path)")
        void shouldApprovePaymentWithValidData() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
            paymentForm.getContinueButton().click();

            paymentForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromPaymentsTable());
        }

        @Test
        @DisplayName("2. Send form with max expiry year")
        void shouldSendFormWithMaxExpiryYear() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(expiryYears));
            paymentForm.getContinueButton().click();

            paymentForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromPaymentsTable());
        }

        @Test
        @DisplayName("3. Send form with max-1 expiry year")
        void shouldSendFormWithExpiryYearBelowMax() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(expiryYears - 1));
            paymentForm.getContinueButton().click();

            paymentForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromPaymentsTable());
        }


        @Test
        @DisplayName("4. Send form with expiry date in current month")
        void shouldSendFormWithExpiryDateInCurrentMonth() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(DataHelper.generateCardDataExpireInShiftedMonthFromCurrent(0));
            paymentForm.getContinueButton().click();

            paymentForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromPaymentsTable());
        }

        @Test
        @DisplayName("5. Send form with expiry date in next month")
        void shouldSendFormWithExpiryDateInNextMonth() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(DataHelper.generateCardDataExpireInShiftedMonthFromCurrent(1));
            paymentForm.getContinueButton().click();

            paymentForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromPaymentsTable());
        }

        @Test
        @DisplayName("6. Send form with card owner length in 26 symbols")
        void shouldSendFormWithCardOwnerInLessThanMaxSymbols() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(new DataHelper.CardData(DataHelper.getApprovedCardNumber(),
                    DataHelper.generateMonth(),
                    DataHelper.generateShiftedYearFromCurrent(expiryYears),
                    DataHelper.generateCardOwnerWithFixedLength("en",cardOwnerMaxLength - 1),
                    DataHelper.generateNumericCode(3)));
            paymentForm.getContinueButton().click();

            paymentForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromPaymentsTable());
        }

        @Test
        @DisplayName("7. Send form with card owner max length")
        void shouldSendFormWithCardOwnerMaxLength() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(new DataHelper.CardData(DataHelper.getApprovedCardNumber(),
                    DataHelper.generateMonth(),
                    DataHelper.generateShiftedYearFromCurrent(expiryYears),
                    DataHelper.generateCardOwnerWithFixedLength("en",cardOwnerMaxLength),
                    DataHelper.generateNumericCode(3)));
            paymentForm.getContinueButton().click();

            paymentForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromPaymentsTable());
        }

        @Test
        @DisplayName("8. Send form with card owner with hyphen")
        void shouldSendFormWithCardOwnerWithHyphen() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(new DataHelper.CardData(DataHelper.getApprovedCardNumber(),
                    DataHelper.generateMonth(),
                    DataHelper.generateShiftedYearFromCurrent(expiryYears),
                    DataHelper.getCardOwnerWithHyphen(),
                    DataHelper.generateNumericCode(3)));
            paymentForm.getContinueButton().click();

            paymentForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromPaymentsTable());
        }

        @Test
        @DisplayName("9. Send form with zero CVC")
        void shouldSendFormWithZeroCVC() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(DataHelper.generateCardDataWithZeroCVC(expiryYears));
            paymentForm.getContinueButton().click();

            paymentForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromPaymentsTable());
        }

        @Test
        @DisplayName("10. Send form with declined card")
        void shouldSendFormWithDeclinedCard() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(DataHelper.generateCardDataWithDeclinedCard(expiryYears));
            paymentForm.getContinueButton().click();

            paymentForm.checkErrorNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            assertEquals(declined, SQLHelper.getLastStatusFromPaymentsTable());
        }
    }

    @Test
    @DisplayName("11. Should not send form without card number")
    void shouldNotSendFormWithoutCardNumber() {
        paymentForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        paymentForm.clearField(paymentForm.getCard());
        paymentForm.getContinueButton().click();

        paymentForm.checkCardErrorIndication();
    }

    @Test
    @DisplayName("12. Should not send form without month")
    void shouldNotSendFormWithoutMonth() {
        paymentForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        paymentForm.clearField(paymentForm.getMonth());
        paymentForm.getContinueButton().click();

        paymentForm.checkMonthWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("13. Should not send form without year")
    void shouldNotSendFormWithoutYear() {
        paymentForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        paymentForm.clearField(paymentForm.getYear());
        paymentForm.getContinueButton().click();

        paymentForm.checkYearWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("14. Should not send form without card owner")
    void shouldNotSendFormWithoutCardOwner() {
        paymentForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        paymentForm.clearField(paymentForm.getCardOwner());
        paymentForm.getContinueButton().click();

        paymentForm.checkCardOwnerErrorEmpty();
    }

    @Test
    @DisplayName("15. Should not send form without cvc/cvv")
    void shouldNotSendFormWithoutCVC() {
        paymentForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        paymentForm.clearField(paymentForm.getCvc());
        paymentForm.getContinueButton().click();

        paymentForm.checkCVCErrorIndication();
    }

    @Test
    @DisplayName("16. Should not send empty form")
    void shouldNotSendEmptyForm() {
        paymentForm.getContinueButton().click();

        paymentForm.checkCardErrorIndication();
        paymentForm.checkMonthWrongFormatOrEmpty();
        paymentForm.checkYearWrongFormatOrEmpty();
        paymentForm.checkCardOwnerErrorEmpty();
        paymentForm.checkCVCErrorIndication();
    }

    @Test
    @DisplayName("17. Should not send form with incomplete card number")
    void shouldNotSendFormWithIncompleteCardNumber() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithIncompleteNumber(expiryYears, 15));
        paymentForm.getContinueButton().click();

        paymentForm.checkCardErrorIndication();
    }

    @Test
    @DisplayName("18. Should not enter more symbols, than card number length")
    void shouldNotEnterMoreSymbolsInCardNumber() {
        String cardNumberExt = DataHelper.generateNumericCode(17);
        String expected = cardNumberExt.substring(0, 16);

        paymentForm.fillField(paymentForm.getCard(), cardNumberExt);
        String actual = paymentForm.getFieldValue(paymentForm.getCard()).replaceAll("\\s+","");

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("19. Should not enter invalid symbols in card number")
    void shouldNotEnterInvalidCardNumber() {
        paymentForm.fillField(paymentForm.getCard(), DataHelper.getInvalidSymbolsForNumericFields());
        String actual = paymentForm.getFieldValue(paymentForm.getCard()).replaceAll("\\s+","");

        assertEquals("", actual);
    }

    @Test
    @DisplayName("20. Should not send form with card of zeroes")
    void shouldNotSendFormWithZeroCard() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithZeroCard(expiryYears));
        paymentForm.getContinueButton().click();

        paymentForm.checkCardErrorIndication();
    }

    @Test
    @DisplayName("21. Should not send form with zero month")
    void shouldNotSendFormWithZeroMonth() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithZeroMonth(expiryYears));
        paymentForm.getContinueButton().click();

        paymentForm.checkMonthWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("22. Should not send form with wrong month")
    void shouldNotSendFormWithWrongMonth() {
        paymentForm.fillTheForm(new DataHelper.CardData(DataHelper.generateNumericCode(16),
                DataHelper.generateWrongMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                DataHelper.generateCardOwner("en"),
                DataHelper.generateNumericCode(3)));
        paymentForm.getContinueButton().click();

        paymentForm.checkMonthWrongExpiryDate();
    }

    @Test
    @DisplayName("23. Should not send form with incomplete month")
    void shouldNotSendFormWithIncompleteMonth() {
        paymentForm.fillTheForm(new DataHelper.CardData(DataHelper.generateNumericCode(16),
                DataHelper.generateNumericCode(1),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                DataHelper.generateCardOwner("en"),
                DataHelper.generateNumericCode(3)));
        paymentForm.getContinueButton().click();

        paymentForm.checkMonthWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("24. Should not enter more symbols, than month length")
    void shouldNotEnterMoreSymbolsInMonth() {
        String monthExt = DataHelper.generateNumericCode(3);
        String expected = monthExt.substring(0, 2);

        paymentForm.fillField(paymentForm.getMonth(), monthExt);
        String actual = paymentForm.getFieldValue(paymentForm.getMonth());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("25. Should not enter invalid symbols in month")
    void shouldNotEnterInvalidSymbolsInMonth() {
        paymentForm.fillField(paymentForm.getMonth(), DataHelper.getInvalidSymbolsForNumericFields());
        String actual = paymentForm.getFieldValue(paymentForm.getMonth());

        assertEquals("", actual);
    }

    @Test
    @DisplayName("26. Should not send form with zero year")
    void shouldNotSendFormWithZeroYear() {
        paymentForm.fillTheForm(new DataHelper.CardData(DataHelper.generateNumericCode(16),
                DataHelper.generateMonth(),
                "00",
                DataHelper.generateCardOwner("en"),
                DataHelper.generateNumericCode(3)));
        paymentForm.getContinueButton().click();

        paymentForm.checkYearErrorExpired();
    }

    @Test
    @DisplayName("27. Should not send form with incomplete year")
    void shouldNotSendFormWithIncompleteYear() {
        paymentForm.fillTheForm(new DataHelper.CardData(DataHelper.generateNumericCode(16),
                DataHelper.generateMonth(),
                DataHelper.generateNumericCode(1),
                DataHelper.generateCardOwner("en"),
                DataHelper.generateNumericCode(3)));
        paymentForm.getContinueButton().click();

        paymentForm.checkYearWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("28. Should not enter more symbols, than year length")
    void shouldNotEnterMoreSymbolsInYear() {
        String yearExt = DataHelper.generateNumericCode(3);
        String expected = yearExt.substring(0, 2);

        paymentForm.fillField(paymentForm.getYear(), yearExt);
        String actual = paymentForm.getFieldValue(paymentForm.getYear());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("29. Should not enter invalid symbols in year")
    void shouldNotEnterInvalidSymbolsInYear() {
        paymentForm.fillField(paymentForm.getYear(), DataHelper.getInvalidSymbolsForNumericFields());
        String actual = paymentForm.getFieldValue(paymentForm.getYear());

        assertEquals("", actual);
    }

    @Test
    @DisplayName("30. Should not send form with previous year")
    void shouldNotSendFormWithPrevYear() {
        paymentForm.fillTheForm(new DataHelper.CardData(DataHelper.generateNumericCode(16),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(-1),
                DataHelper.generateCardOwner("en"),
                DataHelper.generateNumericCode(3)));
        paymentForm.getContinueButton().click();

        paymentForm.checkYearErrorExpired();
    }

    @Test
    @DisplayName("31. Should not send form with more than expiry years")
    void shouldNotSendFormWithMoreThanExpiryYear() {
        paymentForm.fillTheForm(new DataHelper.CardData(DataHelper.generateNumericCode(16),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears + 1),
                DataHelper.generateCardOwner("en"),
                DataHelper.generateNumericCode(3)));
        paymentForm.getContinueButton().click();

        paymentForm.checkYearWrongExpiryDate();
    }

    @Test
    @DisplayName("32. Should not send form with card expired in previous month")
    void shouldNotSendFormWithExpiryDateInPrevMonth() {
        paymentForm.fillTheForm(DataHelper.generateCardDataExpireInShiftedMonthFromCurrent(-1));
        paymentForm.getContinueButton().click();

        paymentForm.checkMonthWrongExpiryDate();
    }

    @Test
    @DisplayName("33. Should not send form with incomplete card owner")
    void shouldNotSendFormWithIncompleteCardOwner() {
        paymentForm.fillTheForm(new DataHelper.CardData(DataHelper.getApprovedCardNumber(),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                DataHelper.generateNameOnly("en"),
                DataHelper.generateNumericCode(3)));
        paymentForm.getContinueButton().click();

        paymentForm.checkCardOwnerAsWrittenOnCard();
    }

    @Test
    @DisplayName("34. Should not send form with cyrillic symbols in card owner")
    void shouldNotSendFormWithCyrillicCardOwner() {
        paymentForm.fillTheForm(new DataHelper.CardData(DataHelper.getApprovedCardNumber(),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                DataHelper.generateCardOwner("ru"),
                DataHelper.generateNumericCode(3)));
        paymentForm.getContinueButton().click();

        paymentForm.checkCardOwnerWrongFormat();
    }

    @Test
    @DisplayName("35. Should not send form with invalid symbols in card owner")
    void shouldNotSendFormWithInvalidCardOwner() {
        paymentForm.fillTheForm(new DataHelper.CardData(DataHelper.getApprovedCardNumber(),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                DataHelper.getInvalidSymbolsForCharacterFields(),
                DataHelper.generateNumericCode(3)));
        paymentForm.getContinueButton().click();

        paymentForm.checkCardOwnerWrongFormat();
    }

    @Test
    @DisplayName("36. Should not send form with spaces in card owner")
    void shouldNotSendFormWithSpacesInCardOwner() {
        paymentForm.fillTheForm(new DataHelper.CardData(DataHelper.getApprovedCardNumber(),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                "      ",
                DataHelper.generateNumericCode(3)));
        paymentForm.getContinueButton().click();

        paymentForm.checkCardOwnerErrorEmpty();
    }
    @Test
    @DisplayName("37. Should not send form card owner more than 27 symbols")
    void shouldNotSendFormWithOverlongCardOwner() {
        paymentForm.fillTheForm(new DataHelper.CardData(DataHelper.getApprovedCardNumber(),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                DataHelper.generateOverlongCardOwner("en"),
                DataHelper.generateNumericCode(3)));
        paymentForm.getContinueButton().click();

        paymentForm.checkCardOwnerAsWrittenOnCard();
    }


    @Test
    @DisplayName("38. Should not send form with incomplete CVC")
    void shouldNotSendFormWithIncompleteCVC() {
        paymentForm.fillTheForm(new DataHelper.CardData(DataHelper.generateNumericCode(16),
                DataHelper.generateMonth(),
                DataHelper.generateShiftedYearFromCurrent(expiryYears),
                DataHelper.generateCardOwner("en"),
                DataHelper.generateNumericCode(2)));
        paymentForm.getContinueButton().click();

        paymentForm.checkCVCErrorIndication();
    }

    @Test
    @DisplayName("39. Should not enter more symbols, than CVC length")
    void shouldNotEnterMoreSymbolsInCVC() {
        String cvcExt = DataHelper.generateNumericCode(4);
        String expected = cvcExt.substring(0, 3);

        paymentForm.fillField(paymentForm.getCvc(), cvcExt);
        String actual = paymentForm.getFieldValue(paymentForm.getCvc());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("40. Should not enter invalid symbols in CVC")
    void shouldNotEnterInvalidSymbolsInCVC() {
        paymentForm.fillField(paymentForm.getCvc(), DataHelper.getInvalidSymbolsForNumericFields());
        String actual = paymentForm.getFieldValue(paymentForm.getCvc());

        assertEquals("", actual);
    }

    /*
    @AfterAll
    @SneakyThrows
    static void cleanDB() {
        SQLHelper.cleanDatabase();
    }

     */

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }
}
