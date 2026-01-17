package org.example.tests;

import org.example.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

public class LoginTest {

    WebDriver driver = new ChromeDriver(); // или другой драйвер
    LoginPage loginPage = new LoginPage(driver);

    @Test
    public void validLoginTest(){
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login("test@mail.com", "123456");
    }
}
