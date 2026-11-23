package com.example.japim.remoteDataSource

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Optional
import com.example.japim.graphql.ConfirmedPresenceByCoordinatorMutation
import com.example.japim.graphql.ConfirmedPresenceByVolunteerMutation
import com.example.japim.graphql.CreateEventMutation
import com.example.japim.graphql.DeleteEventMutation
import com.example.japim.graphql.FinishEventMutation
import com.example.japim.graphql.GetEventByIdQuery
import com.example.japim.graphql.GetEventsQuery
import com.example.japim.graphql.GetParticipantPresenceInDayQuery
import com.example.japim.graphql.RemoveConfirmedPresenceByCoordinatorMutation
import com.example.japim.graphql.RemoveConfirmedPresenceByVolunteerMutation
import com.example.japim.graphql.RequesterPresenceCodeMutation
import com.example.japim.graphql.SubscribeParticipantMutation
import com.example.japim.graphql.SubscribeVolunteerMutation
import com.example.japim.graphql.UpdateEventMutation
import com.example.japim.graphql.ValidateRequesterPresenceCodeMutation
import com.example.japim.graphql.type.EventInputCreate
import com.example.japim.graphql.type.EventInputUpdate
import java.time.LocalDateTime


class EventRemoteDataSource(private val apolloClient: ApolloClient) {

    suspend fun fetchEvents(): ApiResponse<List<GetEventsQuery.Event?>> {
        return executeApolloCall(
            defaultErrorMessage = "Erro ao buscar a lista de eventos",
            successMessage = "Eventos carregados com sucesso",
            transformData = { it.events }
        ) {
            apolloClient.query(GetEventsQuery()).execute()
        }
    }

    suspend fun fetchEventById(id: String): ApiResponse<GetEventByIdQuery.Event> {
        return executeApolloCall(
            defaultErrorMessage = "Erro ao buscar os detalhes do evento",
            successMessage = "Detalhes do evento carregados",
            transformData = { it.event }
        ) {
            apolloClient.query(GetEventByIdQuery(id)).execute()
        }
    }

    suspend fun subscribeParticipant(event_id: String): ApiResponse<SubscribeParticipantMutation.SubscribeParticipant> {
        return executeApolloCall(
            defaultErrorMessage = "Erro ao realizar inscrição como participante",
            successMessage = "Inscrição realizada com sucesso!",
            transformData = { it.subscribeParticipant }
        ) {
            apolloClient.mutation(SubscribeParticipantMutation(event_id)).execute()
        }
    }

    suspend fun subscribeVolunteer(event_id: String): ApiResponse<SubscribeVolunteerMutation.SubscribeVolunteer> {
        return executeApolloCall(
            defaultErrorMessage = "Erro ao realizar inscrição como voluntário",
            successMessage = "Inscrição como voluntário realizada com sucesso!",
            transformData = { it.subscribeVolunteer }
        ) {
            apolloClient.mutation(SubscribeVolunteerMutation(event_id)).execute()
        }
    }

    suspend fun deleteEvent(event_id: String): ApiResponse<DeleteEventMutation.DeleteEvent> {
        return executeApolloCall(
            defaultErrorMessage = "Erro ao deletar evento",
            successMessage = "Evento removido com sucesso!",
            transformData = { it.deleteEvent }
        ) {
            apolloClient.mutation(DeleteEventMutation(event_id)).execute()
        }
    }

    suspend fun deleteEventOrThrow(
        eventId: String
    ): DeleteEventMutation.DeleteEvent {

        val response = apolloClient
            .mutation(DeleteEventMutation(eventId))
            .execute()

        validateResponse(response)

        return response.data?.deleteEvent
            ?: throw IllegalStateException(
                "Não foi possível excluir o evento."
            )
    }

    suspend fun createEvent(input: EventInputCreate): ApiResponse<CreateEventMutation.CreateEvent> {
        return executeApolloCall(
            defaultErrorMessage = "Erro ao criar evento",
            successMessage = "Evento criado com sucesso!",
            transformData = { it.createEvent }
        ) {
            apolloClient.mutation(CreateEventMutation(input = Optional.Present(input))).execute()
        }
    }

    suspend fun updateEvent(input: EventInputUpdate): ApiResponse<UpdateEventMutation.UpdateEvent> {
        return executeApolloCall(
            defaultErrorMessage = "Erro ao atualizar evento",
            successMessage = "Evento atualizado com sucesso!",
            transformData = { it.updateEvent }
        ) {
            apolloClient.mutation(UpdateEventMutation(input = Optional.Present(input))).execute()
        }
    }

    suspend fun confirmedPresenceByVolunteer(
        id: String,
        participantId: String
    ): ApiResponse<ConfirmedPresenceByVolunteerMutation.ConfirmedPresenceByVolunteer> {
        return executeApolloCall(
            defaultErrorMessage = "Erro ao confirmar presença pelo voluntário",
            successMessage = "Presença confirmada com sucesso!",
            transformData = { it.confirmedPresenceByVolunteer }
        ) {
            apolloClient.mutation(
                ConfirmedPresenceByVolunteerMutation(
                    id = id,
                    participant_id = participantId
                )
            ).execute()
        }
    }

    suspend fun finishEvent(id: String): ApiResponse<FinishEventMutation.FinishEvent> {
        return executeApolloCall(
            defaultErrorMessage = "Erro ao finalizar evento",
            successMessage = "Evento finalizado com sucesso!",
            transformData = { it.finishEvent }
        ) {
            apolloClient.mutation(FinishEventMutation(id = id)).execute()
        }
    }

    suspend fun participantPresenceInDay(
        eventId: String,
        memberId: String,
        datePresence: LocalDateTime
    ): ApiResponse<GetParticipantPresenceInDayQuery.ParticipantPresenceInDay> {
        return executeApolloCall(
            defaultErrorMessage = "Erro ao consultar presença do participante",
            successMessage = "Presença consultada com sucesso!",
            transformData = { it.participantPresenceInDay }
        ) {
            apolloClient.query(
                GetParticipantPresenceInDayQuery(
                    id = eventId,
                    participant_id = memberId,
                    date_presence = datePresence
                )
            ).execute()
        }
    }

    private fun validateResponse(response: ApolloResponse<*>) {

        response.exception?.let {
            throw it
        }

        if (response.hasErrors()) {
            throw IllegalStateException(
                response.errors?.firstOrNull()?.message
                    ?: "Erro ao executar operação GraphQL."
            )
        }

        if (response.data == null) {
            throw IllegalStateException(
                "Nenhum dado retornado pelo servidor."
            )
        }
    }

    suspend fun getEventsOrThrow(): List<GetEventsQuery.Event> {

        val response = apolloClient
            .query(GetEventsQuery())
            .execute()

        validateResponse(response)

        return response.data?.events
            ?.filterNotNull()
            ?: throw IllegalStateException(
                "Não foi possível carregar os eventos."
            )
    }

    suspend fun requesterPresenceCode(id: String): ApiResponse<RequesterPresenceCodeMutation.RequesterPresenceCode> {
        return executeApolloCall(
            defaultErrorMessage = "Erro ao solicitar código de presença",
            successMessage = "Código de presença gerado com sucesso!",
            transformData = { it.requesterPresenceCode }
        ) {
            apolloClient.mutation(RequesterPresenceCodeMutation(id)).execute()
        }
    }

    suspend fun validateRequesterPresenceCode(code: String): ApiResponse<ValidateRequesterPresenceCodeMutation.ValidateRequesterPresenceCode> {
        return executeApolloCall(
            defaultErrorMessage = "Erro ao validar código de presença",
            successMessage = "Código de presença validado com sucesso!",
            transformData = { it.validateRequesterPresenceCode }
        ) {
            apolloClient.mutation(ValidateRequesterPresenceCodeMutation(code)).execute()
        }
    }

    suspend fun confirmedPresenceByCoordinator(
        id: String,
        volunteerId: String
    ): ApiResponse<ConfirmedPresenceByCoordinatorMutation.ConfirmedPresenceByCoordinator> {
        return executeApolloCall(
            defaultErrorMessage = "Erro ao confirmar presença pelo coordenador",
            successMessage = "Presença confirmada com sucesso!",
            transformData = { it.confirmedPresenceByCoordinator }
        ) {
            apolloClient.mutation(
                ConfirmedPresenceByCoordinatorMutation(
                    event_id = id,
                    volunteer_id = volunteerId
                )
            ).execute()
        }
    }

    suspend fun removeConfirmedPresenceByVolunteer(
        id: String,
        participantId: String
    ): ApiResponse<RemoveConfirmedPresenceByVolunteerMutation.RemoveConfirmedPresenceByVolunteer> {
        return executeApolloCall(
            defaultErrorMessage = "Erro ao remover confirmação de presença",
            successMessage = "Confirmação de presença removida!",
            transformData = { it.removeConfirmedPresenceByVolunteer }
        ) {
            apolloClient.mutation(
                RemoveConfirmedPresenceByVolunteerMutation(
                    event_id = id,
                    participant_id = participantId
                )
            ).execute()
        }
    }

    suspend fun removeConfirmedPresenceByCoordinator(
        id: String,
        volunteerId: String
    ): ApiResponse<RemoveConfirmedPresenceByCoordinatorMutation.RemoveConfirmedPresenceByCoordinator> {
        return executeApolloCall(
            defaultErrorMessage = "Erro ao remover confirmação de presença pelo coordenador",
            successMessage = "Confirmação de presença removida!",
            transformData = { it.removeConfirmedPresenceByCoordinator }
        ) {
            apolloClient.mutation(
                RemoveConfirmedPresenceByCoordinatorMutation(
                    event_id = id,
                    volunteer_id = volunteerId
                )
            ).execute()
        }
    }

}
