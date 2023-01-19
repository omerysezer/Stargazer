FROM maven:3.8.7-eclipse-temurin-17-alpine

RUN mkdir -p /home/app
RUN mkdir -p /home/src

COPY . /home/src

RUN mvn install -f /home/src/stargazer/pom.xml
RUN cp /home/src/stargazer/target/stargazer-0.0.1-SNAPSHOT.jar /home/app
RUN rm -rf /home/src

CMD ["java", "-jar", "/home/app/stargazer-0.0.1-SNAPSHOT.jar"]