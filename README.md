# Сервис бронирования рабочих пространств

Учебный проект по курсу «Разработка клиент-серверных приложений на Java».
Лабораторная работа № 1 «Базовые типы данных. Создание собственных классов».

## Тема проекта

Сервис бронирования рабочих пространств: переговорных комнат и коворкинг-мест.
Пользователь с ролью клиента создаёт бронирование, менеджер подтверждает его,
администратор управляет пользователями и пространствами. В следующих
лабораторных работах сервис будет переведён на PostgreSQL, Spring Data JPA,
REST API и Spring Security.

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
- Spring Boot 3.3.5
- Maven
- JUnit 5, AssertJ
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
    │   │   │   ├── ConsolePrinter.java
    │   │   │   ├── DemoData.java
    │   │   │   ├── DemoDataSeeder.java
    │   │   │   └── DemoRunner.java
    │   │   ├── concurrency/
    │   │   │   ├── BookingProcessor.java
    │   │   │   ├── NamedThreadFactory.java
    │   │   │   ├── ParallelBookingProcessor.java
    │   │   │   ├── ProcessedBooking.java
    │   │   │   └── ProcessingReport.java
    │   │   ├── model/
    │   │   │   ├── AdminUser.java
    │   │   │   ├── Booking.java
    │   │   │   ├── BookingSearchFilter.java
    │   │   │   ├── BookingStatus.java
    │   │   │   ├── CoworkingDesk.java
    │   │   │   ├── CustomerUser.java
    │   │   │   ├── Identifiable.java
    │   │   │   ├── ManagerUser.java
    │   │   │   ├── MeetingRoom.java
    │   │   │   ├── User.java
    │   │   │   ├── UserRole.java
    │   │   │   ├── Workspace.java
    │   │   │   └── WorkspaceType.java
    │   │   ├── repository/
    │   │   │   ├── AbstractInMemoryRepository.java
    │   │   │   ├── BookingRepository.java
    │   │   │   ├── CrudRepository.java
    │   │   │   ├── InMemoryBookingRepository.java
    │   │   │   ├── InMemoryUserRepository.java
    │   │   │   ├── InMemoryWorkspaceRepository.java
    │   │   │   ├── UserRepository.java
    │   │   │   └── WorkspaceRepository.java
    │   │   ├── service/
    │   │   │   ├── BookingQueue.java
    │   │   │   ├── BookingService.java
    │   │   │   ├── BookingServiceImpl.java
    │   │   │   ├── ConcurrentBookingQueue.java
    │   │   │   ├── PasswordHasher.java
    │   │   │   ├── Sha256PasswordHasher.java
    │   │   │   ├── UserService.java
    │   │   │   ├── UserServiceImpl.java
    │   │   │   ├── WorkspaceService.java
    │   │   │   └── WorkspaceServiceImpl.java
    │   │   └── util/
    │   │       └── TextUtils.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/example/booking/
            ├── TestFixtures.java
            ├── concurrency/
            ├── model/
            ├── repository/
            ├── service/
            └── util/
```

## Назначение пакетов

- `model` — предметная модель: пользователи, роли, пространства, бронирования,
  статусы и фильтр поиска. Классы не зависят от Spring и легко тестируются.
- `repository` — контракты хранения и in-memory реализации. Общий CRUD вынесен
  в `CrudRepository`, общая реализация хранения — в
  `AbstractInMemoryRepository`.
- `service` — бизнес-логика. Интерфейсы отделены от реализаций, зависимости
  передаются через конструкторы.
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

## Запуск

Требования: JDK 17, Maven 3.9+.

```bash
mvn spring-boot:run
```

или:

```bash
mvn clean package
java -jar target/booking-service-1.0.0.jar
```

Приложение не является веб-сервером: после выполнения демонстрационного
сценария оно завершает работу. Лог дополнительно пишется в
`logs/booking-service.log`.

## Тесты

```bash
mvn test
```

Полная проверка, включая Checkstyle и сборку:

```bash
mvn verify
```

Покрыты:

- `TextUtils` — нормализация и проверка строк;
- `User`, `AdminUser`, `ManagerUser`, `CustomerUser` — роли и разрешения;
- `Booking`, `BookingSearchFilter` — расчёт стоимости, статусы, фильтрация;
- `Workspace`, `MeetingRoom`, `CoworkingDesk` — типы и оснащение;
- in-memory репозитории — CRUD, уникальность e-mail, поиск, пересечения;
- `ConcurrentBookingQueue` — операции очереди;
- `Sha256PasswordHasher` — хеширование и проверка пароля;
- сервисы пользователей, пространств и бронирований;
- `ParallelBookingProcessor` — реальная работа в нескольких именованных потоках.

## CI

В корне проекта находится `.gitlab-ci.yml` с pipeline `verify`, который
запускает `mvn verify` в образе `maven:3-openjdk-17`. Pipeline должен быть
зелёным до отправки работы на проверку.

## Git и сдача работы

Проект рассчитан на один репозиторий для всех лабораторных работ. Текущая
работа находится в ветке `lab1`; после проверки и получения Approve её можно
смержить в `main`. Merge Request не закрывается до проверки преподавателем.
