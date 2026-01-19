package org.example.pages;

import io.qameta.allure.Step;
import org.example.config.YmlConfigReader;
import org.example.core.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage extends BaseTest{

    private WebDriverWait wait;

    private By username = By.id("username");
    private By password = By.id("password");
    private By loginButton = By.cssSelector("button[type='submit']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(YmlConfigReader.getInt("timeout")));
    }

    @Step("Открываем страницу логина")
    public void open(){
        driver.get(YmlConfigReader.getString("baseUrl"));
    }

    @Step("Логин: {user}")
    public void login(String user, String pass) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(username))
                .sendKeys(user);

        wait.until(ExpectedConditions.visibilityOfElementLocated(password))
                .sendKeys(pass);

        wait.until(ExpectedConditions.elementToBeClickable(loginButton))
                .click();
    }

}
