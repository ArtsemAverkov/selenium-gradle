pipeline {
    agent {
        docker {
            image 'java11-docker'
            args '-u root -v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    environment {
        DOCKER_HOST = 'unix:///var/run/docker.sock'
    }

    stages {  // <-- ВСЕ stage должны быть здесь

        stage('Start Selenium Grid') {
            steps {
                sh '''
                    docker rm -f selenium-hub chrome || true
                    docker network rm test-network || true

                    docker network create test-network

                    docker run -d --name selenium-hub --net test-network \
                        -p 4444:4444 selenium/hub:4.8.0

                    docker run -d --name chrome --net test-network \
                        --shm-size="2g" \
                        -e SE_EVENT_BUS_HOST=selenium-hub \
                        selenium/node-chrome:4.8.0
                '''
            }
        }

        stage('Run Tests') {
            steps {
                sh './gradlew test -Dselenium.remote=true -Dselenium.host=selenium-hub -Dselenium.port=4444'
            }
        }

        stage('Allure Report') {
            steps {
                sh './gradlew allureReport'
                allure includeProperties: false, jdk: '', results: [[path: 'build/allure-results']]
            }
        }

        stage('Cleanup') {
            steps {
                sh '''
                    docker stop chrome selenium-hub || true
                    docker rm chrome selenium-hub || true
                '''
            }
        }
    }

    post {
        always {
            sh '''
                docker rm -f chrome selenium-hub || true
                docker network rm test-network || true
            '''
        }
    }
}
