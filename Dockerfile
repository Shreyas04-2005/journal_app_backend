# ---------- Stage 1: Build ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only pom first (for better caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy rest of the code
COPY src ./src

# Build the jar
RUN mvn clean package -DskipTests


# ---------- Stage 2: Run ----------
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Run the app
CMD ["java", "-jar", "app.jar"]