pipeline {
	agent any
	
	stages{
		stage("Run Dockerfile"){
			steps{
			    sh("docker stop stargazer-web || true")
			    sh("docker rm stargazer-web || true")
				sh("docker build -t stargazer-web:1.0 .")
				sh("nohup docker run --name stargazer-web -p 80:8080 stargazer-web:1.0 &")
			}
		}
	}
}
