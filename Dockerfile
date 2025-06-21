# Builder
FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Final image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/app.jar .
EXPOSE 8080
EXPOSE 5005
ENTRYPOINT ["java", "-jar", "app.jar"]