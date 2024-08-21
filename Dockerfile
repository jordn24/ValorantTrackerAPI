FROM maven:3.8.5-openjdk-17 AS build

COPY . .

RUN mvn clean package -DskipTests

FROM openjdkL17.0.1-jdk-slim
COPY --from=build /target/tracker-api-0.0.1-SNAPSHOT.jar tracker-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "tracker-api.jar"]
