package org.example.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.config.YmlConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.safari.SafariDriver;

public class DriveFactory {

    private static WebDriver driver;

    public static WebDriver getDriver() {
        if (driver == null) {
            String browser = YmlConfigReader.getString("browser");
            switch (browser.toLowerCase()){
                case "safari":
                    driver= new SafariDriver();
                    break;
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions options = new ChromeOptions();
                    if (YmlConfigReader.getBoolean("headless")){
                        options.addArguments("--headless=new");
                    }
                    driver = new ChromeDriver(options);
                    driver.manage().window().maximize();
            }

        }
        return driver;
    }

    public static void quitDriver(){
        if(driver != null){
            driver.quit();
            driver = null;
        }
    }
}
