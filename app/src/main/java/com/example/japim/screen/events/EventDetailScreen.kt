package com.example.japim.screen.events

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.japim.graphql.GetEventByIdQuery
import com.example.japim.remoteDataSource.EventRemoteDataSource
import com.example.japim.remoteDataSource.AuthRemoteDataSource
import com.example.japim.ui.events.GenerateEventDetails
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import com.example.japim.graphql.AuthQuery
import com.example.japim.ui.theme.PRIMARY
import com.example.japim.ui.theme.ERROR
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.japim.apolloClient.TokenManager
import com.example.japim.apolloClient.generateApolloClient
import com.example.japim.ui.theme.FOURTH
import com.example.japim.ui.theme.MPlus2
import com.example.japim.ui.theme.SECONDARY


@Composable
fun EventDetailScreen(
    tokenProvider: TokenManager,
    event_id : String,
    onNavigateRequestPresence : (String) -> Unit,
    onNavigateValidateRequestPresence : (String)->Unit,
    onClickRedirectListPresences:(String)->Unit,
    onClickReturn : ()-> Unit

){
    val client = remember { generateApolloClient(tokenProvider) }
    val authRemoteDataSource = remember { AuthRemoteDataSource(client) }
    val eventRemoteDataSource = remember { EventRemoteDataSource(client) }
    val coroutineScope = rememberCoroutineScope()

    var auth by remember { mutableStateOf<AuthQuery.Me?>(null) }
    var isAuthLoading by remember { mutableStateOf(true) }
    var event by remember { mutableStateOf<GetEventByIdQuery.Event?>(null) }
    var isEventLoading by remember { mutableStateOf(true) }

    suspend fun fetchEventData() {
        event = eventRemoteDataSource.fetchEventById(event_id)
        isEventLoading = false
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            auth = authRemoteDataSource.auth()
            isAuthLoading = false
        }
        coroutineScope.launch {
            fetchEventData()
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

    event?.let {
        val isVolunteer = it.volunteers?.any { volunteer -> volunteer?.id == currentUser.id } == true

        Column(
            modifier = Modifier.fillMaxSize().background(PRIMARY)
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
                    text = "Detalhes do Evento",
                    color = SECONDARY,
                    fontFamily = MPlus2,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )

            }

            GenerateEventDetails(
                id=it.id,
                title = it.name,
                description = it.description,
                name_speakers = it.speakers.map { speaker -> speaker?.name ?: "" },
                total_participants = it.total_participants,
                max_participants = it.max_participants,
                total_volunteers = it.total_volunteers,
                max_volunteers = it.max_volunteers,
                workload = it.workload,
                value = it.value,
                in_event = it.in_event,
                is_volunteer = isVolunteer,
                status = it.status_event.initialism,
                auth_id = currentUser.id,
                auth_role = currentUser.role.initialism,
                onClickParticipant = {
                    coroutineScope.launch {
                        val result = eventRemoteDataSource.subscribeParticipant(event_id)
                        if (result != null) {
                            Log.d("Subscribe", "Inscrição realizada com sucesso: ${result.id}")
                            fetchEventData()
                        } else {
                            Log.e("Subscribe", "Falha ao se inscrever")
                        }
                    }
                },
                onClickVolunteer = {
                    coroutineScope.launch {
                        val result = eventRemoteDataSource.subscribeVolunteer(event_id)
                        if (result != null) {
                            Log.d("Subscribe", "Inscrição realizada como voluntário com sucesso: ${result.id}")
                            fetchEventData()
                        } else {
                            Log.e("Subscribe", "Falha ao se inscrever")
                        }
                    }
                },
                onClickPresence = {
                    onNavigateRequestPresence(event_id)
                },
                onClickFinishEvent = {
                    coroutineScope.launch {
                        val result = eventRemoteDataSource.finishEvent(it.id)
                        if (result != null) {
                            Log.d("Presence", "Evento Finalizado com sucesso: ${result.id}")
                            fetchEventData()
                        } else {
                            Log.e("Presence", "Falha ao Finalizar o evento")
                        }
                    }
                },

                onClickValidatePresence = {
                    onNavigateValidateRequestPresence(event_id)
                },
                {
                    onClickRedirectListPresences(event_id)
                }

            )

        }
    }
}
