
package com.example.japim.apolloClient

import com.apollographql.apollo.ApolloClient
import com.example.japim.interceptor.AuthorizationInterceptor
import com.example.japim.BuildConfig

val apolloClient: ApolloClient by lazy {
    ApolloClient.Builder()
        .serverUrl(BuildConfig.GRAPHQL_SERVER_URL)
        .addInterceptor(
            AuthorizationInterceptor(
                tokenProvider = { TokenManager.token }
            )
        )
        .build()
}
