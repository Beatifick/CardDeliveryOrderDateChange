package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

class CardDeliveryTest {

    @Test
    void shouldRescheduleMeeting() {
        open("http://localhost:9999");

        var user = DataGenerator.Registration.generateUser("ru");
        var firstDate = DataGenerator.generateDate(3);
        var secondDate = DataGenerator.generateDate(7);

// первое бронирование
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, firstDate);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $("button.button").click();

// проверка первого уведомления
        $("[data-test-id=success-notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text(firstDate));

// меняем дату, отправляем снова
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, secondDate);
        $("button.button").click();

// проверка блока перепланирования
        var replanNotification = $("[data-test-id=replan-notification]");
        replanNotification.shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("У вас уже запланирована встреча"));

//перепланировать
        replanNotification.$("button.button").click();

//проверка второго уведомления
        $("[data-test-id=success-notification]").should(Condition.appear, Duration.ofSeconds(15))
                .shouldHave(Condition.text(secondDate));
    }
}
