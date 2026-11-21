package com.example.japim.screen.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ShapeDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.japim.advancedFeatures.EncriptionQrCodeContent
import com.example.japim.advancedFeatures.generateQrCode
import com.example.japim.apolloClient.TokenManager
import com.example.japim.apolloClient.generateApolloClient
import com.example.japim.graphql.AuthQuery
import com.example.japim.graphql.GetEventByIdQuery
import com.example.japim.graphql.RequesterPresenceCodeMutation
import com.example.japim.graphql.type.RequesterPresence
import com.example.japim.navigation.Screen
import com.example.japim.remoteDataSource.AuthRemoteDataSource
import com.example.japim.remoteDataSource.EventRemoteDataSource
import com.example.japim.ui.components.GenerateHeader
import com.example.japim.ui.events.GenerateEventRequesterPresence
import com.example.japim.ui.theme.ERROR
import com.example.japim.ui.theme.FIFTH
import com.example.japim.ui.theme.FOURTH
import com.example.japim.ui.theme.MPlus2
import com.example.japim.ui.theme.PRIMARY
import com.example.japim.ui.theme.SECONDARY
import com.example.japim.ui.theme.THIRD
import kotlinx.coroutines.launch


@Preview(showBackground = true)
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

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            auth = authRemoteDataSource.auth()
            isAuthLoading = false
        }
        coroutineScope.launch {
            requesterPresence = eventRemoteDataSource.requesterPresenceCode(event_id)
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
                text = "erro na consulta de eventos para gerar o código de validação",
                color = ERROR
            )
        }
        return
    }


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
}
