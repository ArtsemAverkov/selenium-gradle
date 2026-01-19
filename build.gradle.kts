plugins {
    id("java")
    id("io.qameta.allure") version "2.11.2"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.qameta.allure:allure-testng:2.25.0")
    testImplementation("io.qameta.allure:allure-java-commons:2.25.0")
    testImplementation("org.yaml:snakeyaml:2.2")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.17.0")
    testImplementation("org.testng:testng:7.9.0")
    testImplementation("io.github.bonigarcia:webdrivermanager:5.6.3")
}


tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    compileTestJava {
        options.encoding = "UTF-8"
    }

    test {
        useTestNG() {

            outputDirectory = file("$buildDir/testng-output")
            useDefaultListeners = true
        }


        systemProperties = mapOf(
            "browser" to (System.getProperty("browser") ?: "chrome"),
            "headless" to (System.getProperty("headless") ?: "false"),
            "allure.results.directory" to "$buildDir/allure-results"
        )


        testLogging {
            events("passed", "failed", "skipped", "standard_out", "standard_error")
            showExceptions = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showCauses = true
            showStackTraces = true
        }


        minHeapSize = "512m"
        maxHeapSize = "2048m"


        finalizedBy("allureReport")
    }

    allure {
        version = "2.25.0"
        adapter {
            frameworks {
                testng
            }
        }
        report {
            reportDir.set(file("$buildDir/reports/allure-report"))
        }
    }


    register<Test>("runInJenkins") {
        group = "verification"
        description = "Run tests with Jenkins defaults"

        useTestNG()

        systemProperties = mapOf(
            "browser" to "chrome",
            "headless" to "true",
            "allure.results.directory" to "$buildDir/allure-results"
        )
    }
}

