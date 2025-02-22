# Use official OpenJDK 17 as base image (matching your pom.xml)
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the Maven build output (JAR file) to the container
COPY target/wallclubs-0.0.1-SNAPSHOT.jar app.jar

# Expose the port (Render will override with its PORT env var)
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]