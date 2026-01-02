plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    application
}

application {
    mainClass.set("com.develop.server.ApplicationKt")
}

dependencies {
    // ============================================================
    // Ktor Server - асинхронный веб-фреймворк от JetBrains
    // https://github.com/ktorio/ktor
    // Docs: https://ktor.io/docs/
    // ============================================================
    
    // Ядро Ktor сервера
    implementation(libs.ktor.server.core)
    
    // Netty - высокопроизводительный асинхронный сетевой движок
    // https://github.com/netty/netty
    implementation(libs.ktor.server.netty)
    
    // Content Negotiation - автоматическая сериализация/десериализация JSON
    implementation(libs.ktor.server.content.negotiation)
    
    // Authentication - базовый модуль аутентификации
    implementation(libs.ktor.server.auth)
    
    // JWT Auth - аутентификация через JSON Web Tokens
    // https://jwt.io/
    implementation(libs.ktor.server.auth.jwt)
    
    // CORS - Cross-Origin Resource Sharing для работы с веб-клиентами
    implementation(libs.ktor.server.cors)
    
    // Call Logging - логирование HTTP запросов
    implementation(libs.ktor.server.call.logging)
    
    // Kotlinx Serialization adapter для Ktor
    implementation(libs.ktor.serialization.json)
    
    // ============================================================
    // Logging - логирование
    // ============================================================
    
    // Logback - реализация SLF4J для логирования
    // https://github.com/qos-ch/logback
    implementation(libs.logback.classic)
    
    // ============================================================
    // Database - работа с базой данных
    // ============================================================
    
    // Exposed - Kotlin SQL фреймворк от JetBrains
    // https://github.com/JetBrains/Exposed
    // Docs: https://jetbrains.github.io/Exposed/
    implementation(libs.exposed.core)      // Ядро и DSL
    implementation(libs.exposed.dao)       // DAO паттерн
    implementation(libs.exposed.jdbc)      // JDBC драйвер
    implementation(libs.exposed.kotlin.datetime) // Поддержка kotlinx-datetime
    
    // H2 Database - встраиваемая SQL база данных (для разработки)
    // https://github.com/h2database/h2database
    // В продакшене заменить на PostgreSQL
    implementation(libs.h2.database)
    
    // ============================================================
    // Security - безопасность
    // ============================================================
    
    // BCrypt - хеширование паролей
    // https://github.com/patrickfav/bcrypt
    implementation(libs.bcrypt)
    
    // ============================================================
    // DI - Dependency Injection
    // ============================================================
    
    // Koin - легковесный DI фреймворк для Kotlin
    // https://github.com/InsertKoinIO/koin
    // Docs: https://insert-koin.io/
    implementation(libs.koin.core)
    implementation(libs.koin.ktor) // Интеграция с Ktor
    
    // ============================================================
    // Serialization - сериализация данных
    // ============================================================
    
    // Kotlinx Serialization - мультиплатформенная сериализация
    // https://github.com/Kotlin/kotlinx.serialization
    implementation(libs.kotlinx.serialization.json)
    
    // Kotlinx Datetime - мультиплатформенная работа с датами
    // https://github.com/Kotlin/kotlinx-datetime
    implementation(libs.kotlinx.datetime)
    
    // ============================================================
    // Testing
    // ============================================================
    testImplementation(libs.kotlin.test)
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
    }
}
