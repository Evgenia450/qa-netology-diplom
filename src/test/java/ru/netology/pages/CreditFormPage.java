package ru.netology.pages;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Getter
public class CreditFormPage extends FormPage {
    private SelenideElement heading = $$("h3").findBy(text("Кредит по данным карты"));

    public CreditFormPage() {
        heading.shouldBe(visible);
    }
}
