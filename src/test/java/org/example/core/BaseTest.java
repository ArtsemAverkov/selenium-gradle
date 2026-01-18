package org.example.core;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp(){
        driver = DriveFactory.getDriver();
    }

    @AfterMethod
    public void tearDown(){
        DriveFactory.quitDriver();
    }
}
