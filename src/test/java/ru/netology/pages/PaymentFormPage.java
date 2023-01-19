package ru.netology.pages;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.Keys;
import ru.netology.helpers.DataHelper;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Getter
public class PaymentFormPage extends FormPage {
    private SelenideElement heading = $$("h3").findBy(text("Оплата по карте"));

    public PaymentFormPage() {
        heading.shouldBe(visible);
    }
}
