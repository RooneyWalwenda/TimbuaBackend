# Use official Java 17 image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the project (skip tests)
RUN ./mvnw clean package -DskipTests

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Run the JAR generated in the target folder
CMD ["java", "-jar", "target/Timbuabackend-0.0.1-SNAPSHOT.jar"]
