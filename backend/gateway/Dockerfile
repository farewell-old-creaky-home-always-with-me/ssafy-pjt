FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY . .
RUN test -d secret || { echo "ERROR: secret/ directory not found" >&2; exit 1; } && \
    mkdir -p gateway/src/main/resources/secret && \
    cp -r secret/. gateway/src/main/resources/secret/
RUN ./gradlew :gateway:build -x test --no-daemon -q

FROM eclipse-temurin:17-jre
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
WORKDIR /app
COPY --from=builder /app/gateway/build/libs/gateway-0.0.1-SNAPSHOT.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]
