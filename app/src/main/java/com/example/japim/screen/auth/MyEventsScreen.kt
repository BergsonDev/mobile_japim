package com.example.japim.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.japim.apolloClient.TokenManager
import com.example.japim.apolloClient.generateApolloClient
import com.example.japim.graphql.AuthQuery
import com.example.japim.graphql.MyEventsQuery
import com.example.japim.navigation.Screen
import com.example.japim.remoteDataSource.AuthRemoteDataSource
import com.example.japim.remoteDataSource.EventRemoteDataSource
import com.example.japim.ui.components.GenerateErrorToast
import com.example.japim.ui.theme.ERROR
import com.example.japim.ui.theme.FOURTH
import com.example.japim.ui.theme.MPlus2
import com.example.japim.ui.theme.PRIMARY
import com.example.japim.ui.theme.SECONDARY
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import com.example.japim.ui.components.cards.GenerateCardMyEvents
import com.example.japim.ui.components.cards.ROLE_IN_EVENT


@Composable
fun MyEventsScreen(
    onClickReturn : ()-> Unit,
    tokenProvider : TokenManager,
    onClickCertificate : (id : String)-> Unit,
    onClickDetails : (id : String)-> Unit
){
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var errorList by remember { mutableStateOf<List<String>>(emptyList()) }

    val client = remember { generateApolloClient(tokenProvider) }
    val authRemoteDataSource = remember { AuthRemoteDataSource(client) }
    val coroutineScope = rememberCoroutineScope()

    var auth by remember { mutableStateOf<AuthQuery.Me?>(null) }
    var myEvents by remember { mutableStateOf<List<MyEventsQuery.EventsSubscribe?>?>(emptyList()) }

    var isAuthLoading by remember { mutableStateOf(true) }


    LaunchedEffect(Unit) {
        try {
            coroutineScope.launch {
                auth = authRemoteDataSource.auth().getOrNull()
                myEvents = authRemoteDataSource.myEvents().getOrNull()
                isAuthLoading = false
                if(auth == null){
                    errorMessage = "O servidor retornou vazio"
                }
            }
        }catch (e : Exception){
            errorMessage = e.localizedMessage ?: "Erro desconhecido na consulta"
            errorList = listOf(e.javaClass.simpleName)

        }

    }


    if (isAuthLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PRIMARY)
        }
        return
    }

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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier.width(70.dp).padding(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FOURTH
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 10.dp,
                ),
                shape = RoundedCornerShape(5),
                onClick = {
                    onClickReturn()
                }
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Retornar",
                    tint = PRIMARY
                )
            }

        }


        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Eventos que Participo",
                color = SECONDARY,
                fontFamily = MPlus2,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )

        }

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            myEvents?.forEach { event->
                if(event != null){
                    GenerateCardMyEvents(
                        event.name,
                        event.short_description,
                        event.speakers[0]?.name.orEmpty(),
                        event.total_participants,
                        event.total_volunteers,
                        event.is_payment,
                        ROLE_IN_EVENT.PARTICIPANT,
                        {
                            onClickDetails(event.id)
                        },
                        {
                            onClickCertificate(event.id)
                        }
                    )
                }
            }
        }
    }
}