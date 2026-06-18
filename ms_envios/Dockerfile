# ==========================================
# Etapa 1: Construcción (Build)
# ==========================================
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# Copiamos el pom.xml y descargamos las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiamos el código fuente y compilamos
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Etapa 2: Ejecución (Run)
# ==========================================
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app

# Copiamos el .jar generado en la etapa anterior
COPY --from=build /app/target/ms_envios-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto de envíos
EXPOSE 8083

# Comando para arrancar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

