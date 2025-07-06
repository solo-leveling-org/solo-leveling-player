# Build stage
FROM gradle:8.14.2-jdk24-alpine AS build

# Создаём пользователя для безопасности
RUN adduser -D myuser && \
    mkdir -p /usr/src/app && \
    chown myuser:myuser /usr/src/app

WORKDIR /usr/src/app
USER myuser

# Копируем исходники
COPY --chown=myuser:myuser . .

# Собираем проект
RUN gradle build --no-daemon -x test

# Run stage
FROM amazoncorretto:24-alpine3.21-jdk

# Копируем собранный JAR
COPY --from=build /usr/src/app/solo-leveling-player-service/build/libs/*.jar /app/solo-leveling-player.jar

# Безопасность: создаём пользователя
RUN adduser -D myuser && \
    mkdir -p /app && \
    chown myuser:myuser /app

USER myuser
WORKDIR /app

# Открываем порт
EXPOSE 8080 9090

# Запускаем приложение
CMD ["java", \
    "--enable-native-access=ALL-UNNAMED", \
    "--add-opens", "java.base/java.lang=ALL-UNNAMED", \
    "-Dspring.profiles.active=dev", \
    "-jar", "solo-leveling-player.jar"]