# Use Java 23 JRE as the base image
FROM eclipse-temurin:23-jre-alpine

# Set working directory
WORKDIR /app

# Copy the compiled JAR file
COPY target/*.jar app.jar

# Copy the native DLL library
COPY dijkstra_jni.dll /app/libs/

# Set the Java library path to include the DLL location
ENV LD_LIBRARY_PATH=/app/libs

# Expose the application port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-Djava.library.path=/app/libs", "-jar", "app.jar"]
