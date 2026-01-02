# MicroNotes Server — Docker

Документация по контейнеризации MicroNotes REST API сервера.

---

## Архитектура сборки

### Multi-stage Dockerfile

```
┌─────────────────────────────────────────────────────────────────┐
│  Stage 1: deps (gradle:8.5-jdk17)                               │
│  ├── Копирует только build.gradle.kts, settings.gradle.kts     │
│  └── Скачивает зависимости → кешируется Docker                  │
├─────────────────────────────────────────────────────────────────┤
│  Stage 2: builder (gradle:8.5-jdk17)                            │
│  ├── Копирует Gradle кеш из deps                                │
│  ├── Копирует исходный код                                      │
│  └── Собирает fat JAR (без тестов)                              │
├─────────────────────────────────────────────────────────────────┤
│  Stage 3: runtime (eclipse-temurin:17-jre-alpine)               │
│  ├── Минимальный образ (~150MB)                                 │
│  ├── Non-root пользователь (appuser:1001)                       │
│  ├── Healthcheck через wget                                     │
│  └── JVM с оптимизацией для контейнеров                         │
└─────────────────────────────────────────────────────────────────┘
```

### Преимущества

| Аспект | Реализация |
|--------|------------|
| **Размер образа** | ~150MB (Alpine + JRE вместо ~400MB с JDK) |
| **Скорость сборки** | Gradle кеш переиспользуется между сборками |
| **Безопасность** | Non-root user, read-only filesystem |
| **Память** | JVM уважает лимиты контейнера (`MaxRAMPercentage`) |

---

## Быстрый старт

### Запуск через docker-compose

```bash
# Из корня проекта
docker-compose up -d

# Проверка
curl http://localhost:8080/health
```

### Запуск через Docker CLI

```bash
# Сборка
docker build -t micronotes-server .

# Запуск
docker run -d \
  --name micronotes-server \
  -p 8080:8080 \
  -e JWT_SECRET=your-secret-key \
  -v micronotes-data:/app/data \
  --memory=512m \
  --cpus=1.0 \
  --security-opt=no-new-privileges:true \
  --read-only \
  --tmpfs /tmp:size=64M,mode=1777 \
  micronotes-server
```

---

## Конфигурация

### Переменные окружения

| Переменная | Описание | По умолчанию |
|------------|----------|--------------|
| `PORT` | Порт сервера | `8080` |
| `JWT_SECRET` | Секрет для JWT токенов | `your-super-secret-jwt-key...` |
| `DATABASE_URL` | JDBC URL базы H2 | `jdbc:h2:file:/app/data/micronotes` |
| `TZ` | Часовой пояс | `UTC` |

### Лимиты ресурсов

| Параметр | Значение | Описание |
|----------|----------|----------|
| `mem_limit` | 512MB | Лимит памяти контейнера |
| `cpus` | 1.0 | Лимит CPU |
| `JVM heap` | 75% RAM | ~384MB для heap |

### JVM параметры (в Dockerfile)

```
-XX:MaxRAMPercentage=75.0    # Heap = 75% от лимита контейнера
-XX:+UseContainerSupport     # JVM видит лимиты cgroups
-Djava.security.egd=...      # Быстрая генерация случайных чисел
```

---

## Безопасность (OWASP)

| Мера | OWASP Rule | Реализация |
|------|------------|------------|
| **Non-root user** | Rule #2 | `USER appuser` (uid 1001) |
| **No privilege escalation** | Rule #4 | `no-new-privileges:true` |
| **Resource limits** | Rule #7 | `mem_limit`, `cpus` |
| **Read-only filesystem** | Rule #8 | `read_only: true` + tmpfs |

### Файловая система

```
/app/app.jar      # Read-only (из образа)
/app/data/        # Read-write (volume для H2)
/tmp/             # tmpfs (64MB, для временных файлов)
```

---

## Управление

### Основные команды

```bash
# Статус
docker-compose ps

# Логи (follow)
docker-compose logs -f micronotes-server

# Последние 100 строк
docker-compose logs --tail=100 micronotes-server

# Остановка
docker-compose down

# Перезапуск
docker-compose restart micronotes-server

# Пересборка
docker-compose build --no-cache
docker-compose up -d
```

### Healthcheck

```bash
# Проверка endpoint
curl http://localhost:8080/health

# Статус healthcheck в Docker
docker inspect --format='{{.State.Health.Status}}' micronotes-server
```

---

## База данных

- **Тип:** H2 (файловая)
- **Путь в контейнере:** `/app/data/micronotes`
- **Персистентность:** Docker volume `micronotes-data`

### Backup

```bash
# Архив из volume
docker run --rm \
  -v micronotes-data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/micronotes-backup.tar.gz -C /data .
```

### Restore

```bash
docker-compose down

docker run --rm \
  -v micronotes-data:/data \
  -v $(pwd):/backup \
  alpine tar xzf /backup/micronotes-backup.tar.gz -C /data

docker-compose up -d
```

---

## Продакшен

### 1. Создайте `.env` файл

```bash
# Генерация безопасного секрета
echo "JWT_SECRET=$(openssl rand -base64 32)" > .env
```

### 2. Запуск с `.env`

```bash
docker-compose --env-file .env up -d
```

### 3. Чеклист

- [ ] Сменить `JWT_SECRET` (минимум 32 символа)
- [ ] Настроить HTTPS (nginx/traefik reverse proxy)
- [ ] Настроить мониторинг (Prometheus/Grafana)
- [ ] Настроить централизованные логи
- [ ] Автоматический backup базы данных
- [ ] Рассмотреть PostgreSQL вместо H2

---

## Troubleshooting

### Порт занят

```bash
lsof -ti:8080           # Найти процесс
kill -9 $(lsof -ti:8080) # Убить процесс

# Или сменить порт в docker-compose.yml:
# ports: ["8081:8080"]
```

### Проблемы с памятью

```bash
# Мониторинг
docker stats micronotes-server

# Увеличить лимит в docker-compose.yml:
# mem_limit: 1g
```

### Проблемы с правами

```bash
# Проверить пользователя
docker exec micronotes-server whoami
# → appuser

# Проверить права
docker exec micronotes-server ls -la /app/data
```

### Очистка Docker

```bash
# Удалить неиспользуемые образы и кеш
docker system prune -a

# Удалить volume (ОСТОРОЖНО — удалит данные!)
docker volume rm micronotes-data
```

---

## Структура файлов

```
MicroNotes/
├── Dockerfile           # Multi-stage сборка
├── docker-compose.yml   # Конфигурация запуска
├── .dockerignore        # Исключения для сборки
├── DOCKER.md            # Эта документация
└── server/              # Исходный код сервера
    └── build.gradle.kts
```
