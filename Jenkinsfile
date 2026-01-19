pipeline {
    agent any

    tools {
        // Настроить в Jenkins: Manage Jenkins → Tools
        jdk 'JDK_17'
        gradle 'Gradle_8.5'
    }

    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'safari'],
            defaultValue: 'chrome',
            description: 'Browser for UI tests'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run browser in headless mode'
        )
    }

    environment {
        // Пути для отчетов
        WORKSPACE = pwd()
        ALLURE_RESULTS = "${WORKSPACE}/build/allure-results"
        ALLURE_REPORT = "${WORKSPACE}/build/reports/allure-report"

        // Параметры Gradle
        GRADLE_PROPS = "-Dbrowser=${params.BROWSER} -Dheadless=${params.HEADLESS}"

        // Test site (из вашего config.yml)
        TEST_SITE = "https://the-internet.herokuapp.com/login"
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
                script {
                    echo "📦 Repository: ${env.GIT_URL}"
                    echo "🌿 Branch: ${env.GIT_BRANCH}"
                    echo "🔑 Commit: ${env.GIT_COMMIT?.take(7) ?: 'N/A'}"

                    // Показываем структуру проекта
                    sh '''
                        echo "📁 Project structure:"
                        find . -name "*.java" -o -name "*.yml" -o -name "*.gradle*" | head -15
                        echo ""
                        echo "📋 Config file:"
                        cat src/test/resources/config.yml || echo "No config.yml found"
                    '''
                }
            }
        }

        stage('Setup Environment') {
            steps {
                script {
                    // Даем права на gradlew
                    sh 'chmod +x gradlew 2>/dev/null || true'

                    // Устанавливаем Chrome если нужно
                    if (params.BROWSER == 'chrome') {
                        sh '''
                            echo "🔧 Setting up Chrome..."
                            # Для Linux (Jenkins агенты обычно на Linux)
                            if command -v apt-get &> /dev/null; then
                                echo "Installing Chrome on Debian/Ubuntu"
                                wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | sudo apt-key add -
                                echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" | sudo tee /etc/apt/sources.list.d/google-chrome.list
                                sudo apt-get update
                                sudo apt-get install -y google-chrome-stable
                                echo "Chrome version:"
                                google-chrome --version || echo "Chrome not installed"
                            elif command -v yum &> /dev/null; then
                                echo "Installing Chrome on RHEL/CentOS"
                                sudo yum install -y https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm
                            else
                                echo "⚠️ Cannot install Chrome automatically on this system"
                            fi
                        '''
                    }

                    // Проверяем установленные инструменты
                    sh '''
                        echo "🔧 Checking tools:"
                        echo "Java:"
                        java -version
                        echo ""
                        echo "Gradle:"
                        ./gradlew --version || gradle --version || echo "Gradle not found"
                        echo ""
                        echo "Git:"
                        git --version
                    '''
                }
            }
        }

        stage('Build Project') {
            steps {
                sh '''
                    echo "🏗️ Building project..."
                    ./gradlew clean compileJava compileTestJava
                '''
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    echo "🚀 Running tests with:"
                    echo "  Browser: ${params.BROWSER}"
                    echo "  Headless: ${params.HEADLESS}"
                    echo "  Site: ${TEST_SITE}"

                    // Запускаем тесты с таймаутом
                    timeout(time: 15, unit: 'MINUTES') {
                        sh """
                            ./gradlew test ${GRADLE_PROPS} \
                                --no-daemon \
                                --stacktrace \
                                --info \
                                --console=plain
                        """
                    }
                }
            }
            post {
                always {
                    // Сохраняем TestNG отчеты
                    junit 'build/test-results/test/**/*.xml'

                    // Проверяем Allure результаты
                    script {
                        if (fileExists("build/allure-results")) {
                            echo "📊 Allure results found in build/allure-results"
                            sh 'ls -la build/allure-results/ | head -10'
                        } else {
                            echo "⚠️ No Allure results found"
                        }
                    }
                }
            }
        }

        stage('Generate Reports') {
            steps {
                script {
                    echo "📊 Generating reports..."

                    // Генерируем Allure отчет
                    sh './gradlew allureReport'

                    // Копируем скриншоты если есть
                    sh '''
                        mkdir -p ${ALLURE_RESULTS} 2>/dev/null || true
                        find . -name "*.png" -type f | head -5 | xargs -I {} cp {} ${ALLURE_RESULTS}/ 2>/dev/null || true
                    '''
                }
            }
            post {
                always {
                    // Публикация Allure отчета в Jenkins
                    allure([
                        results: [[path: ALLURE_RESULTS]],
                        report: ALLURE_REPORT,
                        properties: [
                            new io.qameta.allure.PropertiesBuilder()
                                .property("Browser", params.BROWSER)
                                .property("Headless", params.HEADLESS.toString())
                                .property("Test Site", TEST_SITE)
                                .property("Jenkins Build", env.BUILD_NUMBER)
                                .build()
                        ]
                    ])

                    // Публикация TestNG HTML отчета
                    publishHTML([
                        target: [
                            reportName: "TestNG Report",
                            reportDir: "build/reports/tests/test",
                            reportFiles: "index.html",
                            keepAll: true,
                            alwaysLinkToLastBuild: true
                        ]
                    ])
                }
            }
        }

        stage('Archive Results') {
            steps {
                script {
                    echo "📁 Archiving results..."

                    // Архивируем важные файлы
                    archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
                    archiveArtifacts artifacts: 'build/reports/**/*', fingerprint: true
                    archiveArtifacts artifacts: 'build/test-results/**/*', fingerprint: true
                    archiveArtifacts artifacts: 'src/test/resources/config.yml', fingerprint: true

                    // Архивируем логи
                    sh '''
                        find . -name "*.log" -type f | head -3 | xargs -I {} cp {} . 2>/dev/null || true
                    '''
                    archiveArtifacts artifacts: '*.log', fingerprint: true
                }
            }
        }
    }

    post {
        always {
            // Очистка workspace
            cleanWs()

            // Сводка сборки
            script {
                def buildStatus = currentBuild.currentResult
                def duration = currentBuild.durationString
                def allureUrl = "${env.BUILD_URL}allure/"

                echo """
                📋 Build Summary:
                  Status: ${buildStatus}
                  Duration: ${duration}
                  Build: #${env.BUILD_NUMBER}
                  Allure Report: ${allureUrl}
                  Parameters:
                    - Browser: ${params.BROWSER}
                    - Headless: ${params.HEADLESS}
                """

                // Email уведомление (настроить под себя)
                emailext(
                    to: 'your.email@example.com', // Замените на свой email
                    subject: "Build ${buildStatus}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: """
                        <h2>Test Automation Results</h2>
                        <p><strong>Project:</strong> ${env.JOB_NAME}</p>
                        <p><strong>Build:</strong> #${env.BUILD_NUMBER}</p>
                        <p><strong>Status:</strong> <span style="color: ${buildStatus == 'SUCCESS' ? 'green' : 'red'}">${buildStatus}</span></p>
                        <p><strong>Duration:</strong> ${duration}</p>

                        <h3>Test Parameters:</h3>
                        <ul>
                            <li>Browser: ${params.BROWSER}</li>
                            <li>Headless: ${params.HEADLESS}</li>
                            <li>Test Site: ${TEST_SITE}</li>
                        </ul>

                        <h3>Links:</h3>
                        <ul>
                            <li><a href="${env.BUILD_URL}">Build Details</a></li>
                            <li><a href="${allureUrl}">Allure Report</a></li>
                        </ul>
                    """,
                    mimeType: 'text/html'
                )
            }
        }

        success {
            echo '✅ All tests passed successfully!'
            script {
                // Можно добавить уведомление в Slack/Telegram
                slackSend(
                    color: 'good',
                    message: "✅ ${env.JOB_NAME} #${env.BUILD_NUMBER} passed\nReport: ${env.BUILD_URL}allure/"
                )
            }
        }

        failure {
            echo '❌ Build or tests failed!'
            script {
                // Уведомление о неудаче
                slackSend(
                    color: 'danger',
                    message: "❌ ${env.JOB_NAME} #${env.BUILD_NUMBER} failed\nLogs: ${env.BUILD_URL}console"
                )

                // Диагностика при падении
                sh '''
                    echo "=== Diagnostic Information ==="
                    echo "Working directory:"
                    pwd
                    ls -la
                    echo ""
                    echo "Gradle build directory:"
                    ls -la build/ 2>/dev/null || echo "No build directory"
                    echo ""
                    echo "Test results:"
                    ls -la build/test-results/ 2>/dev/null || echo "No test results"
                    echo ""
                    echo "Allure results:"
                    ls -la build/allure-results/ 2>/dev/null || echo "No allure results"
                '''
            }
        }
    }
}