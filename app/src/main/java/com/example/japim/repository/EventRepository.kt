package com.example.japim.repository

import com.example.japim.remoteDataSource.EventRemoteDataSource
import com.example.japim.graphql.type.EventInputCreate
import com.example.japim.graphql.type.EventInputUpdate

class EventRepository(
    private val remoteDataSource: EventRemoteDataSource
) {

    suspend fun getEvents() =
        remoteDataSource.getEventsOrThrow()

    suspend fun getEventById(id: String) =
        remoteDataSource.fetchEventById(id)

    suspend fun createEvent(input: EventInputCreate) =
        remoteDataSource.createEvent(input)

    suspend fun updateEvent(input: EventInputUpdate) =
        remoteDataSource.updateEvent(input)

    suspend fun deleteEvent(id: String) =
        remoteDataSource.deleteEventOrThrow(id)

    suspend fun subscribeParticipant(eventId: String) =
        remoteDataSource.subscribeParticipant(eventId)

    suspend fun subscribeVolunteer(eventId: String) =
        remoteDataSource.subscribeVolunteer(eventId)

    suspend fun confirmedPresenceByVolunteer(
        eventId: String,
        participantId: String
    ) = remoteDataSource.confirmedPresenceByVolunteer(
        eventId,
        participantId
    )

    suspend fun finishEvent(eventId: String) =
        remoteDataSource.finishEvent(eventId)

    suspend fun participantPresenceDay(
        eventId: String,
        participantId: String
    ) = remoteDataSource.participantPresenceDay(
        eventId,
        participantId
    )
}