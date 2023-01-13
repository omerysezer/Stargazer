pipeline {
	agent any
	
	stages{
		stage("Run Dockerfile"){
			steps{
				sh("docker build -t stargazer-web:1.0 .")
				sh("docker run -p 80:8080 stargazer-web:1.0")
			}
		}
	}
}
