FROM maven:3.8.7-eclipse-temurin-17-alpine

RUN mkdir -p /home/app

COPY stargazer /home/app

RUN mvn install -f /home/app/Stargazer/stargazer/pom.xml

CMD ["java", "/home/app/Stargazer/stargazer/target/stargazer-*.jar"]