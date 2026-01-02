# =============================================================================
# MicroNotes Server Dockerfile
# Multi-stage build с оптимизациями безопасности и производительности
# =============================================================================

# -----------------------------------------------------------------------------
# Stage 1: Кеширование Gradle зависимостей
# -----------------------------------------------------------------------------
FROM gradle:8.5-jdk17 AS deps

WORKDIR /app

# Копируем только файлы сборки для кеширования зависимостей
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle/ gradle/
COPY server/build.gradle.kts server/

# Скачиваем зависимости (этот слой кешируется)
RUN gradle :server:dependencies --no-daemon || true

# -----------------------------------------------------------------------------
# Stage 2: Сборка приложения
# -----------------------------------------------------------------------------
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Копируем кешированные зависимости
COPY --from=deps /root/.gradle /root/.gradle

# Копируем исходный код
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle/ gradle/
COPY server/ server/

# Собираем fat JAR
RUN gradle :server:build -x test --no-daemon

# -----------------------------------------------------------------------------
# Stage 3: Runtime образ
# -----------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine

# Метаданные образа
LABEL maintainer="MicroNotes Team" \
      version="1.0" \
      description="MicroNotes REST API Server"

# Создаем non-root пользователя для безопасности (OWASP Rule #2)
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

# Копируем собранный JAR
COPY --from=builder /app/server/build/libs/server-*.jar app.jar

# Создаем директорию для данных и устанавливаем права
RUN mkdir -p /app/data && \
    chown -R appuser:appgroup /app

# Переключаемся на non-root пользователя
USER appuser

# Открываем порт
EXPOSE 8080

# Healthcheck без curl (используем wget из Alpine)
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

# Graceful shutdown
STOPSIGNAL SIGTERM

# JVM параметры для контейнера:
# -XX:MaxRAMPercentage=75.0 - использовать 75% доступной памяти для heap
# -XX:+UseContainerSupport - включить поддержку контейнеров (по умолчанию в JDK 17)
# -Djava.security.egd - ускорение генерации случайных чисел
ENTRYPOINT ["java", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseContainerSupport", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
