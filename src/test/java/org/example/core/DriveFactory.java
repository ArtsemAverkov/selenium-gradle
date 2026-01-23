package org.example.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.config.YmlConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public class DriveFactory {

    private static WebDriver driver;

    public static WebDriver getDriver() {
        if (driver == null) {
            String runMode = System.getProperty(
                    "run.mode",
                    System.getenv().getOrDefault("RUN_MODE", "local"));
            String browser = YmlConfigReader.getString("browser");

            switch (browser.toLowerCase()){
                case "chrome":
                    if ("remote".equalsIgnoreCase(runMode)) {
                        driver = createRemoteDriver();
                    } else {
                        driver = createLocalDriver();
                    }
            }

        }
        return driver;
    }


    private static WebDriver createRemoteDriver() {
        try {
            ChromeOptions options = new ChromeOptions();

            if (YmlConfigReader.getBoolean("headless")) {
                options.addArguments("--headless=new");
            }

            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            String gridUrl = System.getProperty(
                    "selenium.remote.url",
                    "http://selenium-hub:4444/wd/hub"
            );

            return new RemoteWebDriver(new URL(gridUrl), options);

        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать RemoteWebDriver", e);
        }
    }

    private static WebDriver createLocalDriver(){
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (YmlConfigReader.getBoolean("headless")){
            options.addArguments("--headless=new");
        }
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
