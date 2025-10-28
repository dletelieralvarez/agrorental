# Usar la imagen oficial de Java 18 como base.
# Fuente: https://hub.docker.com/_/openjdk
# Esta imagen contiene lo necesario para ejecutar aplicaciones Java en modo JDK.
FROM openjdk:18-jdk-slim

# Crear un directorio llamado /app dentro del contenedor.
# Este será el directorio de trabajo donde se ejecutará la aplicación.
WORKDIR /app

# Copiar el archivo JAR (compilado con Maven, usualmente ubicado en /target) al contenedor.
# Este archivo es el microservicio Java que vamos a ejecutar.
# Se renombra a app.jar para facilitar su uso.
COPY target/web_seguro-0.0.1-SNAPSHOT.jar app.jar

# Copiar el folder Wallet que se usa para la conexión segura a Oracle Database Cloud.
# Este folder contiene archivos de configuración (.zip o carpeta descomprimida) necesarios para que Oracle valide la conexión.
# Fuente: Oracle Cloud Wallet: https://docs.oracle.com/en/cloud/paas/autonomous-database/adbsa/connecting-oracle-wallet.html
COPY Wallet_VPI3OXVG2QH7QK56 /app/wallet

# Exponer el puerto 8080 dentro del contenedor.
# Este es el puerto que utiliza Spring Boot por defecto.
# Esta instrucción es opcional, sirve como metadato. Para exponerlo realmente se debe mapear con -p al ejecutar docker run.
EXPOSE 8080

# Comando por defecto que se ejecutará cuando se inicie el contenedor.
# Inicia la aplicación ejecutando el archivo app.jar con Java.
ENTRYPOINT ["java", "-jar", "app.jar"]

ENV SPRING_PROFILES_ACTIVE=docker