package org.example.tests;

import org.example.core.BaseTest;
import org.example.pages.LoginPage;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test
    public void validLoginTest(){
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login("test@mail.com", "123456");
    }
}
