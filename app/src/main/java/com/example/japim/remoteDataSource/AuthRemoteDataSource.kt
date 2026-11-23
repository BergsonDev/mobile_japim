package com.example.japim.remoteDataSource

import com.apollographql.apollo.ApolloClient
import com.example.japim.graphql.AuthQuery
import com.example.japim.graphql.MyEventsQuery

class AuthRemoteDataSource(val apolloClient: ApolloClient) {

    suspend fun auth(): ApiResponse<AuthQuery.Me> {
        return executeApolloCall(
            defaultErrorMessage = "Erro ao realizar autenticação",
            successMessage = "Autenticação realizada com sucesso",
            transformData = { it.me }
        ) {
            apolloClient.query(AuthQuery()).execute()
        }
    }

    suspend fun myEvents(): ApiResponse<List<MyEventsQuery.EventsSubscribe?>> {
        return executeApolloCall(
            defaultErrorMessage = "Erro ao carregar lista de eventos",
            successMessage = "Eventos carregados com sucesso",
            transformData = { it.eventsSubscribe }
        ) {
            apolloClient.query(MyEventsQuery()).execute()
        }
    }
}
