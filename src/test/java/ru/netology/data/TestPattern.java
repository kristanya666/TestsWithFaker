package ru.netology.data;


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;


import java.time.Duration;

import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.openqa.selenium.Keys.BACK_SPACE;

class DeliveryTest {


    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }


    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);


        //ввод данных на доставку карты
        SelenideElement form = $("form.form");
        form.$("[data-test-id=city] input").setValue(validUser.getCity());
        form.$("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME));
        form.$("[data-test-id=date] input").sendKeys(BACK_SPACE);
        form.$("[data-test-id=date] input").setValue(firstMeetingDate);
        form.$("[data-test-id=name] input").setValue(validUser.getName());
        form.$("[data-test-id=phone] input").setValue(validUser.getPhone());
        form.$("[data-test-id=agreement]").click();
        form.$("button.button").click();


        //проверка уведомления об успешной запланированной доставке карты
        SelenideElement notification = $("[data-test-id=success-notification]");
        $(withText("Успешно!")).shouldBe(Condition.visible, Duration.ofSeconds(15));
        notification.$("div.notification__content").shouldHave(Condition.exactText("Встреча успешно запланирована на " + firstMeetingDate));

        //выбор другой даты доставки карты
        form.$("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME));
        form.$("[data-test-id=date] input").sendKeys(BACK_SPACE);
        form.$("[data-test-id=date] input").setValue(secondMeetingDate);
        form.$("button.button").click();

        //подтверждение перепланирования доставки
        SelenideElement notificationSecond = $("[data-test-id=replan-notification]");
        $(withText("Необходимо подтверждение")).shouldBe(Condition.visible, Duration.ofSeconds(15));
        $(byXpath("//*[@id=\"root\"]/div/div[2]/div[3]/button")).click();

        //проверка уведомления о перезапланированной доставке
        $(withText("Успешно!")).shouldBe(Condition.visible, Duration.ofSeconds(15));
        notification.$("div.notification__content").shouldHave(Condition.exactText("Встреча успешно запланирована на " + secondMeetingDate));


    }


}
