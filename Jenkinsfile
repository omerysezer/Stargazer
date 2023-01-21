pipeline {
	agent any
	
	stages{
		stage("Update code with new code"){
			steps{
			    sh("""
			        virtualenv venv
			        . venv/bin/activate
			        pip install -r ./stargazer/requirements.txt
			    """)
			    sh("sudo rm -rf /stargazer/*")
				sh("sudo mv ./stargazer /stargazer")
				sh("sudo mv ./venv /stargazer")

			}
		}
	}
}
