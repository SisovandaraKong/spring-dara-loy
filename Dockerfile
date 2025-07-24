# Stage 1: Build the JAR with Gradle
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Gradle wrapper and config files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src src

# Build the jar (skip tests for speed)
RUN ./gradlew clean build -x test

# Stage 2: Run the app with JRE
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar from previous stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
