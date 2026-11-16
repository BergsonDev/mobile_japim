package com.example.japim.apolloClient

import com.apollographql.apollo.api.Adapter
import com.apollographql.apollo.api.CustomScalarAdapters
import com.apollographql.apollo.api.json.JsonReader
import com.apollographql.apollo.api.json.JsonWriter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object LocalTimeAdapter : Adapter<LocalTime> {
    override fun fromJson(reader: JsonReader, customScalarAdapters: CustomScalarAdapters): LocalTime {
        return LocalTime.parse(reader.nextString()!!)
    }

    override fun toJson(writer: JsonWriter, customScalarAdapters: CustomScalarAdapters, value: LocalTime) {
        writer.value(value.format(DateTimeFormatter.ISO_LOCAL_TIME))
    }
}
