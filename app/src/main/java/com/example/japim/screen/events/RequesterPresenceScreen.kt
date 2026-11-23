package com.example.japim.screen.events

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.japim.advancedFeatures.generateQrCode
import com.example.japim.apolloClient.TokenManager
import com.example.japim.apolloClient.generateApolloClient
import com.example.japim.graphql.AuthQuery
import com.example.japim.graphql.RequesterPresenceCodeMutation
import com.example.japim.remoteDataSource.AuthRemoteDataSource
import com.example.japim.remoteDataSource.EventRemoteDataSource
import com.example.japim.ui.components.GenerateErrorToast
import com.example.japim.ui.components.GenerateSuccessToast
import com.example.japim.ui.events.GenerateEventRequesterPresence
import com.example.japim.ui.theme.ERROR
import com.example.japim.ui.theme.PRIMARY
import kotlinx.coroutines.launch

@Composable
fun RequesterPresenceScreen(
    tokenProvider: TokenManager,
    onClickReturn : ()->Unit,
    event_id : String
){
    val client = remember { generateApolloClient(tokenProvider) }
    val authRemoteDataSource = remember { AuthRemoteDataSource(client) }
    val eventRemoteDataSource = remember { EventRemoteDataSource(client) }

    val coroutineScope = rememberCoroutineScope()

    var auth by remember { mutableStateOf<AuthQuery.Me?>(null) }
    var isAuthLoading by remember { mutableStateOf(true) }
    var requesterPresence by remember { mutableStateOf<RequesterPresenceCodeMutation.RequesterPresenceCode?>(null) }
    var isEventLoading by remember { mutableStateOf(true) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var errorList by remember { mutableStateOf<List<String>>(emptyList()) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            authRemoteDataSource.auth()
                .onSuccess { me, _ -> auth = me }
                .onError { err, list ->
                    errorMessage = err
                    errorList = list
                }
            isAuthLoading = false
        }
        coroutineScope.launch {
            eventRemoteDataSource.requesterPresenceCode(event_id)
                .onSuccess { code, msg ->
                    requesterPresence = code
                    successMessage = msg
                }
                .onError { err, list ->
                    errorMessage = err
                    errorList = list
                }
            isEventLoading = false
        }
    }

    if (isAuthLoading || isEventLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PRIMARY)
        }
        return
    }

    val currentUser = auth
    if (currentUser == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "erro na autenticação para detalhes de eventos",
                color = ERROR
            )
        }
        return
    }

    if (requesterPresence == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = errorMessage ?: "erro na consulta de eventos para gerar o código de validação",
                color = ERROR
            )
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GenerateEventRequesterPresence(
            {
                generateQrCode(requesterPresence!!.code)?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "QR Code do Token de Acesso",
                        modifier = Modifier.size(200.dp)
                    )
                }
            },
            onClickReturn,
            requesterPresence!!.code
        )

        if (errorMessage != null) {
            GenerateErrorToast(
                message = errorMessage!!,
                errors = errorList,
                onDismiss = {
                    errorMessage = null
                    errorList = emptyList()
                }
            )
        }

        if (successMessage != null) {
            GenerateSuccessToast(
                message = successMessage!!,
                onDismiss = {
                    successMessage = null
                }
            )
        }
    }
}
