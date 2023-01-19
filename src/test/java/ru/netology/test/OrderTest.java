package ru.netology.test;

import com.codeborne.selenide.Configuration;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import ru.netology.helpers.DataHelper;
import ru.netology.helpers.SQLHelper;
import ru.netology.pages.DashboardPage;
import ru.netology.pages.PaymentFormPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTest {
    static DashboardPage dashboardPage;
    static PaymentFormPage paymentForm;
    String approved = "APPROVED";
    String declined = "DECLINED";
    int expiryYears = 5;

    String ordersTable = "order_entity";
    String paymentsTable = "payment_entity";

    @BeforeAll
    static void setTimeout() {
        Configuration.timeout = 10000;
    }

    @BeforeEach
    void openHost() {
        open("http://localhost:8080");

        dashboardPage = new DashboardPage();
        paymentForm = dashboardPage.openPaymentForm();
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
        assertEquals(approved, SQLHelper.getLastStatusFrom(paymentsTable));
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
        assertEquals(approved, SQLHelper.getLastStatusFrom(paymentsTable));
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
        assertEquals(approved, SQLHelper.getLastStatusFrom(paymentsTable));
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
        assertEquals(approved, SQLHelper.getLastStatusFrom(paymentsTable));
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
        assertEquals(approved, SQLHelper.getLastStatusFrom(paymentsTable));
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
        assertEquals(approved, SQLHelper.getLastStatusFrom(paymentsTable));
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
        assertEquals(declined, SQLHelper.getLastStatusFrom(paymentsTable));
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

        paymentForm.checkMonthErrorEmpty();
    }

    @Test
    @DisplayName("13. Should not send form without year")
    void shouldNotSendFormWithoutYear() {
        paymentForm.fillTheForm(DataHelper.generateValidCardData(expiryYears));
        paymentForm.clearField(paymentForm.getYear());
        paymentForm.getContinueButton().click();

        paymentForm.checkYearErrorEmpty();
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
        paymentForm.checkMonthErrorEmpty();
        paymentForm.checkYearErrorEmpty();
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

    @AfterAll
    @SneakyThrows
    static void cleanDB() {
        SQLHelper.cleanDatabase();
    }
}
