# ===== المرحلة الأولى: البناء =====
FROM eclipse-temurin:26-jdk AS builder

WORKDIR /app

# نسخ ملفات Maven أولاً (لتحسين الـ cache)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# تحميل الـ dependencies
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# نسخ الكود المصدري والبناء
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ===== المرحلة الثانية: التشغيل =====
FROM eclipse-temurin:26-jre

WORKDIR /app

# نسخ الـ JAR فقط من مرحلة البناء
COPY --from=builder /app/target/autorent-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]