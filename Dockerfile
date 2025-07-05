# Build stage
FROM gradle:8.14.2-jdk24 AS build

# Создаём пользователя для безопасности
RUN useradd -m myuser && \
    mkdir -p /usr/src/app && \
    chown myuser:myuser /usr/src/app

WORKDIR /usr/src/app
USER myuser

# Копируем исходники
COPY --chown=myuser:myuser . .

# Собираем проект
RUN gradle build --no-daemon -x test

# Run stage
FROM eclipse-temurin:24

# Копируем собранный JAR
COPY --from=build /usr/src/app/solo-leveling-player-service/build/libs/*.jar /app/solo-leveling-player.jar

# Безопасность: создаём пользователя
RUN useradd -m myuser && \
    mkdir -p /app && \
    chown myuser:myuser /app

USER myuser
WORKDIR /app

# Открываем порт
EXPOSE 8080

# Запускаем приложение
CMD ["java", "-jar", "solo-leveling-player.jar"]