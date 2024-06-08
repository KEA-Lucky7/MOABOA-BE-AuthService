pipeline {
    agent any
    environment {
        REGISTRY_URL = 'gcu-lucky7-dev.kr-central-2.kcr.dev'
        REGISTRY_CREDENTIALS_ID = 'kakao-cloud-credentials'
        IMAGE_NAME = 'lucky7-cr/auth-service'
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', credentialsId: 'github-signin', url: 'https://github.com/KEA-Lucky7/MOABOA-BE-AuthService.git'
            }
        }
        stage('Build') {
            steps {
                script {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean build'
                }
            }
        }
        stage('Build Docker Image and Push') {
            steps {
                script {
                    docker.withRegistry("http://${env.REGISTRY_URL}", "${env.REGISTRY_CREDENTIALS_ID}") {
                        def app = docker.build("${env.REGISTRY_URL}/${env.IMAGE_NAME}")
                        app.push("${env.BUILD_ID}")
                    }
                }
            }
        }
        stage('Update Helm Values YAML') {
            steps {
                script {
                    git branch: 'main', credentialsId: 'github-signin', url: 'https://github.com/KEA-Lucky7/MOABOA-GitOps.git'
                    sh """
                    sed -i 's|tag: .*|tag: ${env.BUILD_ID}|' charts/auth-service/values.yaml
                    """
                    withCredentials([usernamePassword(credentialsId: 'github-signin', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                        sh """
                        git config user.email "dsk08208@gmail.com"
                        git config user.name "wcorn"
                        git add charts/auth-service/values.yaml
                        git commit -m "[UPDATE] auth service image tag ${env.BUILD_ID}"
                        git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/KEA-Lucky7/MOABOA-GitOps.git main
                        """
                    }
                }
            }
        }
        stage('Cleanup') {
            steps {
                sh 'rm -rf *'
}
        }
    }
    post {
        success {
            withCredentials([string(credentialsId: 'discord-webhook', variable: 'DISCORD')]) {
                        discordSend description: """
                        제목 : ${currentBuild.displayName}
                        결과 : ${currentBuild.result}
                        실행 시간 : ${currentBuild.duration / 1000}s
                        """,
                        link: env.BUILD_URL, result: currentBuild.currentResult,
                        title: "${env.JOB_NAME} : ${currentBuild.displayName} 성공",
                        webhookURL: "$DISCORD"
            }
        }
        failure {
            withCredentials([string(credentialsId: 'Discord-Webhook', variable: 'DISCORD')]) {
                        discordSend description: """
                        제목 : ${currentBuild.displayName}
                        결과 : ${currentBuild.result}
                        실행 시간 : ${currentBuild.duration / 1000}s
                        """,
                        link: env.BUILD_URL, result: currentBuild.currentResult,
                        title: "${env.JOB_NAME} : ${currentBuild.displayName} 실패",
                        webhookURL: "$DISCORD"
            }
        }
    }
}
