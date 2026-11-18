# REST API сервиса бронирования рабочих пространств

Базовый адрес: `http://localhost:8080`.

Формат обмена — JSON. Заголовок запросов с телом: `Content-Type: application/json`.

## Пользователи — `/api/users`

| Метод | URL | Описание |
|---|---|---|
| `POST` | `/api/users` | Создать пользователя |
| `GET` | `/api/users` | Получить всех пользователей |
| `GET` | `/api/users?role={role}` | Фильтр по роли: `ADMIN`, `MANAGER`, `CUSTOMER` |
| `GET` | `/api/users?active={active}` | Фильтр по активности: `true`, `false` (комбинируется с `role`) |
| `GET` | `/api/users/{id}` | Получить пользователя |
| `PUT` | `/api/users/{id}` | Обновить пользователя (роль изменять нельзя) |
| `DELETE` | `/api/users/{id}` | Удалить пользователя |

### Создание пользователя

```json
{
  "fullName": "Петров Иван Сергеевич",
  "email": "customer@mail.ru",
  "password": "secret123",
  "role": "CUSTOMER",
  "phone": "+7 900 111-22-33",
  "company": "ООО Ромашка"
}
```

Дополнительные поля по ролям: `ADMIN` — `position`; `MANAGER` — `department`;
`CUSTOMER` — `phone`, `company`.

Ответ (`201`): тот же объект с присвоенными `id`, `active`, `createdAt`.
Хеш пароля в ответах не возвращается.

## Рабочие пространства — `/api/workspaces`

| Метод | URL | Описание |
|---|---|---|
| `POST` | `/api/workspaces` | Создать пространство |
| `GET` | `/api/workspaces` | Получить все пространства |
| `GET` | `/api/workspaces?type={type}` | Фильтр по типу: `MEETING_ROOM`, `COWORKING_DESK` |
| `GET` | `/api/workspaces?active={active}` | Фильтр по доступности (комбинируется с `type`) |
| `GET` | `/api/workspaces/{id}` | Получить пространство |
| `PUT` | `/api/workspaces/{id}` | Обновить пространство (тип изменять нельзя) |
| `DELETE` | `/api/workspaces/{id}` | Удалить пространство |

### Создание переговорной

```json
{
  "name": "Зал Альфа",
  "location": "3 этаж",
  "capacity": 10,
  "pricePerHour": 1000.00,
  "type": "MEETING_ROOM",
  "projector": true,
  "videoConference": false
}
```

### Создание коворкинг-места

```json
{
  "name": "Место A-12",
  "location": "2 этаж",
  "capacity": 1,
  "pricePerHour": 300.00,
  "type": "COWORKING_DESK",
  "monitorIncluded": true,
  "nearWindow": false
}
```

## Бронирования — `/api/bookings`

| Метод | URL | Описание |
|---|---|---|
| `POST` | `/api/bookings` | Создать бронирование (статус `PENDING`) |
| `GET` | `/api/bookings` | Получить все бронирования |
| `GET` | `/api/bookings?status={status}` | Фильтр по статусу: `PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED` |
| `GET` | `/api/bookings?customerEmail={email}` | Фильтр по e-mail клиента |
| `GET` | `/api/bookings?workspaceType={type}` | Фильтр по типу пространства |
| `GET` | `/api/bookings?date={yyyy-MM-dd}` | Фильтр по дате начала |
| `GET` | `/api/bookings?maxPrice={price}` | Фильтр по максимальной стоимости |
| `GET` | `/api/bookings/{id}` | Получить бронирование |
| `PUT` | `/api/bookings/{id}` | Изменить период (клиент и пространство изменять нельзя) |
| `POST` | `/api/bookings/{id}/confirm` | Подтвердить (только из `PENDING`) |
| `POST` | `/api/bookings/{id}/cancel` | Отменить (из `PENDING` или `CONFIRMED`) |
| `POST` | `/api/bookings/{id}/complete` | Завершить (только из `CONFIRMED`) |
| `DELETE` | `/api/bookings/{id}` | Удалить бронирование |

Фильтры комбинируются между собой.

### Создание бронирования

Даты указываются в будущем, иначе запрос отклоняется.

```json
{
  "customerId": 1,
  "workspaceId": 1,
  "start": "2026-12-01T10:00:00",
  "end": "2026-12-01T12:00:00"
}
```

## Коды ответов

- `200` — запрос выполнен;
- `201` — ресурс создан;
- `204` — ресурс удалён;
- `400` — ошибка валидации или некорректные аргументы;
- `404` — ресурс не найден;
- `409` — конфликт: дублирующийся e-mail или занятый интервал времени.

Тело ответа об ошибке: `{"timestamp": ..., "message": ...}`.
