package com.example.japim.screen.events

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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

    if (codeDecript.value != null){
        LaunchedEffect(Unit) {
            eventRemoteDataSource.validateRequesterPresenceCode(codeDecript.value.toString())
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
