FROM maven:3.9.9-amazoncorretto-21 AS build_phase
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-alpine
WORKDIR /app
COPY --from=build_phase /app/target/*.jar packaged-app.jar
ENTRYPOINT ["java", "-jar", "packaged-app.jar"]