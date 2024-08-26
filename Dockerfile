# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build

# Copy all project files into the container
COPY . .

# Build the project and package it as a JAR
RUN mvn clean package -DskipTests

# Stage 2: Set up the runtime environment
FROM openjdk:17.0.1-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Install necessary packages
RUN apt-get update && apt-get install -y

# Copy the built JAR from the build stage to the runtime stage
COPY --from=build /target/tracker-api-0.0.1-SNAPSHOT.jar tracker-api.jar

# Expose the port the application will run on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "tracker-api.jar"]
