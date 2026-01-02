# MicroNotes Server

REST API сервер для **транзитного шаринга** заметок между пользователями.

## Концепция

Сервер работает как **посредник** для передачи заметок:
1. Отправитель отправляет заметку получателю → заметка временно хранится на сервере
2. Получатель забирает заметку → сохраняет локально на устройстве
3. Получатель подтверждает получение → заметка **удаляется** с сервера

**Сервер НЕ хранит заметки постоянно** — только передаёт между пользователями.

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│  Sender     │  POST   │   Server    │   GET   │  Recipient  │
│  (Client)   │ ──────► │  (Transit)  │ ◄────── │  (Client)   │
└─────────────┘ /send   └─────────────┘ /inbox  └─────────────┘
                              │                        │
                              │    POST /inbox/{id}/ack│
                              │◄───────────────────────┘
                              │
                         [DELETE from DB]
```

## Технологии

| Библиотека | Версия | Описание | GitHub |
|------------|--------|----------|--------|
| **Ktor** | 3.1.3 | Асинхронный веб-фреймворк от JetBrains | [ktorio/ktor](https://github.com/ktorio/ktor) |
| **Netty** | - | Высокопроизводительный сетевой движок | [netty/netty](https://github.com/netty/netty) |
| **Exposed** | 0.61.0 | Kotlin SQL фреймворк от JetBrains | [JetBrains/Exposed](https://github.com/JetBrains/Exposed) |
| **H2** | 2.3.232 | Встраиваемая SQL база данных | [h2database/h2database](https://github.com/h2database/h2database) |
| **Koin** | 4.1.1 | Легковесный DI фреймворк | [InsertKoinIO/koin](https://github.com/InsertKoinIO/koin) |
| **BCrypt** | 0.10.2 | Хеширование паролей | [patrickfav/bcrypt](https://github.com/patrickfav/bcrypt) |
| **Logback** | 1.5.18 | Логирование (SLF4J) | [qos-ch/logback](https://github.com/qos-ch/logback) |
| **kotlinx-serialization** | 1.9.0 | JSON сериализация | [Kotlin/kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) |
| **kotlinx-datetime** | 0.7.1 | Работа с датами | [Kotlin/kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime) |

## Быстрый старт

### Требования
- JDK 17+
- Gradle 8+

### Запуск сервера

```bash
# Из корня проекта
./gradlew :server:run
```

Сервер запустится на `http://localhost:8080`

### Остановка сервера

**Способ 1:** `Ctrl+C` в терминале где запущен сервер

**Способ 2:** Убить процесс Gradle
```bash
pkill -f "server:run"
```

**Способ 3:** Убить процесс на порту 8080
```bash
lsof -ti:8080 | xargs kill -9
```

### Проверка работоспособности

```bash
curl http://localhost:8080/health
# Ответ: OK
```

## API Endpoints

### Аутентификация

| Метод | Endpoint | Описание | Auth |
|-------|----------|----------|------|
| POST | `/api/auth/register` | Регистрация нового пользователя | ❌ |
| POST | `/api/auth/login` | Вход в систему | ❌ |

#### Регистрация
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "username": "User", "password": "password123"}'
```

**Ответ:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "username": "User",
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

#### Вход
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "password": "password123"}'
```

---

### Контакты

| Метод | Endpoint | Описание | Auth |
|-------|----------|----------|------|
| GET | `/api/contacts` | Список контактов | ✅ |
| POST | `/api/contacts` | Добавить контакт | ✅ |
| DELETE | `/api/contacts/{id}` | Удалить контакт | ✅ |
| GET | `/api/users/search?q=` | Поиск пользователей | ✅ |

#### Добавить контакт
```bash
curl -X POST http://localhost:8080/api/contacts \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"email": "friend@example.com"}'
```

#### Поиск пользователей
```bash
curl "http://localhost:8080/api/users/search?q=john" \
  -H "Authorization: Bearer <token>"
```

---

### Отправка заметок (Sender)

| Метод | Endpoint | Описание | Auth |
|-------|----------|----------|------|
| POST | `/api/send` | Отправить заметку одному пользователю | ✅ |
| POST | `/api/send/many` | Отправить заметку нескольким пользователям | ✅ |
| DELETE | `/api/send/{id}` | Отменить отправку (пока не забрали) | ✅ |

#### Отправить заметку
```bash
curl -X POST http://localhost:8080/api/send \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"recipientId": 2, "title": "Привет!", "content": "Текст заметки"}'
```

**Ответ:**
```json
{
  "id": 1,
  "recipientId": 2,
  "recipientUsername": "Friend"
}
```

#### Отправить нескольким
```bash
curl -X POST http://localhost:8080/api/send/many \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"recipientIds": [2, 3, 4], "title": "Всем привет!", "content": "Текст"}'
```

---

### Входящие заметки (Recipient)

| Метод | Endpoint | Описание | Auth |
|-------|----------|----------|------|
| GET | `/api/inbox` | Список входящих заметок | ✅ |
| GET | `/api/inbox/count` | Количество входящих | ✅ |
| GET | `/api/inbox/{id}` | Получить конкретную заметку | ✅ |
| POST | `/api/inbox/{id}/ack` | Подтвердить получение (удаляет с сервера) | ✅ |

#### Получить входящие
```bash
curl http://localhost:8080/api/inbox \
  -H "Authorization: Bearer <token>"
```

**Ответ:**
```json
[
  {
    "id": 1,
    "senderId": 1,
    "senderUsername": "John",
    "senderEmail": "john@example.com",
    "title": "Привет!",
    "content": "Текст заметки",
    "createdAt": "2024-01-15T10:30:00Z"
  }
]
```

#### Подтвердить получение
```bash
# Вызывать ПОСЛЕ сохранения заметки локально!
curl -X POST http://localhost:8080/api/inbox/1/ack \
  -H "Authorization: Bearer <token>"
```

После этого заметка удаляется с сервера.

---

## Структура проекта

```
server/
├── build.gradle.kts          # Зависимости с комментариями
├── README.md                 # Эта документация
└── src/main/
    ├── kotlin/com/develop/server/
    │   ├── Application.kt    # Точка входа
    │   ├── database/
    │   │   ├── DatabaseFactory.kt
    │   │   └── tables/
    │   │       ├── Users.kt
    │   │       ├── PendingShares.kt  # Временное хранение
    │   │       └── Contacts.kt
    │   ├── di/
    │   │   └── ServerModule.kt
    │   ├── models/
    │   │   ├── User.kt
    │   │   ├── SharedNote.kt
    │   │   └── Contact.kt
    │   ├── plugins/
    │   │   ├── Authentication.kt
    │   │   ├── CORS.kt
    │   │   ├── Routing.kt
    │   │   └── Serialization.kt
    │   ├── repository/
    │   │   ├── UserRepository.kt
    │   │   ├── ContactRepository.kt
    │   │   └── PendingShareRepository.kt
    │   └── routes/
    │       ├── AuthRoutes.kt
    │       ├── ContactRoutes.kt
    │       └── InboxRoutes.kt
    └── resources/
        ├── application.conf
        └── logback.xml
```

## Конфигурация

### application.conf

```hocon
ktor {
    deployment {
        port = 8080
    }
}

jwt {
    secret = "your-secret-key"  # Сменить в продакшене!
    issuer = "micronotes-server"
    audience = "micronotes-client"
    expirationMs = 86400000     # 24 часа
}

database {
    driverClassName = "org.h2.Driver"
    jdbcUrl = "jdbc:h2:file:./data/micronotes"
}
```

### Переменные окружения

| Переменная | Описание | По умолчанию |
|------------|----------|--------------|
| `PORT` | Порт сервера | 8080 |
| `JWT_SECRET` | Секрет для JWT | (из конфига) |
| `DATABASE_URL` | JDBC URL базы данных | H2 file |

## Клиентский флоу

### Отправка заметки

```kotlin
// 1. Выбрать получателей из контактов
val contacts = sharingRepository.getContacts()

// 2. Отправить заметку
val result = sharingRepository.sendNote(
    recipientId = contact.userId,
    title = note.title,
    content = note.content
)
```

### Получение заметки

```kotlin
// 1. Проверить входящие (например, при старте приложения)
val inbox = sharingRepository.getInbox()

// 2. Для каждой входящей заметки:
for (share in inbox) {
    // 2.1. Сохранить локально
    noteRepository.insert(
        Note(
            title = share.title,
            content = share.content
        )
    )
    
    // 2.2. Подтвердить получение (удалит с сервера)
    sharingRepository.acknowledgeReceived(share.id)
}
```

## Продакшен

### Замена H2 на PostgreSQL

1. Добавить зависимость:
```kotlin
implementation("org.postgresql:postgresql:42.7.2")
```

2. Изменить `application.conf`:
```hocon
database {
    driverClassName = "org.postgresql.Driver"
    jdbcUrl = "jdbc:postgresql://localhost:5432/micronotes"
}
```

### Безопасность

- [ ] Сменить `JWT_SECRET` на случайную строку 32+ символов
- [ ] Настроить CORS для конкретных доменов
- [ ] Добавить rate limiting
- [ ] Включить HTTPS
- [ ] Добавить TTL для pending shares (автоудаление через N дней)

## Лицензия

MIT
