# Stage 1: build
FROM quay.io/quarkus/ubi-quarkus-maven:3.3 as build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -Pnative -DskipTests

# Stage 2: run
FROM quay.io/quarkus/ubi-quarkus-native-image:3.3
WORKDIR /app
COPY --from=build /app/target/*-runner /app/app
EXPOSE 8080
CMD ["./app"]