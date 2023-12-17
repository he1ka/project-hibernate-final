FROM maven:3.9.4-eclipse-temurin-17-alpine AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean compile assembly:single -Dmaven.test.skip=true

CMD ["java", "-jar", "/home/app/target/hibernate-final-1.0-jar-with-dependencies.jar"]