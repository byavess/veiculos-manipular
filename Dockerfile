FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM maven:3.8.4-openjdk-17-slim AS runtime

WORKDIR /app

COPY --from=build /app/target/veiculo-backend-1.0-SNAPSHOT.jar app.jar


EXPOSE 8080

# Define o profile prod e inicia a aplicação
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
