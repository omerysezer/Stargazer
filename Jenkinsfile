pipeline {
	agent any
	
	stages{
	    stage("Append SSL certificate information"){
	        steps{
	            withCredentials([file(credentialsId: 'ssl-keystore-properties-expires-jan-19-2024', variable: 'sslKeyStoreProperties'),
	                            file(credentialsId: 'ssl-keystore-expires-jan-19-2024', variable: 'sslKeystoreFile')])
                {
                    sh("cp \$sslKeystoreFile /stargazer/src/main/resources")
                    sh("cat \$sslKeyStoreProperties >> /stargazer/src/main/resources/application.properties")
                }
	        }
	    }
		stage("Run Dockerfile"){
			steps{
				sh("docker build -t stargazer-web:1.0 .")
                sh("docker stop stargazer-web || true")
                sh("docker rm stargazer-web || true")
				sh("nohup docker run --name stargazer-web -p 80:8080 stargazer-web:1.0 &")
			}
		}
	}
	post{
	    always{
	        cleanWs()
	    }
	}
}
