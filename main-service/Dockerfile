# Этап 1: Сборка артефакта внутри Docker
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /app
COPY . .
# Собираем модуль main-service
RUN mvn clean package -pl main-service -am -DskipTests
# Находим единственный файл, в имени которого НЕТ слова plain, и переименовываем его в итоговый app.jar
RUN find main-service/target/ -name "*.jar" ! -name "*plain.jar" -exec mv {} main-service/target/app.jar \;

# Этап 2: Минимальный образ для запуска приложения
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Копируем наш гарантированно переименованный файл
COPY --from=build /app/main-service/target/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
