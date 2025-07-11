pipeline {
    agent any

    triggers {
        githubPush()
    }

    environment {
        EC2_USER = 'ec2-user'
        EC2_HOST = '13.60.251.69'
        REMOTE_PATH = '/home/ec2-user/app.jar'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/Shan-kar07/admin-service.git'
            }
        }

        stage('Build') {
            steps {
                // Make mvnw executable before running it
                sh 'chmod +x mvnw'
                sh './mvnw clean package'
            }
        }

        stage('Deploy') {
            steps {
                // Copy the JAR to the EC2 instance
                sh 'scp -o StrictHostKeyChecking=no target/*.jar ${EC2_USER}@${EC2_HOST}:${REMOTE_PATH}'

                // Run the JAR on the EC2 instance in the background
                sh 'ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} "nohup java -jar ${REMOTE_PATH} > app.log 2>&1 &"'
            }
        }
    }
}
