FROM maven:3.8.7-eclipse-temurin-17-alpine

RUN mkdir -p /home/app

COPY . /home/app

RUN mvn install -f /home/app/stargazer/pom.xml

CMD ["java", "-jar", "/home/app/stargazer/target/stargazer-0.0.1-SNAPSHOT.jar"]