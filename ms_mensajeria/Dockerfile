# Usamos una imagen oficial y ligera de Java
# (Asegúrate de que la versión coincida con el Java que usas en tu PC)
FROM eclipse-temurin:25-jdk-alpine

# Creamos una carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el archivo .jar compilado desde tu carpeta target hacia el contenedor
COPY target/*.jar app.jar

# Le decimos al contenedor el comando exacto para iniciar el microservicio
ENTRYPOINT ["java", "-jar", "app.jar"]
