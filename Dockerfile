FROM eclipse-temurin:26-jre
WORKDIR /app
COPY target/autorent-0.0.1-SNAPSHOT.jar.original app.jar
CMD ["java", "-jar", "app.jar"]