package ru.netology.web.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyTransferTest {
    DashboardPage dashboardPage;
    LoginPage loginPage;

    @BeforeEach
    void setUp() {
        loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
        Configuration.holdBrowserOpen = true;
    }

    @AfterEach
    void clear() {
        clearBrowserCookies();
        clearBrowserLocalStorage();
    }

    @Test
    void shouldTransferFromSecondToFirst() {
        var firstCardData = DataHelper.getFirstCard();
        var secondCardData = DataHelper.getSecondCard();

        var firstCardBalance = dashboardPage.getBalance(firstCardData);
        var secondCardBalance = dashboardPage.getBalance(secondCardData);

        var amount = DataHelper.generationValidAmount(secondCardBalance);

        var expectedFirstCardBalance = firstCardBalance + amount;
        var expectedSecondCardBalance = secondCardBalance - amount;

        var transferPage = dashboardPage.selectCardToTransfer(firstCardData);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), secondCardData);

        var actualFirstCardBalance = dashboardPage.getBalance(firstCardData);
        var actualSecondCardBalance = dashboardPage.getBalance(secondCardData);

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldTransferFromFirstToSecond() {
        var firstCardData = DataHelper.getFirstCard();
        var secondCardData = DataHelper.getSecondCard();

        var firstCardBalance = dashboardPage.getBalance(firstCardData);
        var secondCardBalance = dashboardPage.getBalance(secondCardData);

        var amount = DataHelper.generationValidAmount(firstCardBalance);

        var expectedFirstCardBalance = firstCardBalance - amount;
        var expectedSecondCardBalance = secondCardBalance + amount;

        var transferPage = dashboardPage.selectCardToTransfer(secondCardData);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardData);

        var actualFirstCardBalance = dashboardPage.getBalance(firstCardData);
        var actualSecondCardBalance = dashboardPage.getBalance(secondCardData);

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance,actualSecondCardBalance);
    }

    @Test
    void shouldNotTransferMoneyInvalidSum() {
        var firstCardData = DataHelper.getFirstCard();
        var secondCardData = DataHelper.getSecondCard();

        var secondCardBalance = dashboardPage.getBalance(secondCardData);

        var sum = DataHelper.generationInvalidAmount(secondCardBalance);

        var transferPage = dashboardPage.selectCardToTransfer(firstCardData);
        transferPage.makeTransfer(String.valueOf(sum), secondCardData);
        transferPage.getError("Отказ в совершении операции. Недостаточно средств на карте.");
    }

}