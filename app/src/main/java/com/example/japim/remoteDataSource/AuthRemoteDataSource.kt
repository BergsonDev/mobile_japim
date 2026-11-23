package com.example.japim.remoteDataSource

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.example.japim.graphql.AuthQuery
import com.example.japim.graphql.MyEventsQuery
import com.example.japim.graphql.SubscribeParticipantMutation

class AuthRemoteDataSource(val apolloClient: ApolloClient) {

    suspend fun auth() : AuthQuery.Me?{
        return try {
            val response = apolloClient.query(AuthQuery()).execute();

            if (response.hasErrors()) {
                Log.e("APOLLO_TEST", "Erros GraphQL: ${response.errors}")
            }

            response.data?.me
        } catch (e: Exception) {
            Log.e("APOLLO_TEST", "Falha ao executar requisição GraphQL", e)
            null
        }
    }

    suspend fun myEvents() : List<MyEventsQuery.EventsSubscribe?>? {
        return try {
            val response = apolloClient.query(MyEventsQuery()).execute();

            if (response.hasErrors()) {
                Log.e("APOLLO_TEST", "Erros GraphQL: ${response.errors}")
            }

            response.data?.eventsSubscribe
        } catch (e: Exception) {
            Log.e("APOLLO_TEST", "Falha ao executar requisição GraphQL", e)
            null
        }
    }
}