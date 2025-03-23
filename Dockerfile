FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the Maven wrapper and POM
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make the Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies (this layer will be cached)
RUN ./mvnw dependency:go-offline -B

# Copy the source code
COPY src src

# Build the application (skip tests during build)
RUN ./mvnw package -DskipTests && find target -name "*.jar" -not -name "*source.jar" -name "*.jar" -not -name "*javadoc.jar" -exec mv {} target/app.jar \;

# Create a volume to persist H2 database files
VOLUME /data/db

# Expose the port that Spring Boot app will run on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/target/app.jar"]
