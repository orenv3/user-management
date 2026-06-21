#
# Multi-stage build so `docker compose up --build` works
# even when `target/` doesn't exist on the host.
#

FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /workspace

# Cache dependencies first
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# .env is optional at build time; frontend public config is served at runtime via GET /api/config.
COPY .env* ./
RUN test -f .env || cp .env.example .env

# Build the app (frontend required by frontend-maven-plugin)
COPY src ./src
COPY frontend ./frontend
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /
COPY --from=build /workspace/target/*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
