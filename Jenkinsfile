pipeline {
	agent any
	
	stages{
	    stage("Append SSL certificate information"){
	        steps{
	            withCredentials([file(credentialsId: 'ssl-keystore-properties-expires-jan-19-2024', variable: 'sslKeyStoreProperties'),
	                            file(credentialsId: 'ssl-keystore-expires-jan-19-2024', variable: 'sslKeystoreFile')])
                {
                    sh('cp $sslKeystoreFile ./stargazer/src/main/resources/')
                    sh('cat $sslKeyStoreProperties >> ./stargazer/src/main/resources/application.properties')
                }
	        }
	    }
		stage("Run Dockerfile"){
			steps{
			    sh("docker compose up --force-recreate --build -d")
			}
		}
	}
	post{
	    always{
	        cleanWs()
	    }
	}
}
