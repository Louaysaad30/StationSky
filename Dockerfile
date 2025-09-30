# Simple Dockerfile for Spring Boot Application
FROM openjdk:17-alpine

# Copy JAR file
COPY target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]