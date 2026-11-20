
package com.example.japim.apolloClient

import com.apollographql.apollo.ApolloClient
import com.example.japim.interceptor.AuthorizationInterceptor
import com.example.japim.BuildConfig
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.japim.toEventInputCreate

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
