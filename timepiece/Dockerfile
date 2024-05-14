# server base image - java 17
FROM eclipse-temurin:17.0.11_9-jre-alpine

# copy .jar file to docker
COPY ./build/libs/timepiece-0.0.1-SNAPSHOT.jar app.jar

# always do command
ENTRYPOINT ["java", "-Dspring.config.location=file:/resources/application.yml", "-jar", "app.jar"]

