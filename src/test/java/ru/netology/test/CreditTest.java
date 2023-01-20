package ru.netology.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.*;
import ru.netology.helpers.DataHelper;
import ru.netology.helpers.SQLHelper;
import ru.netology.pages.CreditFormPage;
import ru.netology.pages.DashboardPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreditTest extends TestBase {
    static DashboardPage dashboardPage;
    static CreditFormPage creditForm;

    final String creditRequestsTable = "credit_request_entity";

    @BeforeEach
    void openHost() {
        open(testHost);

        dashboardPage = new DashboardPage();
        creditForm = dashboardPage.openCreditForm();
    }

    @Nested
    class IncreasedTimeout {
        @BeforeEach
        void setTimeout() {
            Configuration.timeout = 10000;
        }

        @Test
        @DisplayName("41. Credit approved (Happy path)")
        void shouldApprovePaymentWithValidData() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

            creditForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
            creditForm.getContinueButton().click();


            creditForm.checkSuccessNotification(); // проверка уведомления в UI
            // проверки на изменение кол-ва записей в БД (что точно добавилась новая запись)
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
            // проверка статуса последней записи
            assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
        }

        @Test
        @DisplayName("42. Max expiry year")
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
        @DisplayName("43. Max-1 expiry year")
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
        @DisplayName("44. Expiry date in current month")
        void shouldSendFormWithExpiryDateInCurrentMonth() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

            creditForm.fillTheForm(DataHelper.generateCardDataWithShiftedMonthFromCurrent(0));
            creditForm.getContinueButton().click();

            creditForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
        }

        @Test
        @DisplayName("45. Expiry date in next month")
        void shouldSendFormWithExpiryDateInNextMonth() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

            creditForm.fillTheForm(DataHelper.generateCardDataWithShiftedMonthFromCurrent(1));
            creditForm.getContinueButton().click();

            creditForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
        }

        @Test
        @DisplayName("46. Card owner length = 26 symbols (max-1)")
        void shouldSendFormWithCardOwnerInLessThanMaxSymbols() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

            creditForm.fillTheForm(DataHelper.generateCardDataWithCardOwnerFixedLength(cardOwnerMaxLength - 1, expiryYears));
            creditForm.getContinueButton().click();

            creditForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
        }

        @Test
        @DisplayName("47. Card owner max length")
        void shouldSendFormWithCardOwnerMaxLength() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

            creditForm.fillTheForm(DataHelper.generateCardDataWithCardOwnerFixedLength(cardOwnerMaxLength, expiryYears));
            creditForm.getContinueButton().click();

            creditForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
        }

        @Test
        @DisplayName("48. Card owner with hyphen")
        void shouldSendFormWithCardOwnerWithHyphen() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

            creditForm.fillTheForm(DataHelper.generateCardDataWithHyphenCardOwner(expiryYears));
            creditForm.getContinueButton().click();

            creditForm.checkSuccessNotification();
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
        }

        @Test
        @DisplayName("49. Valid data with zero CVC")
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
        @DisplayName("50. Declined credit request")
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
    }


    @Test
    @DisplayName("51. Empty card number")
    void shouldNotSendFormWithoutCardNumber() {
        creditForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        creditForm.clearField(creditForm.getCard());
        creditForm.getContinueButton().click();

        creditForm.checkCardErrorIndication();
    }

    @Test
    @DisplayName("52. Empty month")
    void shouldNotSendFormWithoutMonth() {
        creditForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        creditForm.clearField(creditForm.getMonth());
        creditForm.getContinueButton().click();

        creditForm.checkMonthWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("53. Empty year")
    void shouldNotSendFormWithoutYear() {
        creditForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        creditForm.clearField(creditForm.getYear());
        creditForm.getContinueButton().click();

        creditForm.checkYearWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("54. Empty card owner")
    void shouldNotSendFormWithoutCardOwner() {
        creditForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        creditForm.clearField(creditForm.getCardOwner());
        creditForm.getContinueButton().click();

        creditForm.checkCardOwnerErrorEmpty();
    }

    @Test
    @DisplayName("55. Empty CVC/CVV")
    void shouldNotSendFormWithoutCVC() {
        creditForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        creditForm.clearField(creditForm.getCvc());
        creditForm.getContinueButton().click();

        creditForm.checkCVCErrorIndication();
    }

    @Test
    @DisplayName("56. Empty form")
    void shouldNotSendEmptyForm() {
        creditForm.getContinueButton().click();

        creditForm.checkCardErrorIndication();
        creditForm.checkMonthWrongFormatOrEmpty();
        creditForm.checkYearWrongFormatOrEmpty();
        creditForm.checkCardOwnerErrorEmpty();
        creditForm.checkCVCErrorIndication();
    }

    @Test
    @DisplayName("57. Incomplete card number")
    void shouldNotSendFormWithIncompleteCardNumber() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithIncompleteNumber(expiryYears, cardNumberMaxLength - 1));
        creditForm.getContinueButton().click();

        creditForm.checkCardErrorIndication();
    }

    @Test
    @DisplayName("58. Enter more symbols, than card number length")
    void shouldNotEnterMoreSymbolsInCardNumber() {
        String cardNumberExt = DataHelper.generateNumericCode(cardNumberMaxLength + 1);
        String expected = cardNumberExt.substring(0, cardNumberMaxLength);

        creditForm.fillField(creditForm.getCard(), cardNumberExt);
        String actual = creditForm.getFieldValue(creditForm.getCard()).replaceAll("\\s+","");

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("59. Enter invalid symbols in card number")
    void shouldNotEnterInvalidCardNumber() {
        creditForm.fillField(creditForm.getCard(), DataHelper.getInvalidSymbolsForNumericFields());
        String actual = creditForm.getFieldValue(creditForm.getCard()).replaceAll("\\s+","");

        assertEquals("", actual);
    }

    @Test
    @DisplayName("60. Card number consists of zeroes")
    void shouldNotSendFormWithZeroCard() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithZeroCard(expiryYears));
        creditForm.getContinueButton().click();

        creditForm.checkCardErrorIndication();
    }

    @Test
    @DisplayName("61. Zero month")
    void shouldNotSendFormWithZeroMonth() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithZeroMonth(expiryYears));
        creditForm.getContinueButton().click();

        creditForm.checkMonthWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("62. Non-existent month")
    void shouldNotSendFormWithWrongMonth() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithWrongMonth(expiryYears));
        creditForm.getContinueButton().click();

        creditForm.checkMonthWrongExpiryDate();
    }

    @Test
    @DisplayName("63. Incomplete month (only 1 digit)")
    void shouldNotSendFormWithIncompleteMonth() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithMonthInvalidLength(1, expiryYears));
        creditForm.getContinueButton().click();

        creditForm.checkMonthWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("64. Enter more symbols, than month length")
    void shouldNotEnterMoreSymbolsInMonth() {
        String monthExt = DataHelper.generateNumericCode(3);
        String expected = monthExt.substring(0, 2);

        creditForm.fillField(creditForm.getMonth(), monthExt);
        String actual = creditForm.getFieldValue(creditForm.getMonth());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("65. Enter invalid symbols in month")
    void shouldNotEnterInvalidSymbolsInMonth() {
        creditForm.fillField(creditForm.getMonth(), DataHelper.getInvalidSymbolsForNumericFields());
        String actual = creditForm.getFieldValue(creditForm.getMonth());

        assertEquals("", actual);
    }

    @Test
    @DisplayName("66. Zero year")
    void shouldNotSendFormWithZeroYear() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithZeroYear());
        creditForm.getContinueButton().click();

        creditForm.checkYearErrorExpired();
    }

    @Test
    @DisplayName("67. Incomplete year")
    void shouldNotSendFormWithIncompleteYear() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithYearInvalidLength(1));
        creditForm.getContinueButton().click();

        creditForm.checkYearWrongFormatOrEmpty();
    }

    @Test
    @DisplayName("68. Enter more symbols, than year length")
    void shouldNotEnterMoreSymbolsInYear() {
        String yearExt = DataHelper.generateNumericCode(3);
        String expected = yearExt.substring(0, 2);

        creditForm.fillField(creditForm.getYear(), yearExt);
        String actual = creditForm.getFieldValue(creditForm.getYear());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("69. Enter invalid symbols in year")
    void shouldNotEnterInvalidSymbolsInYear() {
        creditForm.fillField(creditForm.getYear(), DataHelper.getInvalidSymbolsForNumericFields());
        String actual = creditForm.getFieldValue(creditForm.getYear());

        assertEquals("", actual);
    }

    @Test
    @DisplayName("70. Previous year (card expired)")
    void shouldNotSendFormWithPrevYear() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(-1));
        creditForm.getContinueButton().click();

        creditForm.checkYearErrorExpired();
    }

    @Test
    @DisplayName("71. More than expiry years")
    void shouldNotSendFormWithMoreThanExpiryYear() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(expiryYears + 1));
        creditForm.getContinueButton().click();

        creditForm.checkYearWrongExpiryDate();
    }

    @Test
    @DisplayName("72. Card expired in previous month")
    void shouldNotSendFormWithExpiryDateInPrevMonth() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithShiftedMonthFromCurrent(-1));
        creditForm.getContinueButton().click();

        creditForm.checkMonthWrongExpiryDate();
    }

    @Test
    @DisplayName("73. Incomplete card owner (only one word)")
    void shouldNotSendFormWithIncompleteCardOwner() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithIncompleteCardOwner(expiryYears));
        creditForm.getContinueButton().click();

        creditForm.checkCardOwnerAsWrittenOnCard();
    }

    @Test
    @DisplayName("74. Cyrillic symbols in card owner")
    void shouldNotSendFormWithCyrillicCardOwner() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithInvalidCardOwnerLocale("ru", expiryYears));
        creditForm.getContinueButton().click();

        creditForm.checkCardOwnerWrongFormat();
    }

    @Test
    @DisplayName("75. Invalid symbols in card owner")
    void shouldNotSendFormWithInvalidCardOwner() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithInvalidCardOwnerSymbols(expiryYears));
        creditForm.getContinueButton().click();

        creditForm.checkCardOwnerWrongFormat();
    }

    @Test
    @DisplayName("76. Spaces instead of card owner")
    void shouldNotSendFormWithSpacesInCardOwner() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithCardOwnerSpaces(expiryYears));
        creditForm.getContinueButton().click();

        creditForm.checkCardOwnerErrorEmpty();
    }

    @Test
    @DisplayName("77. Card owner length more than 27 symbols")
    void shouldNotSendFormWithOverlongCardOwner() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithCardOwnerFixedLength(cardOwnerMaxLength + 1, expiryYears));
        creditForm.getContinueButton().click();

        creditForm.checkCardOwnerAsWrittenOnCard();
    }


    @Test
    @DisplayName("78. Incomplete CVC")
    void shouldNotSendFormWithIncompleteCVC() {
        creditForm.fillTheForm(DataHelper.generateCardDataWithCVCInvalidLength(2, expiryYears));
        creditForm.getContinueButton().click();

        creditForm.checkCVCErrorIndication();
    }

    @Test
    @DisplayName("79. Enter more symbols, than CVC length")
    void shouldNotEnterMoreSymbolsInCVC() {
        String cvcExt = DataHelper.generateNumericCode(4);
        String expected = cvcExt.substring(0, 3);

        creditForm.fillField(creditForm.getCvc(), cvcExt);
        String actual = creditForm.getFieldValue(creditForm.getCvc());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("80. Enter invalid symbols in CVC")
    void shouldNotEnterInvalidSymbolsInCVC() {
        creditForm.fillField(creditForm.getCvc(), DataHelper.getInvalidSymbolsForNumericFields());
        String actual = creditForm.getFieldValue(creditForm.getCvc());

        assertEquals("", actual);
    }

}
