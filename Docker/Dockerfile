# Dockerfile - только для запуска тестов
FROM eclipse-temurin:11-jdk

WORKDIR /app

COPY . .

RUN chmod +x gradlew

CMD ["./gradlew", "clean", "test"]