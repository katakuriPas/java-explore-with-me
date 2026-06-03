# Этап 1: Сборка артефакта внутри Docker
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /app
COPY . .
# Собираем модуль stats-server
RUN mvn clean package -pl stats-service-parent/stats-server -am -DskipTests
# Находим единственный исполняемый jar и переименовываем его в app.jar
RUN find stats-service-parent/stats-server/target/ -name "*.jar" ! -name "*plain.jar" -exec mv {} stats-service-parent/stats-server/target/app.jar \;

# Этап 2: Минимальный образ для запуска приложения
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Копируем переименованный файл
COPY --from=build /app/stats-service-parent/stats-server/target/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
