package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.web.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class TransferPage {
    private final SelenideElement transferHead = $(byText("Пополнение карты"));
    private final SelenideElement fromInput = $("[data-test-id='from'] input");
    private final SelenideElement transferButton = $("[data-test-id='action-transfer']");
    private final SelenideElement amountInput = $("[data-test-id='amount'] input");
    private final SelenideElement errorMessage = $("[data-test-id='error-message']");

    public TransferPage() {
        transferHead
                .shouldBe(visible);
    }

    public DashboardPage makeValidTransfer(String amountToTransfer, DataHelper.Card cardInfo) {
        makeTransfer(amountToTransfer, cardInfo);
        return new DashboardPage();
    }

    public void makeTransfer(String amountToTransfer, DataHelper.Card cardInfo) {
        amountInput.setValue(amountToTransfer);
        fromInput.setValue(cardInfo.getNumber());
        transferButton.click();
    }

    public void getError(String exp) {
        errorMessage
                .shouldBe(Condition.visible)
                .shouldHave(exactText(exp), Duration.ofSeconds(15));
    }

}
