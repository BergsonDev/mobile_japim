package com.example.japim.apolloClient

import com.apollographql.apollo.api.Adapter
import com.apollographql.apollo.api.CustomScalarAdapters
import com.apollographql.apollo.api.json.JsonReader
import com.apollographql.apollo.api.json.JsonWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object LocalDateAdapter : Adapter<LocalDate> {
    override fun fromJson(reader: JsonReader, customScalarAdapters: CustomScalarAdapters): LocalDate {
        return LocalDate.parse(reader.nextString()!!)
    }

    override fun toJson(writer: JsonWriter, customScalarAdapters: CustomScalarAdapters, value: LocalDate) {
        writer.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }
}
