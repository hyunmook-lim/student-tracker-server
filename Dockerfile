# Stage 1: Build the application
FROM gradle:8.5.0-jdk17 AS builder
WORKDIR /app
COPY . .
# Skip tests during build to speed up deployment
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test

# Stage 2: Run the application
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port (Koyeb expects 8080 by default)
EXPOSE 8080

# Run with prod profile
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
