package ru.netology.test;

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

        @Test
        @DisplayName("1. Оплата одобрена (Позитивный сценарий)")
        void shouldApprovePaymentWithValidData() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillForm(DataHelper.generateValidCardData(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkSuccessNotification(10); // проверка уведомления в UI

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
        @DisplayName("2. Отправка формы с годом на 5 лет больше текущего (граничное значение)")
        void shouldSendFormWithMaxExpiryYear() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkSuccessNotification(10);
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(approved, lastEntry.getStatus());
        }

        @Test
        @DisplayName("3. Отправка формы с годом на 4 года больше текущего (ниже граничного значения)")
        void shouldSendFormWithExpiryYearBelowMax() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(expiryYears - 1));
            paymentForm.sendForm();

            paymentForm.checkSuccessNotification(10);
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(approved, lastEntry.getStatus());
        }


        @Test
        @DisplayName("4. Отправка формы со сроком действия карты, истекающем в текущем месяце (граничное значение)")
        void shouldSendFormWithExpiryDateInCurrentMonth() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillForm(DataHelper.generateCardDataWithShiftedMonthFromCurrent(0));
            paymentForm.sendForm();

            paymentForm.checkSuccessNotification(10);
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(approved, lastEntry.getStatus());
        }

        @Test
        @DisplayName("5. Отправка формы со сроком действия карты, истекающем в следующем месяце (выше граничного значения)")
        void shouldSendFormWithExpiryDateInNextMonth() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillForm(DataHelper.generateCardDataWithShiftedMonthFromCurrent(1));
            paymentForm.sendForm();

            paymentForm.checkSuccessNotification(10);
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(approved, lastEntry.getStatus());
        }

        @Test
        @DisplayName("6. Отправка формы с именем владельца длиной 26 символов (включая пробел)")
        void shouldSendFormWithCardOwnerInLessThanMaxSymbols() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillForm(DataHelper.generateCardDataWithCardOwnerFixedLength(cardOwnerMaxLength - 1, expiryYears));
            paymentForm.sendForm();

            paymentForm.checkSuccessNotification(10);
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(approved, lastEntry.getStatus());
        }

        @Test
        @DisplayName("7. Отправка формы с именем владельца длиной 27 символов (включая пробел)")
        void shouldSendFormWithCardOwnerMaxLength() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillForm(DataHelper.generateCardDataWithCardOwnerFixedLength(cardOwnerMaxLength, expiryYears));
            paymentForm.sendForm();

            paymentForm.checkSuccessNotification(10);
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(approved, lastEntry.getStatus());
        }

        @Test
        @DisplayName("8. Отправка формы с с фамилией владельца, содержащим дефис")
        void shouldSendFormWithCardOwnerWithHyphen() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillForm(DataHelper.generateCardDataWithHyphenCardOwner(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkSuccessNotification(10);
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(approved, lastEntry.getStatus());
        }

        @Test
        @DisplayName("9. Отправка формы с нулевым кодом CVC")
        void shouldSendFormWithZeroCVC() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillForm(DataHelper.generateCardDataWithZeroCVC(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkSuccessNotification(10);
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(approved, lastEntry.getStatus());
        }

        @Test
        @DisplayName("10. Оплата отклонена")
        void shouldSendFormWithDeclinedCard() {
            long rowsOrdersBefore = SQLHelper.getRowsAmountFrom(ordersTable);
            long rowsPaymentsBefore = SQLHelper.getRowsAmountFrom(paymentsTable);

            paymentForm.fillForm(DataHelper.generateCardDataWithDeclinedCard(expiryYears));
            paymentForm.sendForm();

            paymentForm.checkErrorNotification(10);
            assertEquals(rowsOrdersBefore + 1, SQLHelper.getRowsAmountFrom(ordersTable));
            assertEquals(rowsPaymentsBefore + 1, SQLHelper.getRowsAmountFrom(paymentsTable));
            SQLHelper.Payment lastEntry = SQLHelper.getLastEntryFromPaymentsTable();
            assertEquals(expectedPrice, lastEntry.getAmount());
            assertEquals(declined, lastEntry.getStatus());
        }
    }

    @Test
    @DisplayName("11. Отправка формы без номера карты")
    void shouldNotSendFormWithoutCardNumber() {
        paymentForm.fillForm(DataHelper.generateCardDataWithEmptyCard(expiryYears));
        paymentForm.sendForm();

        paymentForm.checkCardNumberError("Неверный формат", 2);
    }

    @Test
    @DisplayName("12. Отправка формы без указания месяца")
    void shouldNotSendFormWithoutMonth() {
        paymentForm.fillForm(DataHelper.generateCardDataWithEmptyMonth(expiryYears));
        paymentForm.sendForm();

        paymentForm.checkMonthError("Неверный формат",2);
    }

    @Test
    @DisplayName("13. Отправка формы без указания года")
    void shouldNotSendFormWithoutYear() {
        paymentForm.fillForm(DataHelper.generateCardDataWithEmptyYear(expiryYears));
        paymentForm.sendForm();

        paymentForm.checkYearError("Неверный формат",2);
    }

    @Test
    @DisplayName("14. Отправка формы без указания владельца карты")
    void shouldNotSendFormWithoutCardOwner() {
        paymentForm.fillForm(DataHelper.generateCardDataWithEmptyCardOwner(expiryYears));
        paymentForm.sendForm();

        paymentForm.checkCardOwnerError("Поле обязательно для заполнения", 2);
    }

    @Test
    @DisplayName("15. Отправка формы без указания CVC/CVV кода")
    void shouldNotSendFormWithoutCVC() {
        paymentForm.fillForm(DataHelper.generateCardDataWithEmptyCVC(expiryYears));
        paymentForm.sendForm();

        paymentForm.checkCVCError("Неверный формат",2);
    }

    @Test
    @DisplayName("16. Отправка пустой формы")
    void shouldNotSendEmptyForm() {
        paymentForm.sendForm();

        paymentForm.checkCardNumberError("Неверный формат",2);
        paymentForm.checkMonthError("Неверный формат", 2);
        paymentForm.checkYearError("Неверный формат", 2);
        paymentForm.checkCardOwnerError("Поле обязательно для заполнения", 2);
        paymentForm.checkCVCError("Неверный формат", 2);
    }

    @Test
    @DisplayName("17. Неполный номер карты")
    void shouldNotSendFormWithIncompleteCardNumber() {
        paymentForm.fillForm(DataHelper.generateCardDataWithIncompleteNumber(expiryYears, cardNumberMaxLength - 1));
        paymentForm.sendForm();

        paymentForm.checkCardNumberError("Неверный формат", 2);
    }

    @Test
    @DisplayName("18. Ввод номера карты длиной 17 цифр (без отправки формы)")
    void shouldNotEnterMoreSymbolsInCardNumber() {
        String cardNumberExt = DataHelper.generateNumericCode(cardNumberMaxLength + 1);
        String expected = cardNumberExt.substring(0, cardNumberMaxLength);

        paymentForm.fillCard(cardNumberExt);
        String actual = paymentForm.getCardValue().replaceAll("\\s+","");

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("19. Ввод в поле для номера карты невалидных символов (без отправки формы)")
    void shouldNotEnterInvalidCardNumber() {
        paymentForm.fillCard(DataHelper.getInvalidSymbolsForNumericFields());
        String actual = paymentForm.getCardValue().replaceAll("\\s+","");

        assertEquals("", actual);
    }

    @Test
    @DisplayName("20. Отправка формы с номером карты, состоящим из нулей")
    void shouldNotSendFormWithZeroCard() {
        paymentForm.fillForm(DataHelper.generateCardDataWithZeroCard(expiryYears));
        paymentForm.sendForm();

        paymentForm.checkCardNumberError("Неверный формат", 2);
    }

    @Test
    @DisplayName("21. Отправка формы с нулевым месяцем")
    void shouldNotSendFormWithZeroMonth() {
        paymentForm.fillForm(DataHelper.generateCardDataWithZeroMonth(expiryYears));
        paymentForm.sendForm();

        paymentForm.checkMonthError("Неверный формат", 2);
    }

    @Test
    @DisplayName("22. Отправка формы с несуществующим месяцем")
    void shouldNotSendFormWithWrongMonth() {
        paymentForm.fillForm(DataHelper.generateCardDataWithWrongMonth(expiryYears));
        paymentForm.sendForm();

        paymentForm.checkMonthError("Неверно указан срок действия карты", 2);
    }

    @Test
    @DisplayName("23. Отправка формы с месяцем неверного формата: менее 2 цифр")
    void shouldNotSendFormWithIncompleteMonth() {
        paymentForm.fillForm(DataHelper.generateCardDataWithMonthInvalidLength(1, expiryYears));
        paymentForm.sendForm();

        paymentForm.checkMonthError("Неверный формат", 2);
    }

    //
    @Test
    @DisplayName("24. Ввод месяца неверного формата: более 2 цифр (без отправки формы)")
    void shouldNotEnterMoreSymbolsInMonth() {
        String monthExt = DataHelper.generateNumericCode(3);
        String expected = monthExt.substring(0, 2);

        paymentForm.fillMonth(monthExt);
        String actual = paymentForm.getMonthValue();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("25. Ввод в поле месяца невалидных символов (без отправки формы)")
    void shouldNotEnterInvalidSymbolsInMonth() {
        paymentForm.fillMonth(DataHelper.getInvalidSymbolsForNumericFields());
        String actual = paymentForm.getMonthValue();

        assertEquals("", actual);
    }

    @Test
    @DisplayName("26. Отправка формы с нулевым годом")
    void shouldNotSendFormWithZeroYear() {
        paymentForm.fillForm(DataHelper.generateCardDataWithZeroYear());
        paymentForm.sendForm();

        paymentForm.checkYearError("Истёк срок действия карты", 2);
    }

    @Test
    @DisplayName("27. Отправка формы с годом неверного формата: менее 2 цифр")
    void shouldNotSendFormWithIncompleteYear() {
        paymentForm.fillForm(DataHelper.generateCardDataWithYearInvalidLength(1));
        paymentForm.sendForm();

        paymentForm.checkYearError("Неверный формат", 2);
    }

    @Test
    @DisplayName("28. Ввод года неверного формата: более 2 цифр (без отправки формы)")
    void shouldNotEnterMoreSymbolsInYear() {
        String yearExt = DataHelper.generateNumericCode(3);
        String expected = yearExt.substring(0, 2);

        paymentForm.fillYear(yearExt);
        String actual = paymentForm.getYearValue();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("29. Ввод в поле года невалидных символов (без отправки формы)")
    void shouldNotEnterInvalidSymbolsInYear() {
        paymentForm.fillYear(DataHelper.getInvalidSymbolsForNumericFields());
        String actual = paymentForm.getYearValue();

        assertEquals("", actual);
    }

    @Test
    @DisplayName("30. Отправка формы с предыдущим годом")
    void shouldNotSendFormWithPrevYear() {
        paymentForm.fillForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(-1));
        paymentForm.sendForm();

        paymentForm.checkYearError("Истёк срок действия карты", 2);
    }

    @Test
    @DisplayName("31. Отправка формы с годом на 6 лет больше текущего")
    void shouldNotSendFormWithMoreThanExpiryYear() {
        paymentForm.fillForm(DataHelper.generateCardDataWithShiftedYearFromCurrent(expiryYears + 1));
        paymentForm.sendForm();

        paymentForm.checkYearError("Неверно указан срок действия карты", 2);
    }

    @Test
    @DisplayName("32. Отправка формы с истекшим сроком")
    void shouldNotSendFormWithExpiryDateInPrevMonth() {
        paymentForm.fillForm(DataHelper.generateCardDataWithShiftedMonthFromCurrent(-1));
        paymentForm.sendForm();

        paymentForm.checkMonthError("Неверно указан срок действия карты", 2);
    }

    @Test
    @DisplayName("33. Отправка формы с неполным именем (только одно слово в поле)")
    void shouldNotSendFormWithIncompleteCardOwner() {
        paymentForm.fillForm(DataHelper.generateCardDataWithIncompleteCardOwner(expiryYears));
        paymentForm.sendForm();

        paymentForm.checkCardOwnerError("Введите имя и фамилию, как указано на карте", 2);
    }

    @Test
    @DisplayName("34. Отправка формы с кирилическими символами  имени владельца")
    void shouldNotSendFormWithCyrillicCardOwner() {
        paymentForm.fillForm(DataHelper.generateCardDataWithInvalidCardOwnerLocale("ru", expiryYears));
        paymentForm.sendForm();

        paymentForm.checkCardOwnerError("Только латинские символы (A-Z), пробел и дефис", 2);
    }

    @Test
    @DisplayName("35. Отправка формы с невалидными символами в имени владельца")
    void shouldNotSendFormWithInvalidCardOwner() {
        paymentForm.fillForm(DataHelper.generateCardDataWithInvalidCardOwnerSymbols(expiryYears));
        paymentForm.sendForm();

        paymentForm.checkCardOwnerError("Только латинские символы (A-Z), пробел и дефис", 2);
    }

    @Test
    @DisplayName("36. Отправка формы с пробелами в имени владельца")
    void shouldNotSendFormWithSpacesInCardOwner() {
        paymentForm.fillForm(DataHelper.generateCardDataWithCardOwnerSpaces(expiryYears));
        paymentForm.sendForm();

        paymentForm.checkCardOwnerError("Поле обязательно для заполнения", 2);
    }

    @Test
    @DisplayName("37. Отправка формы с именем владельца 28 символов (включая пробел)")
    void shouldNotSendFormWithOverlongCardOwner() {
        paymentForm.fillForm(DataHelper.generateCardDataWithCardOwnerFixedLength(cardOwnerMaxLength + 1, expiryYears));
        paymentForm.sendForm();

        paymentForm.checkCardOwnerError("Введите имя и фамилию, как указано на карте", 2);
    }


    @Test
    @DisplayName("38. Отправка формы с CVC/CVV неверного формата: менее 3 цифр")
    void shouldNotSendFormWithIncompleteCVC() {
        paymentForm.fillForm(DataHelper.generateCardDataWithCVCInvalidLength(2, expiryYears));
        paymentForm.sendForm();

        paymentForm.checkCVCError("Неверный формат", 2);
    }

    @Test
    @DisplayName("39. Ввод CVC/CVV неверного формата: более 3 цифр (без отправки формы)")
    void shouldNotEnterMoreSymbolsInCVC() {
        String cvcExt = DataHelper.generateNumericCode(4);
        String expected = cvcExt.substring(0, 3);

        paymentForm.fillCVC(cvcExt);
        String actual = paymentForm.getCVCValue();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("40. Ввод в поле CVC/CVV невалидных символов (без отправки формы)")
    void shouldNotEnterInvalidSymbolsInCVC() {
        paymentForm.fillCVC(DataHelper.getInvalidSymbolsForNumericFields());
        String actual = paymentForm.getCVCValue();

        assertEquals("", actual);
    }

}
