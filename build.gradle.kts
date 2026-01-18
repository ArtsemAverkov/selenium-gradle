plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.yaml:snakeyaml:2.2")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.17.0")
    testImplementation("org.testng:testng:7.9.0")
    testImplementation("io.github.bonigarcia:webdrivermanager:5.6.3")
}

tasks.test {
    useTestNG()
}