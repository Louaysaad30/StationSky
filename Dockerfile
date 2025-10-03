# Simple Dockerfile for Spring Boot Application
FROM openjdk:17-jdk-alpine

# Copy JAR file (flexible pattern)
COPY target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]