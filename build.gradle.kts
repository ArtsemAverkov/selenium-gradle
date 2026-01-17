plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
// Selenium
    testImplementation("org.seleniumhq.selenium:selenium-java:4.17.0")
// TestNG
    testImplementation("org.testng:testng:7.9.0")
// WebDriverManager
    testImplementation("io.github.bonigarcia:webdrivermanager:5.6.3")
}

tasks.test {
    useTestNG()
}