pipeline {
	agent any
	
	stages{
		stage("Update code with new code"){
			steps{
			    sh("sudo rm -rf /usr/src/stargazer/*")
				sh("sudo cp -a /usr/src/stargazer")
			}
		}
	}
}
