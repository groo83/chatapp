pipeline {
    agent any

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/groo83/chatapp.git'
            }
        }
        stage('Grant Permission') {
            steps {
                sh 'chmod +x gradlew'  // 실행 권한 추가
            }
        }
        stage('Build') {
            steps {
                sh './gradlew clean build' // test 포함
                echo 'Building from GitHub!'
            }
        }
    }
}