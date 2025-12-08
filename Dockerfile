# Stage 1: build
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy maven wrapper and pom
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Make mvnw executable (fix Windows -> Linux permission issue)
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the source
COPY src ./src

# Build Quarkus app
RUN ./mvnw package -DskipTests

# Stage 2: run
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/quarkus-app/ /app/
EXPOSE 8080
CMD ["java", "-jar", "quarkus-run.jar"]
