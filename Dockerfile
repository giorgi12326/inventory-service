# Stage 1: build the application
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy maven files and download dependencies first (for caching)
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

# Copy the rest of the project
COPY src ./src

# Build the Quarkus app in production mode
RUN ./mvnw package -DskipTests

# Stage 2: run the application
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/quarkus-app/lib/ /app/lib/
COPY --from=build /app/target/quarkus-app/*.jar /app/
COPY --from=build /app/target/quarkus-app/app/ /app/app/
COPY --from=build /app/target/quarkus-app/quarkus/ /app/quarkus/

# Expose default Quarkus port
EXPOSE 8080

# Run Quarkus
CMD ["java", "-jar", "quarkus-run.jar"]
