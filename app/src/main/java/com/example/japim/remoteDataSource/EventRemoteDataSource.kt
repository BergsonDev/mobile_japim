package com.example.japim.remoteDataSource

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.example.japim.graphql.GetEventByIdQuery
import com.example.japim.graphql.GetEventsQuery

class EventRemoteDataSource(private val apolloClient: ApolloClient) {
    suspend fun fetchEvents(): List<GetEventsQuery.Event?>? {    return try {
        val response = apolloClient.query(GetEventsQuery()).execute()

        if (response.hasErrors()) {
            Log.e("APOLLO_TEST", "Erros GraphQL: ${response.errors}")
        } else {
            Log.d("APOLLO_TEST", "Sucesso: ${response.data}")
        }

        response.data?.events
    } catch (e: Exception) {
        // Imprime o erro exato no Logcat (ex: Conexão recusada, Timeout, Host desconhecido)
        Log.e("APOLLO_TEST", "Falha ao executar requisição GraphQL", e)
        null
    }
    }

    suspend fun fetchEventById(id : String) : GetEventByIdQuery.Event? {
        return try {
            val response = apolloClient.query(GetEventByIdQuery(id)).execute();

            if (response.hasErrors()) {
                Log.e("APOLLO_TEST", "Erros GraphQL: ${response.errors}")
            }

            response.data?.event
        } catch (e: Exception) {
            Log.e("APOLLO_TEST", "Falha ao executar requisição GraphQL", e)
            null
        }
    }
}
