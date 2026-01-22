package org.example.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public class DriveFactory {

    private static WebDriver driver;

    public static WebDriver getDriver() {
        if (driver != null) {
            return driver;
        }

        RunMode runMode = resolveRunMode();

        log("Run mode = " + runMode);

        switch (runMode) {
            case REMOTE:
                driver = createRemoteDriver();
                break;

            case LOCAL:
                driver = createLocalDriver();
                break;

            default:
                throw new IllegalStateException("Unsupported run mode: " + runMode);
        }

        return driver;
    }

    

    private static RunMode resolveRunMode() {

        String mode = System.getProperty("run.mode");


        if (mode == null) {
            mode = System.getenv("RUN_MODE");
        }


        if (mode == null && isCI()) {
            mode = "remote";
        }


        if (mode == null) {
            mode = "local";
        }

        return RunMode.from(mode);
    }

    private static boolean isCI() {
        return System.getenv("JENKINS_HOME") != null
                || System.getenv("CI") != null;
    }



    private static WebDriver createRemoteDriver() {
        try {
            ChromeOptions options = new ChromeOptions();

            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            if (Boolean.parseBoolean(System.getProperty("headless", "true"))) {
                options.addArguments("--headless=new");
            }

            String gridUrl = System.getProperty(
                    "selenium.remote.url",
                    "http://selenium-hub:4444/wd/hub"
            );

            log("Using REMOTE WebDriver");
            log("Grid URL = " + gridUrl);

            return new RemoteWebDriver(new URL(gridUrl), options);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create RemoteWebDriver", e);
        }
    }

    private static WebDriver createLocalDriver() {
        if (isCI()) {
            throw new IllegalStateException(
                    "ChromeDriver is forbidden in CI. Use REMOTE mode."
            );
        }

        log("Using LOCAL ChromeDriver");

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
        }

        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        return driver;
    }



    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }



    private static void log(String message) {
        System.out.println("🚀 [DriverFactory] " + message);
    }



    enum RunMode {
        LOCAL, REMOTE;

        static RunMode from(String value) {
            return RunMode.valueOf(value.trim().toUpperCase());
        }
    }
}
