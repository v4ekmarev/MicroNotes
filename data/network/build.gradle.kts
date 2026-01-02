import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    // Opt-in для ExperimentalTime (kotlinx.datetime использует kotlin.time.Instant)
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
    }
    
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "NetworkModule"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            
            // ============================================================
            // Ktor Client - мультиплатформенный HTTP клиент
            // https://github.com/ktorio/ktor
            // Docs: https://ktor.io/docs/client.html
            // ============================================================
            implementation(libs.ktor.client.core)              // Ядро клиента
            implementation(libs.ktor.client.content.negotiation) // JSON сериализация
            implementation(libs.ktor.client.auth)              // Bearer token auth
            implementation(libs.ktor.client.logging)           // Логирование запросов/ответов
            implementation(libs.ktor.serialization.json)       // Kotlinx JSON adapter
            
            // ============================================================
            // Koin - DI фреймворк
            // https://github.com/InsertKoinIO/koin
            // ============================================================
            implementation(libs.koin.core)
            
            // ============================================================
            // Kotlinx - мультиплатформенные утилиты
            // ============================================================
            implementation(libs.kotlinx.serialization.json) // https://github.com/Kotlin/kotlinx.serialization
            implementation(libs.kotlinx.datetime)           // https://github.com/Kotlin/kotlinx-datetime
        }
        
        androidMain.dependencies {
            // Android HTTP engine для Ktor
            implementation(libs.ktor.client.android)
            implementation(libs.koin.android)
        }
        
        iosMain.dependencies {
            // Darwin (iOS/macOS) HTTP engine для Ktor
            implementation(libs.ktor.client.darwin)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.develop.data.network"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    
    buildFeatures {
        buildConfig = true
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
