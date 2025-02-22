# Use official OpenJDK 17 as base image (includes Maven)
FROM maven:3.8.6-openjdk-17-slim AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the JAR
RUN mvn clean package -DskipTests

# Use a slim runtime image for the final app
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/wallclubs-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]