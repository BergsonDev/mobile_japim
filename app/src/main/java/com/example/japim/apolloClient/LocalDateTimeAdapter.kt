package com.example.japim.apolloClient

import com.apollographql.apollo.api.Adapter
import com.apollographql.apollo.api.CustomScalarAdapters
import com.apollographql.apollo.api.json.JsonReader
import com.apollographql.apollo.api.json.JsonWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LocalDateTimeAdapter : Adapter<LocalDateTime> {
    override fun fromJson(reader: JsonReader, customScalarAdapters: CustomScalarAdapters): LocalDateTime {
        return LocalDateTime.parse(reader.nextString()!!)
    }

    override fun toJson(writer: JsonWriter, customScalarAdapters: CustomScalarAdapters, value: LocalDateTime) {
        writer.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    }
}
