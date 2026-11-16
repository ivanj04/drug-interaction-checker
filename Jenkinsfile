pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    environment {
        APP_NAME = 'drug-interaction-checker'
        IMAGE_NAME = "${APP_NAME}:${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile -q'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test -q'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests -q'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Docker Build') {
            when {
                branch 'main'
            }
            steps {
                sh "docker build -t ${IMAGE_NAME} ."
                sh "docker tag ${IMAGE_NAME} ${APP_NAME}:latest"
            }
        }

        stage('Deploy to Dev') {
            when {
                branch 'develop'
            }
            steps {
                echo "Deploying ${IMAGE_NAME} to development environment"
                sh "docker-compose up -d --build"
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo "Build ${BUILD_NUMBER} succeeded for ${APP_NAME}"
        }
        failure {
            echo "Build ${BUILD_NUMBER} failed for ${APP_NAME}"
        }
    }
}
