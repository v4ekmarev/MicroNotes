# --- Room / KMP ---
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Entities, Database, DAO
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Database class * { *; }
-keep @androidx.room.Dao class * { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# --- SQLite bundled ---
-dontwarn androidx.sqlite.**

# --- Koin ---
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# --- Kotlin Serialization (если используешь @Serializable) ---
-keep class kotlinx.serialization.** { *; }
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers class * { @kotlinx.serialization.* <methods>; }
-keepattributes *Annotation*, Signature
-dontwarn kotlinx.serialization.**

# --- Compose (safety) ---
-dontwarn kotlin.Unit
-dontwarn kotlin.reflect.**
