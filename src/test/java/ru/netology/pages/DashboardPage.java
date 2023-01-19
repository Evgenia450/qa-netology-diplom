package ru.netology.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class DashboardPage {
    private SelenideElement buttonPayment = $(byText("Купить"));
    private SelenideElement buttonCredit = $(byText("Купить в кредит"));

    // при создании страницы - кнопки "Купить" и "Купить в кредит" должны быть кликабельны
    public DashboardPage() {
        buttonPayment.shouldBe(enabled);
        buttonCredit.shouldBe(enabled);
    }

    // открыть форму для обычного платежа
    public PaymentFormPage openPaymentForm() {
        buttonPayment.click();
        return new PaymentFormPage();
    }

    // открыть форму для оформления кредита
    public CreditFormPage openCreditForm() {
        buttonCredit.click();
        return new CreditFormPage();
    }
}
