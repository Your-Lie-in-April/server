# server base image - java 17
FROM openjdk:17.0.2-slim

# copy .jar file to docker
COPY ./build/libs/timepiece-0.0.1-SNAPSHOT.jar app.jar

# always do command
ENTRYPOINT ["java", "-Dspring.config.location=classpath:/application.yml,/resources/application.yml", "-jar", "app.jar"]

