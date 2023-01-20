package ru.netology.test;

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

        @Test
        @DisplayName("41. Кредит одобрен (Позитивный сценарий)")
        void shouldApprovePaymentWithValidData() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

            creditForm.fillForm(DataHelper.generateValidCardData(expiryYears));
            creditForm.sendForm();

            creditForm.checkSuccessNotification(10); // проверка уведомления в UI
            // проверки на изменение кол-ва записей в БД (что точно добавилась новая запись)
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
            // проверка статуса последней записи
            assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
        }

        @Test
        @DisplayName("42. Отправка формы с годом на 5 лет больше текущего (граничное значение)")
        void shouldSendFormWithMaxExpiryYear() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

            creditForm.fillForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(expiryYears));
            creditForm.sendForm();

            creditForm.checkSuccessNotification(10);
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
        }

        @Test
        @DisplayName("43. Отправка формы с годом на 4 года больше текущего (ниже граничного значения)")
        void shouldSendFormWithExpiryYearBelowMax() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

            creditForm.fillForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(expiryYears - 1));
            creditForm.sendForm();

            creditForm.checkSuccessNotification(10);
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
        }


        @Test
        @DisplayName("44. Отправка формы со сроком действия карты, истекающем в текущем месяце (граничное значение)")
        void shouldSendFormWithExpiryDateInCurrentMonth() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

            creditForm.fillForm(DataHelper.generateCardDataWithShiftedMonthFromCurrent(0));
            creditForm.sendForm();

            creditForm.checkSuccessNotification(10);
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
        }

        @Test
        @DisplayName("45. Отправка формы со сроком действия карты, истекающем в следующем месяце (выше граничного значения)")
        void shouldSendFormWithExpiryDateInNextMonth() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

            creditForm.fillForm(DataHelper.generateCardDataWithShiftedMonthFromCurrent(1));
            creditForm.sendForm();

            creditForm.checkSuccessNotification(10);
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
        }

        @Test
        @DisplayName("46. Отправка формы с именем владельца длиной 26 символов (включая пробел)")
        void shouldSendFormWithCardOwnerInLessThanMaxSymbols() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

            creditForm.fillForm(DataHelper.generateCardDataWithCardOwnerFixedLength(cardOwnerMaxLength - 1, expiryYears));
            creditForm.sendForm();

            creditForm.checkSuccessNotification(10);
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
        }

        @Test
        @DisplayName("47. Отправка формы с именем владельца длиной 27 символов (включая пробел)")
        void shouldSendFormWithCardOwnerMaxLength() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

            creditForm.fillForm(DataHelper.generateCardDataWithCardOwnerFixedLength(cardOwnerMaxLength, expiryYears));
            creditForm.sendForm();

            creditForm.checkSuccessNotification(10);
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
        }

        @Test
        @DisplayName("48. Отправка формы с именем владельца, содержащим дефис")
        void shouldSendFormWithCardOwnerWithHyphen() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

            creditForm.fillForm(DataHelper.generateCardDataWithHyphenCardOwner(expiryYears));
            creditForm.sendForm();

            creditForm.checkSuccessNotification(10);
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
        }

        @Test
        @DisplayName("49. Отправка формы с нулевым кодом CVC/CVV")
        void shouldSendFormWithZeroCVC() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

            creditForm.fillForm(DataHelper.generateCardDataWithZeroCVC(expiryYears));
            creditForm.sendForm();

            creditForm.checkSuccessNotification(10);
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
            assertEquals(approved, SQLHelper.getLastStatusFromCreditsTable());
        }

        @Test
        @DisplayName("50. В кредите отказано (Негативный сценарий)")
        void shouldSendFormWithDeclinedCard() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsCreditsBefore = SQLHelper.getRowsAmountFrom(creditRequestsTable);

            creditForm.fillForm(DataHelper.generateCardDataWithDeclinedCard(expiryYears));
            creditForm.sendForm();

            creditForm.checkErrorNotification(10);
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsCreditsBefore + 1, SQLHelper.getRowsAmountFrom(creditRequestsTable));
            assertEquals(declined, SQLHelper.getLastStatusFromCreditsTable());
        }
    }


    @Test
    @DisplayName("51. Отправка формы без номера карты")
    void shouldNotSendFormWithoutCardNumber() {
        creditForm.fillForm(DataHelper.generateCardDataWithEmptyCard(expiryYears));
        creditForm.sendForm();

        creditForm.checkCardNumberError("Неверный формат", 2);
    }

    @Test
    @DisplayName("52. Отправка формы без указания месяца")
    void shouldNotSendFormWithoutMonth() {
        creditForm.fillForm(DataHelper.generateCardDataWithEmptyMonth(expiryYears));
        creditForm.sendForm();

        creditForm.checkMonthError("Неверный формат", 2);
    }

    @Test
    @DisplayName("53. Отправка формы без указания года")
    void shouldNotSendFormWithoutYear() {
        creditForm.fillForm(DataHelper.generateCardDataWithEmptyYear(expiryYears));
        creditForm.sendForm();

        creditForm.checkYearError("Неверный формат", 2);
    }

    @Test
    @DisplayName("54. Отправка формы без указания владельца карты")
    void shouldNotSendFormWithoutCardOwner() {
        creditForm.fillForm(DataHelper.generateCardDataWithEmptyCardOwner(expiryYears));
        creditForm.sendForm();

        creditForm.checkCardOwnerError("Поле обязательно для заполнения", 2);
    }

    @Test
    @DisplayName("55. Отправка формы без указания CVC/CVV кода")
    void shouldNotSendFormWithoutCVC() {
        creditForm.fillForm(DataHelper.generateCardDataWithEmptyCVC(expiryYears));
        creditForm.sendForm();

        creditForm.checkCVCError("Неверный формат", 2);
    }

    @Test
    @DisplayName("56. Отправка пустой формы")
    void shouldNotSendEmptyForm() {
        creditForm.sendForm();

        creditForm.checkCardNumberError("Неверный формат", 2);
        creditForm.checkMonthError("Неверный формат", 2);
        creditForm.checkYearError("Неверный формат", 2);
        creditForm.checkCardOwnerError("Поле обязательно для заполнения", 2);
        creditForm.checkCVCError("Неверный формат", 2);
    }

    @Test
    @DisplayName("57. Отправка формы с номером карты длиной 15 цифр")
    void shouldNotSendFormWithIncompleteCardNumber() {
        creditForm.fillForm(DataHelper.generateCardDataWithIncompleteNumber(expiryYears, cardNumberMaxLength - 1));
        creditForm.sendForm();

        creditForm.checkCardNumberError("Неверный формат", 2);
    }

    @Test
    @DisplayName("58. Ввод номера карты длиной 17 цифр (без отправки формы)")
    void shouldNotEnterMoreSymbolsInCardNumber() {
        String cardNumberExt = DataHelper.generateNumericCode(cardNumberMaxLength + 1);
        String expected = cardNumberExt.substring(0, cardNumberMaxLength);

        creditForm.fillCard(cardNumberExt);
        String actual = creditForm.getCardValue().replaceAll("\\s+","");

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("59. Ввод в поле для номера карты невалидных символов (без отправки формы)")
    void shouldNotEnterInvalidCardNumber() {
        creditForm.fillCard(DataHelper.getInvalidSymbolsForNumericFields());
        String actual = creditForm.getCardValue().replaceAll("\\s+","");

        assertEquals("", actual);
    }

    @Test
    @DisplayName("60. Отправка формы с номером карты, состоящим из нулей")
    void shouldNotSendFormWithZeroCard() {
        creditForm.fillForm(DataHelper.generateCardDataWithZeroCard(expiryYears));
        creditForm.sendForm();

        creditForm.checkCardNumberError("Неверный формат", 2);
    }

    @Test
    @DisplayName("61. Отправка формы с нулевым месяцем")
    void shouldNotSendFormWithZeroMonth() {
        creditForm.fillForm(DataHelper.generateCardDataWithZeroMonth(expiryYears));
        creditForm.sendForm();

        creditForm.checkMonthError("Неверный формат", 2);
    }

    @Test
    @DisplayName("62. Отправка формы с несуществующим месяцем")
    void shouldNotSendFormWithWrongMonth() {
        creditForm.fillForm(DataHelper.generateCardDataWithWrongMonth(expiryYears));
        creditForm.sendForm();

        creditForm.checkMonthError("Неверно указан срок действия карты", 2);
    }

    @Test
    @DisplayName("63. Отправка формы с месяцем неверного формата: менее 2 цифр")
    void shouldNotSendFormWithIncompleteMonth() {
        creditForm.fillForm(DataHelper.generateCardDataWithMonthInvalidLength(1, expiryYears));
        creditForm.sendForm();

        creditForm.checkMonthError("Неверный формат", 2);
    }

    @Test
    @DisplayName("64. Ввод месяца неверного формата: более 2 цифр (без отправки формы)")
    void shouldNotEnterMoreSymbolsInMonth() {
        String monthExt = DataHelper.generateNumericCode(3);
        String expected = monthExt.substring(0, 2);

        creditForm.fillMonth(monthExt);
        String actual = creditForm.getMonthValue();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("65. Ввод в поле месяца невалидных символов (без отправки формы)")
    void shouldNotEnterInvalidSymbolsInMonth() {
        creditForm.fillMonth(DataHelper.getInvalidSymbolsForNumericFields());
        String actual = creditForm.getMonthValue();

        assertEquals("", actual);
    }

    @Test
    @DisplayName("66. Отправка формы с нулевым годом")
    void shouldNotSendFormWithZeroYear() {
        creditForm.fillForm(DataHelper.generateCardDataWithZeroYear());
        creditForm.sendForm();

        creditForm.checkYearError("Истёк срок действия карты", 2);
    }

    @Test
    @DisplayName("67. Отправка формы с годом неверного формата: менее 2 цифр")
    void shouldNotSendFormWithIncompleteYear() {
        creditForm.fillForm(DataHelper.generateCardDataWithYearInvalidLength(1));
        creditForm.sendForm();

        creditForm.checkYearError("Неверный формат", 2);
    }

    @Test
    @DisplayName("68. Ввод года неверного формата: более 2 цифр (без отправки формы)")
    void shouldNotEnterMoreSymbolsInYear() {
        String yearExt = DataHelper.generateNumericCode(3);
        String expected = yearExt.substring(0, 2);

        creditForm.fillYear(yearExt);
        String actual = creditForm.getYearValue();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("69. Ввод в поле года невалидных символов (без отправки формы)")
    void shouldNotEnterInvalidSymbolsInYear() {
        creditForm.fillYear(DataHelper.getInvalidSymbolsForNumericFields());
        String actual = creditForm.getYearValue();

        assertEquals("", actual);
    }

    @Test
    @DisplayName("70. Отправка формы с предыдущим годом")
    void shouldNotSendFormWithPrevYear() {
        creditForm.fillForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(-1));
        creditForm.sendForm();

        creditForm.checkYearError("Истёк срок действия карты", 2);
    }

    @Test
    @DisplayName("71. Отправка формы с годом на 6 лет больше текущего")
    void shouldNotSendFormWithMoreThanExpiryYear() {
        creditForm.fillForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(expiryYears + 1));
        creditForm.sendForm();

        creditForm.checkYearError("Неверно указан срок действия карты", 2);
    }

    @Test
    @DisplayName("72. Отправка формы с истекшим сроком")
    void shouldNotSendFormWithExpiryDateInPrevMonth() {
        creditForm.fillForm(DataHelper.generateCardDataWithShiftedMonthFromCurrent(-1));
        creditForm.sendForm();

        creditForm.checkMonthError("Неверно указан срок действия карты", 2);
    }

    @Test
    @DisplayName("73. Отправка формы с неполным именем (только одно слово в поле)")
    void shouldNotSendFormWithIncompleteCardOwner() {
        creditForm.fillForm(DataHelper.generateCardDataWithIncompleteCardOwner(expiryYears));
        creditForm.sendForm();

        creditForm.checkCardOwnerError("Введите имя и фамилию, как указано на карте", 2);
    }

    @Test
    @DisplayName("74. Отправка формы с кирилическими символами  имени владельца")
    void shouldNotSendFormWithCyrillicCardOwner() {
        creditForm.fillForm(DataHelper.generateCardDataWithInvalidCardOwnerLocale("ru", expiryYears));
        creditForm.sendForm();

        creditForm.checkCardOwnerError("Только латинские символы (A-Z), пробел и дефис", 2);
    }

    @Test
    @DisplayName("75. Отправка формы с невалидными символами в имени владельца")
    void shouldNotSendFormWithInvalidCardOwner() {
        creditForm.fillForm(DataHelper.generateCardDataWithInvalidCardOwnerSymbols(expiryYears));
        creditForm.sendForm();

        creditForm.checkCardOwnerError("Только латинские символы (A-Z), пробел и дефис", 2);
    }

    @Test
    @DisplayName("76. Отправка формы с пробелами в имени владельца")
    void shouldNotSendFormWithSpacesInCardOwner() {
        creditForm.fillForm(DataHelper.generateCardDataWithCardOwnerSpaces(expiryYears));
        creditForm.sendForm();

        creditForm.checkCardOwnerError("Поле обязательно для заполнения", 2);
    }

    @Test
    @DisplayName("77. Отправка формы с именем владельца 28 символов (включая пробел)")
    void shouldNotSendFormWithOverlongCardOwner() {
        creditForm.fillForm(DataHelper.generateCardDataWithCardOwnerFixedLength(cardOwnerMaxLength + 1, expiryYears));
        creditForm.sendForm();

        creditForm.checkCardOwnerError("Введите имя и фамилию, как указано на карте", 2);
    }


    @Test
    @DisplayName("78. Отправка формы с CVC/CVV неверного формата: менее 3 цифр")
    void shouldNotSendFormWithIncompleteCVC() {
        creditForm.fillForm(DataHelper.generateCardDataWithCVCInvalidLength(2, expiryYears));
        creditForm.sendForm();

        creditForm.checkCVCError("Неверный формат", 2);
    }

    @Test
    @DisplayName("79. Ввод CVC/CVV неверного формата: более 3 цифр")
    void shouldNotEnterMoreSymbolsInCVC() {
        String cvcExt = DataHelper.generateNumericCode(4);
        String expected = cvcExt.substring(0, 3);

        creditForm.fillCVC(cvcExt);
        String actual = creditForm.getCVCValue();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("80. Ввод в поле CVC/CVV невалидных символов (без отправки формы)")
    void shouldNotEnterInvalidSymbolsInCVC() {
        creditForm.fillCVC(DataHelper.getInvalidSymbolsForNumericFields());
        String actual = creditForm.getCVCValue();

        assertEquals("", actual);
    }

}
