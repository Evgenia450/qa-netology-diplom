package ru.netology.pages;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.helpers.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;


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

    public void fillForm(DataHelper.CardData data) {
        fillCard (data.getCardNumber());
        fillMonth(data.getMonth());
        fillYear(data.getYear());
        fillCardOwner(data.getCardOwner());
        fillCVC(data.getCvc());
    }

    public void sendForm() {
        continueButton.click();
    }

    public void fillCard(String value) {
        card.$(inputClass).setValue(value);
    }

    public void fillMonth(String value) {
        month.$(inputClass).setValue(value);
    }

    public void fillYear(String value) {
        year.$(inputClass).setValue(value);
    }

    public void fillCardOwner(String value) {
        cardOwner.$(inputClass).setValue(value);
    }

    public void fillCVC(String value) {
        cvc.$(inputClass).setValue(value);
    }

    public String getCardValue() {
        return card.$(inputClass).getValue();
    }

    public String getMonthValue() {
        return month.$(inputClass).getValue();
    }

    public String getYearValue() {
        return year.$(inputClass).getValue();
    }

    public String getCVCValue() {
        return cvc.$(inputClass).getValue();
    }

    public void clearField(SelenideElement element) {
        element.$(inputClass).sendKeys(Keys.CONTROL + "A");
        element.$(inputClass).sendKeys(Keys.BACK_SPACE);
    }

    public void checkSuccessNotification(int durationOfSec) {
        notificationSuccess.$(".notification__content").shouldHave(exactText("Операция одобрена Банком."), Duration.ofSeconds(durationOfSec));
        notificationSuccess.shouldBe(visible);
    }

    public void checkErrorNotification(int durationOfSec) {
        notificationError.$(".notification__content").shouldHave(exactText("Ошибка! Банк отказал в проведении операции."), Duration.ofSeconds(durationOfSec));
        notificationError.shouldBe(visible);
    }

    public void checkCardNumberError(String errorText, int durationOfSec) {
        card.$(indicationClass).shouldHave(exactText(errorText), Duration.ofSeconds(durationOfSec)).shouldBe(visible);
    }

    public void checkMonthError(String errorText, int durationOfSec) {
        month.$(indicationClass).shouldHave(exactText(errorText), Duration.ofSeconds(durationOfSec)).shouldBe(visible);
    }

    public void checkYearError(String errorText, int durationOfSec) {
        year.$(indicationClass).shouldHave(exactText(errorText), Duration.ofSeconds(durationOfSec)).shouldBe(visible);
    }

    public void checkCardOwnerError(String errorText, int durationOfSec) {
        cardOwner.$(indicationClass).shouldHave(exactText(errorText), Duration.ofSeconds(durationOfSec)).shouldBe(visible);
    }

    public void checkCVCError(String errorText, int durationOfSec) {
        cvc.$(indicationClass).shouldHave(exactText(errorText), Duration.ofSeconds(durationOfSec)).shouldBe(visible);
    }
}
