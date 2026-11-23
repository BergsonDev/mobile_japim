package com.example.japim.screen.events

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.japim.advancedFeatures.CameraQrCodePreview
import com.example.japim.advancedFeatures.DecryptionQrCodeContent
import com.example.japim.apolloClient.TokenManager
import com.example.japim.apolloClient.generateApolloClient
import com.example.japim.remoteDataSource.EventRemoteDataSource
import com.example.japim.ui.components.GenerateErrorToast
import com.example.japim.ui.components.GenerateSuccessToast
import com.example.japim.ui.events.GenerateEventValidateRequesterPresence

@Composable
fun ValidateRequesterPresenceScreen(
    tokenProvider: TokenManager,
    onClickReturn: () -> Unit,
    event_id: String
) {
    val client = remember { generateApolloClient(tokenProvider) }
    val eventRemoteDataSource = remember { EventRemoteDataSource(client) }

    val codeDecript = remember { mutableStateOf<String?>(null) }
    val userName = remember { mutableStateOf<String?>(null) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var errorList by remember { mutableStateOf<List<String>>(emptyList()) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(codeDecript.value) {
        val code = codeDecript.value
        if (code != null) {
            eventRemoteDataSource.validateRequesterPresenceCode(code)
                .onSuccess { result, _ ->
                    userName.value = "Validado: ${result.participant.name}"
                    successMessage = "Presença de ${result.participant.name} validada com sucesso!"
                    Log.d("ValidatePresence", "Presença validada para: ${result.participant.name}")
                }
                .onError { err, list ->
                    userName.value = "Erro ao validar presença"
                    errorMessage = err
                    errorList = list
                    Log.e("ValidatePresence", "Falha ao validar presença: $err")
                }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GenerateEventValidateRequesterPresence(
            onClickReturn = onClickReturn,
            cameraQrCodePreview = {
                CameraQrCodePreview(
                    modifier = Modifier.fillMaxSize(),
                ) { codeCript ->
                    // Só tenta descriptografar se não estiver processando uma mensagem/toast ativo
                    if (codeDecript.value == null && errorMessage == null && successMessage == null) {
                        val decrypted = DecryptionQrCodeContent.decrypt(codeCript)
                        if (decrypted != null) {
                            codeDecript.value = decrypted
                        } else {
                            errorMessage = "QR Code inválido ou em formato incorreto"
                            errorList = listOf("Não foi possível descriptografar o conteúdo do QR Code")
                        }
                    }
                }
            },
            codeDecrypt = codeDecript,
            user_name = userName
        )

        if (errorMessage != null) {
            GenerateErrorToast(
                title="Validação de Presença",
                message = errorMessage!!,
                errors = errorList,
                onDismiss = {
                    errorMessage = null
                    errorList = emptyList()
                    codeDecript.value = null
                    userName.value = null
                }
            )
        }

        if (successMessage != null) {
            GenerateSuccessToast(
                message = successMessage!!,
                onDismiss = {
                    successMessage = null
                    codeDecript.value = null
                    userName.value = null
                }
            )
        }
    }
}
