package com.example.japim.screen.events

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.example.japim.advancedFeatures.CameraQrCodePreview
import com.example.japim.advancedFeatures.DecryptionQrCodeContent
import com.example.japim.apolloClient.TokenManager
import com.example.japim.apolloClient.generateApolloClient
import com.example.japim.remoteDataSource.EventRemoteDataSource
import com.example.japim.ui.events.GenerateEventValidateRequesterPresence

@Composable
fun ValidateRequesterPresenceScreen(
    tokenProvider: TokenManager,
    onClickReturn : ()->Unit,
    event_id : String
){

    val client = remember { generateApolloClient(tokenProvider) }
    val eventRemoteDataSource = remember { EventRemoteDataSource(client) }


    val codeDecript = remember { mutableStateOf<String?>(null) }
    val userName = remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(codeDecript.value) {
        val code = codeDecript.value
        if (code != null) {
            coroutineScope.launch {
                val result = eventRemoteDataSource.validateRequesterPresenceCode(code)
                if (result != null) {
                    userName.value = "Validado: ${result.participant.name}"
                    Log.d("ValidatePresence", "Presença validada para: ${result.participant.name}")
                } else {
                    userName.value = "Erro ao validar presença"
                    Log.e("ValidatePresence", "Falha ao validar presença")
                }
            }
        }
    }

    GenerateEventValidateRequesterPresence(
        onClickReturn = onClickReturn,

        cameraQrCodePreview = {
            CameraQrCodePreview(
                modifier = Modifier.fillMaxSize(),
            ){
                codeCript->
                val decrypted = DecryptionQrCodeContent.decrypt(codeCript)
                if (decrypted != null) {
                    codeDecript.value = decrypted
                }
            }
        },
        codeDecrypt=codeDecript,
        user_name = userName
    )
}
