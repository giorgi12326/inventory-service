# Stage 1: build
FROM quay.io/quarkus/ubi-quarkus-maven:3.3 as build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -Pnative -DskipTests -Dquarkus.native.container-build=true

# Stage 2: run native binary
FROM quay.io/quarkus/ubi9-quarkus-micro-image:2.0
WORKDIR /app
COPY --from=build /app/target/*-runner /app/application
EXPOSE 8080
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
