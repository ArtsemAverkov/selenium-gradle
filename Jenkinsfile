pipeline {
    agent any

    tools {
        jdk 'JDK_17'
        gradle 'Gradle_8.5'
    }

    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox'],
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
        WORKSPACE = pwd()
        ALLURE_RESULTS = "${WORKSPACE}/build/allure-results"
        ALLURE_REPORT = "${WORKSPACE}/build/reports/allure-report"
        GRADLE_PROPS = "-Dbrowser=${params.BROWSER} -Dheadless=${params.HEADLESS}"
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
                script {
                    echo "📦 Repository: ${env.GIT_URL}"
                    echo "🌿 Branch: ${env.GIT_BRANCH}"
                }
            }
        }

        stage('Show Project Structure') {
            steps {
                sh '''
                    echo "=== PROJECT STRUCTURE ==="
                    pwd
                    ls -la
                    echo ""
                    echo "=== SOURCE FILES ==="
                    find . -name "*.java" | head -15 || echo "No Java files"
                    echo ""
                    echo "=== GRADLE FILES ==="
                    ls -la *.gradle* gradlew* 2>/dev/null || echo "No Gradle files"
                    echo ""
                    echo "=== CONFIG FILE ==="
                    cat src/test/resources/config.yml 2>/dev/null || echo "No config.yml"
                '''
            }
        }

        stage('Setup Environment') {
            steps {
                script {
                    // Даем права на gradlew
                    sh 'chmod +x gradlew 2>/dev/null || true'

                    // Проверяем Java и Gradle
                    sh '''
                        echo "=== JAVA VERSION ==="
                        java -version
                        echo ""
                        echo "=== GRADLE VERSION ==="
                        ./gradlew --version || echo "Gradle not found"
                    '''
                }
            }
        }

        stage('Build Project') {
            steps {
                sh './gradlew clean compileJava compileTestJava --no-daemon'
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    echo "🚀 Running tests with:"
                    echo "  Browser: ${params.BROWSER}"
                    echo "  Headless: ${params.HEADLESS}"

                    // Запускаем тесты
                    sh """
                        ./gradlew test ${GRADLE_PROPS} \
                            --no-daemon \
                            --stacktrace \
                            --info \
                            --console=plain
                    """
                }
            }
            post {
                always {
                    // Сохраняем TestNG отчеты
                    junit 'build/test-results/test/**/*.xml'
                }
            }
        }

        stage('Generate Allure Report') {
            steps {
                script {
                    // Генерируем Allure отчет
                    sh './gradlew allureReport'

                    // Проверяем что отчеты создались
                    sh '''
                        echo "=== CHECKING ALLURE RESULTS ==="
                        if [ -d "build/allure-results" ]; then
                            echo "Allure results found:"
                            ls -la build/allure-results/ | head -5
                        else
                            echo "WARNING: No allure-results directory!"
                        fi

                        if [ -d "build/reports/allure-report" ]; then
                            echo "Allure report generated"
                        else
                            echo "WARNING: No allure-report directory!"
                        fi
                    '''
                }
            }
            post {
                always {
                    // Публикация Allure отчета (БЕЗ PropertiesBuilder)
                    allure([
                        results: [[path: ALLURE_RESULTS]],
                        report: ALLURE_REPORT
                    ])
                }
            }
        }

        stage('Show Test Results') {
            steps {
                sh '''
                    echo "=== TEST RESULTS SUMMARY ==="
                    if [ -f "build/test-results/test/TEST-*.xml" ]; then
                        echo "Test results XML found"
                        # Показываем краткую статистику
                        grep -h "testsuite" build/test-results/test/TEST-*.xml | head -5
                    else
                        echo "No test results found"
                        # Ищем другие возможные пути
                        find . -name "TEST-*.xml" -type f | head -5
                    fi

                    echo ""
                    echo "=== BUILD DIRECTORY CONTENTS ==="
                    ls -la build/ 2>/dev/null || echo "No build directory"
                '''
            }
        }
    }

    post {
        always {
            // Очистка
            cleanWs()

            // Сводка
            script {
                def status = currentBuild.currentResult
                echo "📋 Build #${env.BUILD_NUMBER} - ${status}"
                echo "Duration: ${currentBuild.durationString}"

                // Сохраняем важные артефакты
                archiveArtifacts artifacts: 'build/reports/**/*', fingerprint: true
                archiveArtifacts artifacts: 'build/test-results/**/*', fingerprint: true
                archiveArtifacts artifacts: '**/*.log', fingerprint: true
            }
        }

        success {
            echo '✅ Build successful!'
            script {
                // Показываем где найти отчеты
                echo "Allure Report: ${env.BUILD_URL}allure/"
                echo "Test Results: ${env.BUILD_URL}testReport/"
            }
        }

        failure {
            echo '❌ Build failed!'
            script {
                // Сохраняем дополнительную диагностику
                sh '''
                    echo "=== DIAGNOSTICS ON FAILURE ==="
                    echo "Current directory:"
                    pwd
                    echo ""
                    echo "Listing all files:"
                    find . -type f -name "*.java" -o -name "*.gradle*" -o -name "*.yml" -o -name "*.xml" | head -20
                    echo ""
                    echo "Gradle build directory:"
                    ls -la build/ 2>/dev/null || echo "No build dir"
                '''
            }
        }
    }
}