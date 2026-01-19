pipeline {
    agent any

    // Вариант 1: Использовать JDK 11 (рекомендуется для Selenium)
    environment {
        JAVA_HOME = '/Library/Java/JavaVirtualMachines/jdk-11.0.12.jdk/Contents/Home'
        PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
    }

    // Вариант 2: Или использовать JDK 8
    // environment {
    //     JAVA_HOME = '/Library/Java/JavaVirtualMachines/liberica-jdk-8.jdk/Contents/Home'
    //     PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
    // }

    // Вариант 3: Или просто убрать tools блок и позволить Jenkins использовать системную Java

    parameters {
        choice(
            name: 'browser',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Выберите браузер для тестов'
        )
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master',
                    url: 'https://github.com/ArtsemAverkov/selenium-gradle.git'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean compileJava'
            }
        }

        stage('Test') {
            steps {
                script {
                    echo "Running tests with browser: ${params.browser}"
                    sh "./gradlew test -Dbrowser=${params.browser}"
                }
            }
        }
    }

    post {
        always {
            junit 'build/test-results/test/**/*.xml'
            cleanWs()  // Очистить workspace после выполнения
        }
    }
}