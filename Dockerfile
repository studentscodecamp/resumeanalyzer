FROM openjdk:17-jdk

WORKDIR /app

COPY target/resumeanalyzer-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]
