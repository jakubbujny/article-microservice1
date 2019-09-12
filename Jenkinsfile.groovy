pipeline {
    agent {
        kubernetes {
            //cloud 'kubernetes'
            label 'mypod'
            yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: docker
    image: docker:1.11
    command: ['cat']
    tty: true
    volumeMounts:
    - name: dockersock
      mountPath: /var/run/docker.sock
  volumes:
  - name: dockersock
    hostPath:
      path: /var/run/docker.sock
"""
        }
    }
    stages {
        stage('Build Docker image') {
            steps {
                checkout scm
                container('docker') {
                    script {
                        def image = docker.build("digitalrasta/article-microservice1:${BUILD_NUMBER}")
                        docker.withRegistry( '', "dockerhub") {
                            image.push()
                        }
                    }
                }
            }
        }
    }
}
