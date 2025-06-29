# Use Maven to build the application
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy the project files
COPY . .

# Build the JAR
RUN mvn clean package -DskipTests

# ---- Production image ----
FROM openjdk:17-jdk

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/resumeanalyzer-0.0.1-SNAPSHOT.jar app.jar

# Run the application
CMD ["java", "-jar", "app.jar"]
