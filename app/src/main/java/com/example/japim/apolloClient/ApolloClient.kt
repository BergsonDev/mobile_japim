
package com.example.japim.apolloClient

import com.apollographql.apollo.ApolloClient
import com.example.japim.interceptor.AuthorizationInterceptor

val apolloClient: ApolloClient by lazy {
    ApolloClient.Builder()
        .serverUrl("http://192.168.80.1:8081/graphql")
        .addInterceptor(
            AuthorizationInterceptor(
                tokenProvider = { TokenManager.token }
            )
        )
        .build()
}
