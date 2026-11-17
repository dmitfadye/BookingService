# Сервис бронирования рабочих пространств

Учебный проект по курсу «Разработка клиент-серверных приложений на Java».
Лабораторная работа № 1 «Базовые типы данных. Создание собственных классов».
Лабораторная работа № 2 «Взаимодействие с базой данных. Spring JPA. Разработка REST сервиса».

## Тема проекта

Сервис бронирования рабочих пространств: переговорных комнат и коворкинг-мест.
Пользователь с ролью клиента создаёт бронирование, менеджер подтверждает его,
администратор управляет пользователями и пространствами. В лабораторной
работе № 2 сервис переведён на PostgreSQL, Spring Data JPA и REST API.

## Что реализовано в лабораторной работе № 1

- иерархия пользователей: `User` → `AdminUser`, `ManagerUser`, `CustomerUser`;
- перечисление ролей `UserRole` (`ADMIN`, `MANAGER`, `CUSTOMER`);
- иерархия рабочих пространств: `Workspace` → `MeetingRoom`, `CoworkingDesk`;
- сущность `Booking` со статусами `PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`;
- интерфейсы `Identifiable`, `CrudRepository`, `UserRepository`,
  `WorkspaceRepository`, `BookingRepository`;
- in-memory реализации хранилищ на `Map`, `Set`, `List` и `ConcurrentHashMap`;
- очередь ожидающих подтверждения бронирований на `ConcurrentLinkedQueue`;
- сервисы с бизнес-логикой: регистрация пользователей, добавление пространств,
  создание, поиск, подтверждение, отмена, завершение и удаление бронирований;
- поиск по e-mail, ФИО, статусу, типу пространства, дате и максимальной цене
  (класс `BookingSearchFilter`);
- многопоточная обработка очереди через `ExecutorService`, `CountDownLatch`,
  `Future` и фабрику потоков `NamedThreadFactory`; имена потоков имеют вид
  `booking-worker-1`, `booking-worker-2`, ...;
- unit-тесты на JUnit 5 и AssertJ (111 тестов);
- консольная демонстрация работы приложения;
- Checkstyle-правило, запрещающее использование `var`.

## Стек

- Java 17
- Spring Boot 3.4.4
- Spring MVC
- Spring Data JPA
- PostgreSQL
- Lombok
- H2 для тестов
- Maven
- JUnit 5, AssertJ, Mockito, MockMvc
- SLF4J / Logback
- Checkstyle

## Роли пользователей

| Роль | Класс | Возможности |
|---|---|---|
| `ADMIN` | `AdminUser` | управление пользователями, пространствами и бронированиями |
| `MANAGER` | `ManagerUser` | управление пространствами, подтверждение бронирований |
| `CUSTOMER` | `CustomerUser` | создание и отмена собственных бронирований |

Абстрактный класс `User` хранит общие поля (id, ФИО, e-mail, хеш пароля, дата
регистрации, признак активности) и задаёт абстрактные методы разрешений.
Наследники реализуют роль и правила доступа, что позволяет в лабораторной
работе № 4 подключить Spring Security без переделки модели.

## Структура проекта

```text
booking-service/
├── .github/workflows/ci.yaml
├── .gitlab-ci.yml
├── README.md
├── pom.xml
├── config/
│   └── checkstyle/
│       └── checkstyle.xml
└── src/
    ├── main/
    │   ├── java/com/example/booking/
    │   │   ├── BookingServiceApplication.java
    │   │   ├── console/
    │   │   ├── concurrency/
    │   │   ├── controller/          # ЛР2: REST-контроллеры и ApiExceptionHandler
    │   │   ├── dto/                 # ЛР2: запросы и ответы REST API
    │   │   ├── model/               # JPA-сущности (ЛР1 + аннотации ЛР2)
    │   │   ├── repository/          # in-memory (ЛР1) + JpaRepository (ЛР2)
    │   │   ├── service/             # бизнес-логика (ЛР1) + *ApiService (ЛР2)
    │   │   └── util/
    │   └── resources/
    │       └── application.properties
    └── test/
        ├── java/com/example/booking/
        └── resources/
            └── application.properties  # ЛР2: изолированная H2-база для тестов
```

## Назначение пакетов

- `model` — предметная модель: пользователи, роли, пространства, бронирования,
  статусы и фильтр поиска. В ЛР2 классы стали JPA-сущностями: `User`
  и `Workspace` используют иерархию `SINGLE_TABLE` с дискриминаторами
  `user_type` / `workspace_type`, `Booking` связан с ними через `@ManyToOne`.
- `repository` — контракты хранения и in-memory реализации (ЛР1), а также
  Spring Data JPA-репозитории `UserJpaRepository`, `WorkspaceJpaRepository`,
  `BookingJpaRepository` с derived query-методами (ЛР2).
- `dto` — DTO-объекты запросов и ответов REST API с валидацией (ЛР2).
- `service` — бизнес-логика (ЛР1). В ЛР2 добавлены `UserApiService`,
  `WorkspaceApiService`, `BookingApiService` и их `*Impl` на JPA,
  а также `ResourceNotFoundException`.
- `controller` — REST-контроллеры `/api/users`, `/api/workspaces`,
  `/api/bookings`, `ApiError` и `ApiExceptionHandler` (ЛР2).
- `concurrency` — многопоточная обработка очереди и отчёт о выполнении.
- `console` — демонстрационный сценарий и форматированный вывод.
- `util` — нормализация и проверка строк.

## Используемые структуры данных

| Структура | Где используется | Зачем |
|---|---|---|
| `List` | результаты поиска, `findAll()` | упорядоченный вывод |
| `Set` | `registeredEmails` в `InMemoryUserRepository` | контроль уникальности e-mail |
| `Map` | `ConcurrentHashMap` в репозиториях | хранение сущностей по id |
| `Queue` | `ConcurrentLinkedQueue` в `ConcurrentBookingQueue` | очередь на подтверждение |
| `EnumMap` | `BookingServiceImpl.countByStatus()` | подсчёт по статусам |

## Алгоритмы обработки данных

- добавление пользователей, пространств и бронирований;
- поиск пользователя по e-mail без учёта регистра;
- поиск бронирований по e-mail, ФИО, статусу, типу пространства, дате и цене;
- проверка пересечения интервалов бронирования;
- удаление бронирования и пользователя;
- подсчёт количества бронирований по статусам и выручки;
- нормализация строк: e-mail, ФИО, маскирование e-mail;
- многопоточное подтверждение очереди с логированием имён потоков.

## Многопоточность

`ParallelBookingProcessor` получает очередь `PENDING`-бронирований, делит её на
части по числу потоков и запускает задачи в `Executors.newFixedThreadPool`.
Потоки создаются фабрикой `NamedThreadFactory` с именами
`booking-worker-1`, `booking-worker-2`, ... Все задачи одновременно стартуют
через `CountDownLatch`, что гарантирует параллельную работу и наглядный лог.
Результат содержит имена потоков, количество успешных операций, ошибки и время.

Пример лога:

```text
20:29:46.387 INFO [booking-worker-3] ... [booking-worker-3] Взят в обработку заказ #5
20:29:46.387 INFO [booking-worker-1] ... [booking-worker-1] Взят в обработку заказ #1
20:29:46.387 INFO [booking-worker-2] ... [booking-worker-2] Взят в обработку заказ #3
...
Обработка завершена: успешно 6, ошибок 0, потоки [booking-worker-1, booking-worker-2, booking-worker-3]
```

## REST API (ЛР2)

### Пользователи (`/api/users`)

- `POST /api/users` — создать пользователя;
- `PUT /api/users/{id}` — обновить пользователя;
- `GET /api/users` — получить всех пользователей;
- `GET /api/users/{id}` — получить пользователя;
- `GET /api/users?role={role}` — фильтр по роли (`ADMIN`, `MANAGER`, `CUSTOMER`);
- `GET /api/users?active={active}` — фильтр по активности;
- `DELETE /api/users/{id}` — удалить пользователя.

### Пространства (`/api/workspaces`)

- `POST /api/workspaces` — создать пространство;
- `PUT /api/workspaces/{id}` — обновить пространство;
- `GET /api/workspaces` — получить все пространства;
- `GET /api/workspaces/{id}` — получить пространство;
- `GET /api/workspaces?type={type}` — фильтр по типу
  (`MEETING_ROOM`, `COWORKING_DESK`);
- `GET /api/workspaces?active={active}` — фильтр по доступности;
- `DELETE /api/workspaces/{id}` — удалить пространство.

### Бронирования (`/api/bookings`)

- `POST /api/bookings` — создать бронирование;
- `PUT /api/bookings/{id}` — обновить период бронирования;
- `GET /api/bookings` — получить все бронирования;
- `GET /api/bookings/{id}` — получить бронирование;
- `GET /api/bookings?status={status}` — фильтр по статусу
  (`PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`);
- `GET /api/bookings?customerEmail={email}` — фильтр по e-mail клиента;
- `GET /api/bookings?workspaceType={type}` — фильтр по типу пространства;
- `GET /api/bookings?date={yyyy-MM-dd}` — фильтр по дате начала;
- `GET /api/bookings?maxPrice={price}` — фильтр по максимальной стоимости;
- `POST /api/bookings/{id}/confirm` — подтвердить бронирование;
- `POST /api/bookings/{id}/cancel` — отменить бронирование;
- `POST /api/bookings/{id}/complete` — завершить бронирование;
- `DELETE /api/bookings/{id}` — удалить бронирование.

Ошибки возвращаются в формате `{"timestamp": ..., "message": ...}`:
`404` — ресурс не найден, `409` — конфликт (дубликат, занятый интервал),
`400` — ошибка валидации или некорректные аргументы.

## Схема базы данных (ЛР2)

Схема создаётся Hibernate по JPA-аннотациям при запуске приложения
(`spring.jpa.hibernate.ddl-auto=update`), в PgAdmin таблицы появляются
автоматически. Тесты используют изолированную H2-базу и не подключаются
к PostgreSQL.

- `app_users` — все пользователи (`SINGLE_TABLE`, дискриминатор `user_type`:
  `ADMIN`, `MANAGER`, `CUSTOMER`): `id`, `full_name`, `email` (unique),
  `password_hash`, `active`, `created_at`, `position`, `department`, `phone`,
  `company`;
- `workspaces` — все пространства (`SINGLE_TABLE`, дискриминатор
  `workspace_type`: `MEETING_ROOM`, `COWORKING_DESK`): `id`, `name`,
  `location`, `capacity`, `price_per_hour`, `active`, `projector`,
  `video_conference`, `monitor_included`, `near_window`;
- `bookings` — бронирования: `id`, `customer_id` (FK → `app_users`),
  `workspace_id` (FK → `workspaces`), `start_time`, `end_time`,
  `created_at`, `total_price`, `status`.

## Запуск

Требования: JDK 17, Maven 3.9+, PostgreSQL.

Перед запуском задайте переменные окружения (либо используйте значения
по умолчанию для локального PostgreSQL):

```bash
export DB_URL=jdbc:postgresql://localhost:5432/booking_service
export DB_USERNAME=postgres
export DB_PASSWORD=secret
```

```bash
mvn spring-boot:run
```

или:

```bash
mvn clean package
java -jar target/booking-service-1.0.0.jar
```

Приложение стартует как веб-сервер (по умолчанию порт `8080`),
консольная демонстрация ЛР1 выполняется при старте, лог дополнительно
пишется в `logs/booking-service.log`.

## Тесты

```bash
mvn test
```

Полная проверка, включая Checkstyle и сборку:

```bash
mvn verify
```

ЛР1, обычные unit-тесты без Spring:

- `TextUtils` — нормализация и проверка строк;
- `User`, `AdminUser`, `ManagerUser`, `CustomerUser` — роли и разрешения;
- `Booking`, `BookingSearchFilter` — расчёт стоимости, статусы, фильтрация;
- `Workspace`, `MeetingRoom`, `CoworkingDesk` — типы и оснащение;
- in-memory репозитории — CRUD, уникальность e-mail, поиск, пересечения;
- `ConcurrentBookingQueue` — операции очереди;
- `Sha256PasswordHasher` — хеширование и проверка пароля;
- сервисы пользователей, пространств и бронирований;
- `ParallelBookingProcessor` — реальная работа в нескольких именованных потоках.

ЛР2, `@SpringBootTest` + Mockito, без обращения к PostgreSQL:

- `BookingServiceApplicationTests` — подъём Spring-контекста с `@MockitoBean`
  для JPA-репозиториев;
- `BookingControllerTest` — `MockMvc`-проверка `GET /api/bookings?status=…`
  с замоканным `BookingApiService`.

## CI

В корне проекта находится `.gitlab-ci.yml` с pipeline `verify`, который
запускает `mvn verify` в образе `maven:3-openjdk-17`. Pipeline должен быть
зелёным до отправки работы на проверку.