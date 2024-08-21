FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
WORKDIR /app

# Copy the built JAR from the build stage to the runtime stage
COPY --from=build /target/tracker-api-0.0.1-SNAPSHOT.jar tracker-api.jar
# Copy chromedriver from the project to the container's /usr/local/bin directory
COPY src/main/resources/chromedriver /usr/local/bin/chromedriver
# Make chromedriver executable
RUN chmod +x /usr/local/bin/chromedriver

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "tracker-api.jar"]
