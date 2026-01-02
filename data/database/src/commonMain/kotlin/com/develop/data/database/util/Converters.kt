package com.develop.data.database.util

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class Converters(
    private val json: Json = Json
) {
    @TypeConverter
    fun listToJson(list: List<String>): String = json.encodeToString(ListSerializer(String.serializer()), list)

    @TypeConverter
    fun jsonToList(value: String): List<String> = json.decodeFromString(ListSerializer(String.serializer()), value)
}
