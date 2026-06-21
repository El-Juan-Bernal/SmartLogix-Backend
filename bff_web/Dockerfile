# Etapa 1: Construcción y autocompilación
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

# Copiamos todo el código fuente actual
COPY . .

# Compilamos el .jar fresco dentro del contenedor
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Etapa 2: Ejecución liviana
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Tomamos el .jar recién horneado de la etapa 1
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

