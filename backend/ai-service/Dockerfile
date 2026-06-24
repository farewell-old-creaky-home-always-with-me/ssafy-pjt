FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY . .
RUN mkdir -p ai-service/src/main/resources/secret && \
    cp -r secret/. ai-service/src/main/resources/secret/ 2>/dev/null || true
RUN ./gradlew :ai-service:build -x test --no-daemon -q

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/ai-service/build/libs/ai-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
