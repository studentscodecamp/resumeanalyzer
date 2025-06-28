# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy Maven build file and download dependencies
COPY pom.xml ./
RUN apt-get update && apt-get install -y maven && mvn dependency:go-offline -B

# Copy the entire project and build it
COPY . ./
RUN mvn package -DskipTests

# Run the JAR file (replace with your actual JAR name if different)
CMD ["java", "-jar", "target/resumeanalyzer-0.0.1-SNAPSHOT.jar"]
