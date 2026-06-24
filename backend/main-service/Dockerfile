FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY . .
RUN test -d secret || { echo "ERROR: secret/ directory not found" >&2; exit 1; } && \
    mkdir -p main-service/src/main/resources/secret && \
    cp -r secret/. main-service/src/main/resources/secret/
RUN ./gradlew :main-service:build -x test --no-daemon -q

FROM eclipse-temurin:17-jre
RUN apt-get update -q && apt-get install -y --no-install-recommends wget && rm -rf /var/lib/apt/lists/* && \
    addgroup --system appgroup && adduser --system --ingroup appgroup appuser
WORKDIR /app
COPY --from=builder /app/main-service/build/libs/main-service-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir -p logs && chown -R appuser:appgroup app.jar logs
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]
