package com.example.japim.remoteDataSource

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.example.japim.graphql.DeleteEventMutation
import com.example.japim.graphql.GetEventByIdQuery
import com.example.japim.graphql.GetEventsQuery
import com.example.japim.graphql.GetPresenceParticipantQuery
import com.example.japim.graphql.RequesterPresenceCodeMutation
import com.example.japim.graphql.SubscribeParticipantMutation
import com.example.japim.graphql.SubscribeVolunteerMutation
import com.example.japim.graphql.CreateEventMutation
import com.example.japim.graphql.UpdateEventMutation
import com.example.japim.graphql.type.EventInputCreate
import com.example.japim.graphql.type.EventInputUpdate
import com.apollographql.apollo.api.Optional
import com.example.japim.graphql.ConfirmedPresenceByVolunteerMutation
import com.example.japim.graphql.FinishEventMutation
import com.example.japim.graphql.ValidateRequesterPresenceCodeMutation

class EventRemoteDataSource(private val apolloClient: ApolloClient) {
    suspend fun fetchEvents(): List<GetEventsQuery.Event?>? {
        return try {
            val response = apolloClient.query(GetEventsQuery()).execute()

            if (response.hasErrors()) {
                Log.e("APOLLO_TEST", "Erros GraphQL: ${response.errors}")
            } else {
                Log.d("APOLLO_TEST", "Sucesso: ${response.data}")
            }

            response.data?.events
        } catch (e: Exception) {
            Log.e("APOLLO_TEST", "Falha ao executar requisição GraphQL", e)
            null
        }
    }

    suspend fun fetchEventById(id: String): GetEventByIdQuery.Event? {
        return try {
            val response = apolloClient.query(GetEventByIdQuery(id)).execute()

            if (response.hasErrors()) {
                Log.e("APOLLO_TEST", "Erros GraphQL: ${response.errors}")
            }

            response.data?.event
        } catch (e: Exception) {
            Log.e("APOLLO_TEST", "Falha ao executar requisição GraphQL", e)
            null
        }
    }

    suspend fun subscribeParticipant(event_id: String): SubscribeParticipantMutation.SubscribeParticipant? {
        return try {
            val response = apolloClient.mutation(SubscribeParticipantMutation(event_id)).execute()

            if (response.hasErrors()) {
                Log.e("APOLLO_TEST", "Erros GraphQL: ${response.errors}")
            }

            response.data?.subscribeParticipant
        } catch (e: Exception) {
            Log.e("APOLLO_TEST", "Falha ao executar requisição GraphQL", e)
            null
        }
    }

    suspend fun subscribeVolunteer(event_id: String): SubscribeVolunteerMutation.SubscribeVolunteer? {
        return try {
            val response = apolloClient.mutation(SubscribeVolunteerMutation(event_id)).execute()

            if (response.hasErrors()) {
                Log.e("APOLLO_TEST", "Erros GraphQL: ${response.errors}")
            }

            response.data?.subscribeVolunteer
        } catch (e: Exception) {
            Log.e("APOLLO_TEST", "Falha ao executar requisição GraphQL", e)
            null
        }
    }

    suspend fun deleteEvent(event_id: String): DeleteEventMutation.DeleteEvent? {
        return try {
            val response = apolloClient.mutation(DeleteEventMutation(event_id)).execute()

            if (response.hasErrors()) {
                Log.e("APOLLO_TEST", "Erros GraphQL: ${response.errors}")
            }

            response.data?.deleteEvent
        } catch (e: Exception) {
            Log.e("APOLLO_TEST", "Falha ao executar requisição GraphQL", e)
            null
        }
    }

    suspend fun createEvent(
        input: EventInputCreate
    ): CreateEventMutation.CreateEvent? {
        return try {
            val response = apolloClient
                .mutation(
                    CreateEventMutation(
                        input = Optional.Present(input)
                    )
                )
                .execute()

            if (response.hasErrors()) {
                Log.e(
                    "APOLLO_EVENT",
                    "Erro ao criar evento: ${response.errors}"
                )
                null
            } else {
                Log.d(
                    "APOLLO_EVENT",
                    "Evento criado: ${response.data?.createEvent?.id}"
                )
                response.data?.createEvent
            }
        } catch (e: Exception) {
            Log.e(
                "APOLLO_EVENT",
                "Falha ao criar evento",
                e
            )
            null
        }
    }

    suspend fun updateEvent(
        input: EventInputUpdate
    ): UpdateEventMutation.UpdateEvent? {
        return try {
            val response = apolloClient
                .mutation(
                    UpdateEventMutation(
                        input = Optional.Present(input)
                    )
                )
                .execute()

            if (response.hasErrors()) {
                Log.e(
                    "APOLLO_EVENT",
                    "Erro ao atualizar evento: ${response.errors}"
                )
                null
            } else {
                Log.d(
                    "APOLLO_EVENT",
                    "Evento atualizado com sucesso"
                )
                response.data?.updateEvent
            }
        } catch (e: Exception) {
            Log.e(
                "APOLLO_EVENT",
                "Falha ao atualizar evento",
                e
            )
            null
        }
    }

    suspend fun confirmedPresenceByVolunteer(
        id: String,
        participantId: String
    ): ConfirmedPresenceByVolunteerMutation.ConfirmedPresenceByVolunteer? {
        return try {
            val response = apolloClient.mutation(
                ConfirmedPresenceByVolunteerMutation(
                    id = id,
                    participant_id = participantId
                )
            ).execute()

            if (response.hasErrors()) {
                Log.e(
                    "APOLLO_PRESENCE",
                    "Erro ao confirmar presença: ${response.errors}"
                )
                null
            } else {
                Log.d(
                    "APOLLO_PRESENCE",
                    "Presença confirmada com sucesso"
                )
                response.data?.confirmedPresenceByVolunteer
            }
        } catch (e: Exception) {
            Log.e(
                "APOLLO_PRESENCE",
                "Falha ao confirmar presença",
                e
            )
            null
        }
    }

    suspend fun finishEvent(
        id: String
    ): FinishEventMutation.FinishEvent? {
        return try {
            val response = apolloClient.mutation(
                FinishEventMutation(id = id)
            ).execute()

            if (response.hasErrors()) {
                Log.e(
                    "APOLLO_EVENT",
                    "Erro ao finalizar evento: ${response.errors}"
                )
                null
            } else {
                Log.d(
                    "APOLLO_EVENT",
                    "Evento finalizado com sucesso"
                )
                response.data?.finishEvent
            }
        } catch (e: Exception) {
            Log.e(
                "APOLLO_EVENT",
                "Falha ao finalizar evento",
                e
            )
            null
        }
    }

    suspend fun participantPresenceDay(
        eventId: String,
        participantId: String
    ): GetPresenceParticipantQuery.ParticipantPresenceDay? {
        return try {
            val response = apolloClient.query(
                GetPresenceParticipantQuery(
                    id = eventId,
                    participant_id = participantId
                )
            ).execute()

            if (response.hasErrors()) {
                Log.e(
                    "APOLLO_PRESENCE",
                    "Erro ao consultar presença: ${response.errors}"
                )
                null
            } else {
                Log.d(
                    "APOLLO_PRESENCE",
                    "Consulta de presença realizada com sucesso"
                )
                response.data?.participantPresenceDay
            }
        } catch (e: Exception) {
            Log.e(
                "APOLLO_PRESENCE",
                "Falha ao consultar presença",
                e
            )
            null
        }
    }

    suspend fun requesterPresenceCode(id: String): RequesterPresenceCodeMutation.RequesterPresenceCode? {
        return try {
            val response = apolloClient.mutation(RequesterPresenceCodeMutation(id)).execute()
            if (response.hasErrors()) {
                Log.e("APOLLO_TEST", "Erros GraphQL: ${response.errors}")
            }
            response.data?.requesterPresenceCode
        } catch (e: Exception) {
            Log.e("APOLLO_TEST", "Falha ao executar requisição GraphQL", e)
            null
        }
    }

    suspend fun validateRequesterPresenceCode(code: String): ValidateRequesterPresenceCodeMutation.ValidateRequesterPresenceCode? {
        return try {
            val response = apolloClient.mutation(ValidateRequesterPresenceCodeMutation(code)).execute()
            if (response.hasErrors()) {
                Log.e("APOLLO_TEST", "Erros GraphQL: ${response.errors}")
            }
            response.data?.validateRequesterPresenceCode
        } catch (e: Exception) {
            Log.e("APOLLO_TEST", "Falha ao executar requisição GraphQL", e)
            null
        }
    }
}
