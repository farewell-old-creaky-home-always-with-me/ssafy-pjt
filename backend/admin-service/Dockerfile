FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY . .
RUN test -d secret || { echo "ERROR: secret/ directory not found" >&2; exit 1; } && \
    mkdir -p admin-service/src/main/resources/secret && \
    cp -r secret/. admin-service/src/main/resources/secret/
RUN ./gradlew :admin-service:build -x test --no-daemon -q

FROM eclipse-temurin:17-jre
RUN apt-get update -q && apt-get install -y --no-install-recommends wget && rm -rf /var/lib/apt/lists/* && \
    addgroup --system appgroup && adduser --system --ingroup appgroup appuser
WORKDIR /app
COPY --from=builder /app/admin-service/build/libs/admin-service-0.0.1-SNAPSHOT.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]
