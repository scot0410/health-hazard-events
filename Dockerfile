# ==========================================
# Stage 1: Extract the Spring Boot 4 Jar Layers
# ==========================================
FROM eclipse-temurin:21-jre-alpine AS builder
WORKDIR /builder

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# Spring Boot 4.x native command structure for extracting layers safely
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher --destination extracted

# ==========================================
# Stage 2: Create the Hardened Production Image
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /application

# Security Hardening: Enforce non-root application process execution
RUN addgroup -S springgroup && adduser -S springuser -G springgroup
USER springuser:springgroup

# Copy the entire extracted tree directly from Stage 1 into the runtime stage
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
# If snapshot-dependencies exist, this safely grabs them, otherwise skips
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./

ENV JAVA_OPTS="-XX:+UseG1GC -XX:+ExitOnOutOfMemoryError -Xms128m -Xmx512m"

# Boot 4 class location for the JarLauncher
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} org.springframework.boot.loader.launch.JarLauncher"]
