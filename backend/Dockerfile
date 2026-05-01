# Java 17 runtime image
FROM eclipse-temurin:17-jdk

# App folder inside container
WORKDIR /app

# Copy built jar file
COPY target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Run application
ENTRYPOINT ["java","-jar","app.jar"]