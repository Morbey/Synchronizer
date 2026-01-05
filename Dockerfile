FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline || true

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:25-jre

WORKDIR /app

# Create non-root user
RUN groupadd -r synchronizer && useradd -r -g synchronizer synchronizer

# Copy the built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership
RUN chown -R synchronizer:synchronizer /app

# Switch to non-root user
USER synchronizer

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
