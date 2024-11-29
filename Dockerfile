# Use an OpenJDK base image
FROM openjdk:17-jdk-slim

# Set a working directory inside the container
WORKDIR /app

# Copy the JAR file to the container
COPY target/seven-0.0.1-SNAPSHOT.jar app.jar

# Expose the application's port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
