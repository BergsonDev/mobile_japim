
package com.example.japim.apolloClient

import com.apollographql.apollo.ApolloClient
import com.example.japim.interceptor.AuthorizationInterceptor
import com.example.japim.BuildConfig
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.japim.toEventInputCreate



// Cria um cliente Apollo utilizando o TokenManager informado
fun generateApolloClient(tokenProvider: TokenManager): ApolloClient {
    return ApolloClient.Builder()
        .serverUrl(BuildConfig.GRAPHQL_SERVER_URL)
        .addInterceptor(
            AuthorizationInterceptor(tokenProvider)
        )
        .build()
}

// Mantém a variável global para compatibilidade com código existente.
// O TokenManager será informado quando a aplicação configurar o cliente.
lateinit var apolloClient: ApolloClient

fun initializeApolloClient(tokenProvider: TokenManager) {
    apolloClient = generateApolloClient(tokenProvider)
}

