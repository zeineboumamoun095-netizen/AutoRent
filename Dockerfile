# =============================================
#  AutoRent — Dockerfile
#  Build multi-stage pour image légère
# =============================================

# ── Stage 1: Build avec Maven ────────────────
FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app

# Copier pom.xml en premier (cache des dépendances)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source et compiler
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Stage 2: Image finale légère ─────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copier le JAR depuis l'étape build
COPY --from=build /app/target/*.jar app.jar

# Port exposé
EXPOSE 8080

# Lancer avec profil PROD
ENTRYPOINT ["java", \
  "-Dspring.profiles.active=prod", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]