# Build stage
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app
COPY . .

# Use the wrapper to ensure correct Gradle version
RUN chmod +x gradlew
RUN ./gradlew :backend:shadowJar --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Copy the built JAR
COPY --from=builder /app/backend/build/libs/*-all.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
