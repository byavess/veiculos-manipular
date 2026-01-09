FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM maven:3.8.4-openjdk-17-slim AS runtime

WORKDIR /app

COPY --from=build /app/target/veiculo-backend-1.0-SNAPSHOT.jar app.jar

# Cria o diretório para armazenar as imagens
RUN mkdir -p /app/images/veiculos && chmod -R 755 /app/images

# Define o volume para persistência das imagens
VOLUME /app/images

EXPOSE 8080

# Define o profile prod e inicia a aplicação
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
