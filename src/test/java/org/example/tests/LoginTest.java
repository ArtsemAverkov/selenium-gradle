package org.example.tests;

import io.qameta.allure.testng.AllureTestNg;
import org.example.core.BaseTest;
import org.example.pages.LoginPage;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(AllureTestNg.class)
public class LoginTest extends BaseTest {

    @Test
    public void validLoginTest(){
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login("test@mail.com", "123456");
    }
}
