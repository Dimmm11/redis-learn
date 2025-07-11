FROM maven:3.9.9-amazoncorretto-21 AS build_phase
WORKDIR /app
ENV MAVEN_CONFIG=/root/.m2

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-alpine
WORKDIR /app
COPY --from=build_phase /app/target/*.jar packaged-app.jar
ENTRYPOINT ["java", "-jar", "packaged-app.jar"]