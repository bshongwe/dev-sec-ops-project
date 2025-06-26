# Multi-stage build for security and size optimization
FROM eclipse-temurin:11-jdk-alpine AS build
LABEL maintainer="DevSecOps Team"

# Set working directory
WORKDIR /workspace/app

# Copy Maven wrapper and configuration files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build application (skip tests in container build)
RUN ./mvnw clean package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Production stage
FROM eclipse-temurin:11-jre-alpine AS production
LABEL maintainer="DevSecOps Team"
LABEL version="1.0.0"
LABEL description="DevSecOps Demo Application"

# Create non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy application files from build stage
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build --chown=appuser:appgroup ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build --chown=appuser:appgroup ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build --chown=appuser:appgroup ${DEPENDENCY}/BOOT-INF/classes /app

# Switch to non-root user
USER appuser

# Expose application port
EXPOSE 8080

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Set JVM options for security and performance
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -cp app:app/lib/* com.example.demo.DemoApplication"]