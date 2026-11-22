
package com.example.japim.remoteDataSource

import com.apollographql.apollo.ApolloClient
import com.example.japim.graphql.MeQuery

class UserRemoteDataSource(
    private val apolloClient: ApolloClient
) {

    suspend fun getCurrentUser(): MeQuery.Me {
        val response = apolloClient
            .query(MeQuery())
            .execute()

        response.exception?.let { exception ->
            throw exception
        }

        if (response.hasErrors()) {
            val message = response.errors
                ?.firstOrNull()
                ?.message
                ?: "Erro ao consultar o usuário autenticado."

            throw IllegalStateException(message)
        }

        return response.data?.me
            ?: throw IllegalStateException(
                "O backend não retornou o usuário autenticado."
            )
    }
}
