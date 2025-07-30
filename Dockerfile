# Stage 1: Build the JAR with Gradle
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Gradle wrapper and config files first
COPY gradlew .
COPY gradle gradle

# Make gradlew executable immediately after copying
RUN chmod +x ./gradlew

# Verify gradlew is executable and test it
RUN ls -la ./gradlew
RUN ./gradlew --version

# Copy build configuration files
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src src

# Build the jar (skip tests for speed)
RUN ./gradlew clean build -x test

# Stage 2: Run the app with JRE
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create a non-root user for security
RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup

# Copy the built jar from previous stage
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership to non-root user
RUN chown appuser:appgroup app.jar

# Switch to non-root user
USER appuser:appgroup

EXPOSE 8080

# Add health check (optional)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]