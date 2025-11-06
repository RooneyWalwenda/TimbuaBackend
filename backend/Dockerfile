# Use official Java 17 image
FROM eclipse-temurin:17-jdk-alpine

# Install Maven
RUN apk add --no-cache maven bash

# Set working directory
WORKDIR /app

# Copy the backend folder content into /app
COPY backend/ .

# Build the project (skip tests)
RUN mvn clean package -DskipTests

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Run the JAR generated in the target folder
CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]

