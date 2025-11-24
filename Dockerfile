# Multi-stage Dockerfile for both main-api and event-logger
# Use --target flag to build specific service:
# docker build --target main-api -t main-api .
# docker build --target event-logger -t event-logger .

# Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy parent pom and checkstyle config
COPY pom.xml .
COPY checkstyle.xml .
COPY .mvn .mvn
COPY mvnw .

# Copy all module pom files first for better layer caching
COPY common/pom.xml common/
COPY common-mysqldb/pom.xml common-mysqldb/
COPY common-event-logger/pom.xml common-event-logger/
COPY common-embedded-data/pom.xml common-embedded-data/
COPY main-api/pom.xml main-api/
COPY event-logger/pom.xml event-logger/

# Download dependencies (cached if pom files unchanged)
RUN mvn dependency:go-offline -B

# Copy source code
COPY common/src common/src
COPY common-mysqldb/src common-mysqldb/src
COPY common-event-logger/src common-event-logger/src
COPY common-embedded-data/src common-embedded-data/src
COPY main-api/src main-api/src
COPY event-logger/src event-logger/src

# Build the application
RUN mvn clean package -DskipTests -B

# Runtime stage for main-api
FROM eclipse-temurin:21-jre-alpine AS main-api

WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy jar from builder
COPY --from=builder /app/main-api/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Runtime stage for event-logger
FROM eclipse-temurin:21-jre-alpine AS event-logger

WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy jar from builder
COPY --from=builder /app/event-logger/target/*.jar app.jar

# Expose port
EXPOSE 8091

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8091/actuator/health || exit 1

# JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]