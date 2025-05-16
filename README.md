# TZ_user-subscription-service
REST API для управления пользователями и их подписками на сервисы 

## Описание проекта:

  Микросервис на Spring Boot 3, Java 17, который предоставляет REST API для управления пользователями и их подписками на сервисы.
  Подписки представляют собой подписки на цифровые сервисы, такие как YouTube Premium, VK Музыка, Яндекс.Плюс, Netflix и другие стриминговые платформы.
  Проект реализован как микросервис для легкой интеграции в распределенную архитектуру

### Основной функционал:
1.	Полное управление пользователями (CRUD операции)
2.	Полное управление подписками (CRUD операции + получить ТОП-3 популярных подписок)

### Используемые технологии:
- Backend: Spring Boot 3 (Java 17), Spring Data JPA, REST API.
- База данных: PostgreSQL, Flyway.
- Утилиты и инструменты:  MapStruct,  Lombok, Spring Validation,
- Логирование:  SLF4J
- Тестирование: Spring Boot Test, Junit + Mockito , Testcontainers
- Документация API: Swagger  (SpringDoc OpenAPI )
- Контейнеризация: Docker (образ сервиса + Postgres в docker-compose).

### Особенности:
- Интеграция с реляционной СУБД через JPA/Hibernate.
- Готовое окружение для локального запуска (Dockerfile, docker-compose.yml) - позволит локально запускать проект
вместе с базой данных.
- Поддержка типовых CRUD-операций для сущностей (users, subscriptions).


### Требования к API
Примерные эндпоинты:
POST /users - создать пользователя
GET /users/{id} - получить информацию о пользователе
PUT /users/{id} - обновить пользователя
DELETE /users/{id} - удалить пользователя
POST /users/{id}/subscriptions - добавить подписку
GET /users/{id}/subscriptions - получить подписки пользователя
DELETE /users/{id}/subscriptions/{sub_id} - удалить подписку
GET /subscriptions/top - получить ТОП-3 популярных подписок


Реализация
### Эндпоинты:
- POST /users - создать пользователя
- GET /users/ - получить список всех пользователей
- GET /users/{user_id} - получить информацию о пользователе его id
- PUT /users/{user_id} - обновить пользователя по id
- DELETE /users/{user_id} - удалить пользователя по id

- POST /subscriptions/users/{user_id} - добавить новую подписку пользователю
- GET /subscriptions/ - получить все подписки из БД
- GET /subscriptions/{subscription_id} - получить подписку по id подписки
- GET /subscriptions/users/{user_id} - получить все подписки пользователя
- PUT /subscriptions/{subscription_id} - обновить подписку по id
- DELETE /subscriptions/{subscription_id}/users/{user_id} - удалить подписку по id у пользователя
- GET /subscriptions/top - получить ТОП-3 популярных подписок

### Swagger
- http://localhost:8080/swagger-ui/index.html
