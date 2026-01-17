# Stage 1: Build the application
FROM gradle:8.5.0-jdk17 AS builder
WORKDIR /app
COPY . .
# Skip tests during build to speed up deployment
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port (Koyeb Free tier uses 8000)
EXPOSE 8000

# Run with prod profile
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
