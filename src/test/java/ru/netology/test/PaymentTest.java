package ru.netology.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.*;
import ru.netology.helpers.DataHelper;
import ru.netology.helpers.SQLHelper;
import ru.netology.pages.DashboardPage;
import ru.netology.pages.PaymentFormPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentTest extends TestBase {
    static DashboardPage dashboardPage;
    static PaymentFormPage paymentForm;

    final String paymentsTable = "payment_entity";
    String expectedPrice = "4500000";

    @BeforeEach
    void openHost() {
        open(testHost);

        dashboardPage = new DashboardPage();
        paymentForm = dashboardPage.openPaymentForm();
    }

    @Nested
    class IncreasedTimeout {
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


            paymentForm.checkSuccessNotification(); // проверка уведомления в UI

            // проверки на изменение кол-ва записей в БД (что точно добавилась новая запись)
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            // проверка суммы оплаты
            assertEquals(expectedPrice, lastEntry.getAmount());
            // проверка статуса последней записи
            assertEquals(approved, lastEntry.getStatus());
        }

        @Test
        @DisplayName("2. Max expiry year")
        void shouldSendFormWithMaxExpiryYear() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(expiryYears));
            paymentForm.getContinueButton().click();

            paymentForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(approved, lastEntry.getStatus());
        }

        @Test
        @DisplayName("3. Max-1 expiry year")
        void shouldSendFormWithExpiryYearBelowMax() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(expiryYears - 1));
            paymentForm.getContinueButton().click();

            paymentForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(approved, lastEntry.getStatus());
        }


        @Test
        @DisplayName("4. Expiry date in current month")
        void shouldSendFormWithExpiryDateInCurrentMonth() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(DataHelper.generateCardDataWithShiftedMonthFromCurrent(0));
            paymentForm.getContinueButton().click();

            paymentForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(approved, lastEntry.getStatus());
        }

        @Test
        @DisplayName("5. Expiry date in next month")
        void shouldSendFormWithExpiryDateInNextMonth() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(DataHelper.generateCardDataWithShiftedMonthFromCurrent(1));
            paymentForm.getContinueButton().click();

            paymentForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(approved, lastEntry.getStatus());
        }

        @Test
        @DisplayName("6. Card owner length = 26 symbols (max-1)")
        void shouldSendFormWithCardOwnerInLessThanMaxSymbols() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(DataHelper.generateCardDataWithCardOwnerFixedLength(cardOwnerMaxLength - 1, expiryYears));
            paymentForm.getContinueButton().click();

            paymentForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(approved, lastEntry.getStatus());
        }

        @Test
        @DisplayName("7. Card owner max length")
        void shouldSendFormWithCardOwnerMaxLength() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(DataHelper.generateCardDataWithCardOwnerFixedLength(cardOwnerMaxLength, expiryYears));
            paymentForm.getContinueButton().click();

            paymentForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(approved, lastEntry.getStatus());
        }

        @Test
        @DisplayName("8. Card owner with hyphen")
        void shouldSendFormWithCardOwnerWithHyphen() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(DataHelper.generateCardDataWithHyphenCardOwner(expiryYears));
            paymentForm.getContinueButton().click();

            paymentForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(approved, lastEntry.getStatus());
        }

        @Test
        @DisplayName("9. Valid data with zero CVC")
        void shouldSendFormWithZeroCVC() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(DataHelper.generateCardDataWithZeroCVC(expiryYears));
            paymentForm.getContinueButton().click();

            paymentForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(approved, lastEntry.getStatus());
        }

        @Test
        @DisplayName("10. Declined payment")
        void shouldSendFormWithDeclinedCard() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillTheForm(DataHelper.generateCardDataWithDeclinedCard(expiryYears));
            paymentForm.getContinueButton().click();

            paymentForm.checkErrorNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(declined, lastEntry.getStatus());
        }
    }

    @Test
    @DisplayName("11. Empty card number")
    void shouldNotSendFormWithoutCardNumber() {
        paymentForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        paymentForm.clearField(paymentForm.getCard());
        paymentForm.getContinueButton().click();

        paymentForm.checkCardErrorIndication();
    }

    @Test
    @DisplayName("12. Empty month")
    void shouldNotSendFormWithoutMonth() {
        paymentForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        paymentForm.clearField(paymentForm.getMonth());
        paymentForm.getContinueButton().click();

        paymentForm.checkMonthWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("13. Empty year")
    void shouldNotSendFormWithoutYear() {
        paymentForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        paymentForm.clearField(paymentForm.getYear());
        paymentForm.getContinueButton().click();

        paymentForm.checkYearWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("14. Empty card owner")
    void shouldNotSendFormWithoutCardOwner() {
        paymentForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        paymentForm.clearField(paymentForm.getCardOwner());
        paymentForm.getContinueButton().click();

        paymentForm.checkCardOwnerErrorEmpty();
    }

    @Test
    @DisplayName("15. Empty CVC/CVV")
    void shouldNotSendFormWithoutCVC() {
        paymentForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        paymentForm.clearField(paymentForm.getCvc());
        paymentForm.getContinueButton().click();

        paymentForm.checkCVCErrorIndication();
    }

    @Test
    @DisplayName("16. Empty form")
    void shouldNotSendEmptyForm() {
        paymentForm.getContinueButton().click();

        paymentForm.checkCardErrorIndication();
        paymentForm.checkMonthWrongFormatOrEmpty();
        paymentForm.checkYearWrongFormatOrEmpty();
        paymentForm.checkCardOwnerErrorEmpty();
        paymentForm.checkCVCErrorIndication();
    }

    @Test
    @DisplayName("17. Incomplete card number")
    void shouldNotSendFormWithIncompleteCardNumber() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithIncompleteNumber(expiryYears, cardNumberMaxLength - 1));
        paymentForm.getContinueButton().click();

        paymentForm.checkCardErrorIndication();
    }

    @Test
    @DisplayName("18. Enter more symbols, than card number length")
    void shouldNotEnterMoreSymbolsInCardNumber() {
        String cardNumberExt = DataHelper.generateNumericCode(cardNumberMaxLength + 1);
        String expected = cardNumberExt.substring(0, cardNumberMaxLength);

        paymentForm.fillField(paymentForm.getCard(), cardNumberExt);
        String actual = paymentForm.getFieldValue(paymentForm.getCard()).replaceAll("\\s+","");

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("19. Enter invalid symbols in card number")
    void shouldNotEnterInvalidCardNumber() {
        paymentForm.fillField(paymentForm.getCard(), DataHelper.getInvalidSymbolsForNumericFields());
        String actual = paymentForm.getFieldValue(paymentForm.getCard()).replaceAll("\\s+","");

        assertEquals("", actual);
    }

    @Test
    @DisplayName("20. Card number consists of zeroes")
    void shouldNotSendFormWithZeroCard() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithZeroCard(expiryYears));
        paymentForm.getContinueButton().click();

        paymentForm.checkCardErrorIndication();
    }

    @Test
    @DisplayName("21. Zero month")
    void shouldNotSendFormWithZeroMonth() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithZeroMonth(expiryYears));
        paymentForm.getContinueButton().click();

        paymentForm.checkMonthWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("22. Non-existent month")
    void shouldNotSendFormWithWrongMonth() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithWrongMonth(expiryYears));
        paymentForm.getContinueButton().click();

        paymentForm.checkMonthWrongExpiryDate();
    }

    @Test
    @DisplayName("23. Incomplete month (only 1 digit)")
    void shouldNotSendFormWithIncompleteMonth() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithMonthInvalidLength(1, expiryYears));
        paymentForm.getContinueButton().click();

        paymentForm.checkMonthWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("24. Enter more symbols, than month length")
    void shouldNotEnterMoreSymbolsInMonth() {
        String monthExt = DataHelper.generateNumericCode(3);
        String expected = monthExt.substring(0, 2);

        paymentForm.fillField(paymentForm.getMonth(), monthExt);
        String actual = paymentForm.getFieldValue(paymentForm.getMonth());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("25. Enter invalid symbols in month")
    void shouldNotEnterInvalidSymbolsInMonth() {
        paymentForm.fillField(paymentForm.getMonth(), DataHelper.getInvalidSymbolsForNumericFields());
        String actual = paymentForm.getFieldValue(paymentForm.getMonth());

        assertEquals("", actual);
    }

    @Test
    @DisplayName("26. Zero year")
    void shouldNotSendFormWithZeroYear() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithZeroYear());
        paymentForm.getContinueButton().click();

        paymentForm.checkYearErrorExpired();
    }

    @Test
    @DisplayName("27. Incomplete year")
    void shouldNotSendFormWithIncompleteYear() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithYearInvalidLength(1));
        paymentForm.getContinueButton().click();

        paymentForm.checkYearWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("28. Enter more symbols, than year length")
    void shouldNotEnterMoreSymbolsInYear() {
        String yearExt = DataHelper.generateNumericCode(3);
        String expected = yearExt.substring(0, 2);

        paymentForm.fillField(paymentForm.getYear(), yearExt);
        String actual = paymentForm.getFieldValue(paymentForm.getYear());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("29. Enter invalid symbols in year")
    void shouldNotEnterInvalidSymbolsInYear() {
        paymentForm.fillField(paymentForm.getYear(), DataHelper.getInvalidSymbolsForNumericFields());
        String actual = paymentForm.getFieldValue(paymentForm.getYear());

        assertEquals("", actual);
    }

    @Test
    @DisplayName("30. Previous year (card expired)")
    void shouldNotSendFormWithPrevYear() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(-1));
        paymentForm.getContinueButton().click();

        paymentForm.checkYearErrorExpired();
    }

    @Test
    @DisplayName("31. More than expiry years")
    void shouldNotSendFormWithMoreThanExpiryYear() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(expiryYears + 1));
        paymentForm.getContinueButton().click();

        paymentForm.checkYearWrongExpiryDate();
    }

    @Test
    @DisplayName("32. Card expired in previous month")
    void shouldNotSendFormWithExpiryDateInPrevMonth() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithShiftedMonthFromCurrent(-1));
        paymentForm.getContinueButton().click();

        paymentForm.checkMonthWrongExpiryDate();
    }

    @Test
    @DisplayName("33. Incomplete card owner (only one word)")
    void shouldNotSendFormWithIncompleteCardOwner() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithIncompleteCardOwner(expiryYears));
        paymentForm.getContinueButton().click();

        paymentForm.checkCardOwnerAsWrittenOnCard();
    }

    @Test
    @DisplayName("34. Cyrillic symbols in card owner")
    void shouldNotSendFormWithCyrillicCardOwner() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithInvalidCardOwnerLocale("ru", expiryYears));
        paymentForm.getContinueButton().click();

        paymentForm.checkCardOwnerWrongFormat();
    }

    @Test
    @DisplayName("35. Invalid symbols in card owner")
    void shouldNotSendFormWithInvalidCardOwner() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithInvalidCardOwnerSymbols(expiryYears));
        paymentForm.getContinueButton().click();

        paymentForm.checkCardOwnerWrongFormat();
    }

    @Test
    @DisplayName("36. Spaces instead of card owner")
    void shouldNotSendFormWithSpacesInCardOwner() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithCardOwnerSpaces(expiryYears));
        paymentForm.getContinueButton().click();

        paymentForm.checkCardOwnerErrorEmpty();
    }

    @Test
    @DisplayName("37. Card owner length more than 27 symbols")
    void shouldNotSendFormWithOverlongCardOwner() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithCardOwnerFixedLength(cardOwnerMaxLength + 1, expiryYears));
        paymentForm.getContinueButton().click();

        paymentForm.checkCardOwnerAsWrittenOnCard();
    }


    @Test
    @DisplayName("38. Incomplete CVC")
    void shouldNotSendFormWithIncompleteCVC() {
        paymentForm.fillTheForm(DataHelper.generateCardDataWithCVCInvalidLength(2, expiryYears));
        paymentForm.getContinueButton().click();

        paymentForm.checkCVCErrorIndication();
    }

    @Test
    @DisplayName("39. Enter more symbols, than CVC length")
    void shouldNotEnterMoreSymbolsInCVC() {
        String cvcExt = DataHelper.generateNumericCode(4);
        String expected = cvcExt.substring(0, 3);

        paymentForm.fillField(paymentForm.getCvc(), cvcExt);
        String actual = paymentForm.getFieldValue(paymentForm.getCvc());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("40. Enter invalid symbols in CVC")
    void shouldNotEnterInvalidSymbolsInCVC() {
        paymentForm.fillField(paymentForm.getCvc(), DataHelper.getInvalidSymbolsForNumericFields());
        String actual = paymentForm.getFieldValue(paymentForm.getCvc());

        assertEquals("", actual);
    }

}
