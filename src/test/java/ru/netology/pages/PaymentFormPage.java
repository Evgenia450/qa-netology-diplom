package ru.netology.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$;

public class PaymentFormPage extends FormPage {
    private SelenideElement heading = $$("h3").findBy(text("Оплата по карте"));

    public PaymentFormPage() {
        heading.shouldBe(visible);
    }
}
