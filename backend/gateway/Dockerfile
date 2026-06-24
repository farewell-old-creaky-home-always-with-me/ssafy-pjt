FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY . .
RUN mkdir -p gateway/src/main/resources/secret && \
    cp -r secret/. gateway/src/main/resources/secret/ 2>/dev/null || true
RUN ./gradlew :gateway:build -x test --no-daemon -q

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/gateway/build/libs/gateway-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
