package ru.netology.pages;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.Keys;
import ru.netology.helpers.DataHelper;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class FormPage {
    private SelenideElement card = $(byText("Номер карты")).parent();
    private SelenideElement month = $(byText("Месяц")).parent();
    private SelenideElement year = $(byText("Год")).parent();
    private SelenideElement cardOwner = $(byText("Владелец")).parent();
    private SelenideElement cvc = $(byText("CVC/CVV")).parent();
    private SelenideElement continueButton = $(byText("Продолжить"));

    private String inputClass = ".input__control";
    private String indicationClass = ".input__sub";

    private SelenideElement notificationSuccess = $(".notification_status_ok");
    private SelenideElement notificationError = $(".notification_status_error");

    public void fillTheForm(DataHelper.CardData data) {
        fillField(card, data.getCardNumber());
        fillField(month, data.getMonth());
        fillField(year, data.getYear());
        fillField(cardOwner, data.getCardOwner());
        fillField(cvc, data.getCvc());
    }

    public void fillField(SelenideElement element, String value) {
        element.$(inputClass).setValue(value);
    }

    public String getFieldValue(SelenideElement element) {
        return element.$(inputClass).getValue();
    }

    public void clearField(SelenideElement element) {
        element.$(inputClass).sendKeys(Keys.CONTROL + "A");
        element.$(inputClass).sendKeys(Keys.BACK_SPACE);
    }

    public void checkSuccessNotification() {
        notificationSuccess.shouldBe(visible);
        notificationSuccess.$(".notification__content").shouldHave(exactText("Операция одобрена Банком."));
    }

    public void checkErrorNotification() {
        notificationError.shouldBe(visible);
        notificationError.$(".notification__content").shouldHave(exactText("Ошибка! Банк отказал в проведении операции."));
    }

    public void checkCardErrorIndication() {
        card.$(indicationClass).should(exist).shouldHave(exactText("Неверный формат"));
    }

    public void checkMonthWrongFormatOrEmpty() {
        month.$(indicationClass).should(exist).shouldHave(exactText("Неверный формат"));
    }

    public void checkMonthWrongExpiryDate() {
        month.$(indicationClass).should(exist).shouldHave(exactText("Неверно указан срок действия карты"));
    }

    public void checkYearWrongFormatOrEmpty() {
        year.$(indicationClass).should(exist).shouldHave(exactText("Неверный формат"));
    }

    public void checkYearErrorExpired() {
        year.$(indicationClass).should(exist).shouldHave(exactText("Истёк срок действия карты"));
    }

    public void checkYearWrongExpiryDate() {
        year.$(indicationClass).should(exist).shouldHave(exactText("Неверно указан срок действия карты"));
    }

    public void checkCardOwnerErrorEmpty() {
        cardOwner.$(indicationClass).should(exist).shouldHave(exactText("Поле обязательно для заполнения"));
    }

    public void checkCardOwnerAsWrittenOnCard() {
        cardOwner.$(indicationClass).should(exist).shouldHave(exactText("Введите имя и фамилию, как указано на карте"));
    }

    public void checkCardOwnerWrongFormat() {
        cardOwner.$(indicationClass).should(exist).shouldHave(exactText("Только латинские символы (A-Z), пробел и дефис"));
    }

    public void checkCVCErrorIndication() {
        cvc.$(indicationClass).should(exist).shouldHave(exactText("Неверный формат"));
    }
}
