# Etapa 1: Construcción del archivo ejecutable
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

# Copiamos el código fuente y las herramientas de Maven del proyecto
COPY . .

# Otorgamos permisos de ejecución al script de Maven y compilamos omitiendo las pruebas
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagen de ejecución liviana
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Copiamos solo el JAR compilado desde la etapa anterior
COPY --from=build /app/target/ms_usuario-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto correspondiente a Usuarios
EXPOSE 8085

# Comando para iniciar el microservicio
ENTRYPOINT ["java", "-jar", "app.jar"]

