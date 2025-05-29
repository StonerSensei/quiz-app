FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x ./mvnw

# Download dependencies (optimization)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose port (Render will use PORT environment variable)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/quizapp-0.0.1-SNAPSHOT.jar"]