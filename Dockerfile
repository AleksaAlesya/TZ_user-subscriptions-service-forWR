
# Используем официальный образ OpenJDK 17, будет скачан с докерхаба
#image-образ, который б. использовать как базу jdk в нее же уже включена ОС
#т.е. на нем будет разворачиваться наш jar файл
FROM openjdk:17-jdk-slim
# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app
# Копируем скомпилированный JAR-файл в контейнер
COPY target/user_subscriptions_service-0.0.1-SNAPSHOT.jar user_subscriptions_service.jar
# Указываем команду запуска
ENTRYPOINT ["java", "-jar", "user_subscriptions_service.jar"]

