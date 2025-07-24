# Use official Eclipse Temurin JDK 21 base image
FROM eclipse-temurin:21-jdk

# Set working directory inside container
WORKDIR /app

# Copy the Gradle build output (your jar file)
COPY build/libs/*.jar app.jar

# Expose port 8080 (optional, but good practice)
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
