# Build stage
FROM maven:3-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the entire project
COPY . .

# Run Maven build (skip tests to speed up the build process)
RUN mvn clean package -DskipTests


# Run stage
FROM eclipse-temurin:17-alpine

# Copy the WAR file from the build stage
COPY --from=build /app/target/*jar demo.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "demo.jar"]
