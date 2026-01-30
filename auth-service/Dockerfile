FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .

RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -DskipTests package


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /workspace/target/quarkus-app/ /app/

EXPOSE 8080
ENV QUARKUS_HTTP_HOST=0.0.0.0

ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]
