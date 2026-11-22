package com.example.japim.screen.events

import androidx.compose.animation.Crossfade
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.japim.apolloClient.TokenManager
import com.example.japim.apolloClient.generateApolloClient
import com.example.japim.graphql.AuthQuery
import com.example.japim.graphql.GetEventByIdQuery
import com.example.japim.navigation.Screen
import com.example.japim.remoteDataSource.AuthRemoteDataSource
import com.example.japim.remoteDataSource.EventRemoteDataSource
import com.example.japim.ui.components.GenerateErrorToast
import com.example.japim.ui.components.tables.GenerateListPresence
import java.time.LocalDateTime
import com.example.japim.ui.components.tables.IMembersEvent
import com.example.japim.ui.theme.FOURTH
import com.example.japim.ui.theme.MPlus2
import com.example.japim.ui.theme.PRIMARY
import com.example.japim.ui.theme.SECONDARY
import kotlinx.coroutines.launch

enum class EventTab {
    PARTICIPANTS,
    VOLUNTEERS
}

private suspend fun confirmPresenceByVolunteer(
    eventRemoteDataSource: EventRemoteDataSource,
    eventId: String,
    participantId: String
): Boolean {
    return try {
        eventRemoteDataSource.confirmedPresenceByVolunteer(eventId, participantId) != null
    } catch (_: Exception) {
        false
    }
}

private suspend fun removeConfirmPresenceByVolunteer(
    eventRemoteDataSource: EventRemoteDataSource,
    eventId: String,
    participantId: String
): Boolean {
    return try {
        eventRemoteDataSource.removeConfirmedPresenceByVolunteer(eventId, participantId) != null
    } catch (_: Exception) {
        false
    }
}

private suspend fun confirmPresenceByCoordinator(
    eventRemoteDataSource: EventRemoteDataSource,
    eventId: String,
    volunteerID: String
): Boolean {
    return try {
        eventRemoteDataSource.confirmedPresenceByCoordinator(eventId, volunteerID) != null
    } catch (_: Exception) {
        false
    }
}

private suspend fun removeConfirmPresenceByCoordinator(
    eventRemoteDataSource: EventRemoteDataSource,
    eventId: String,
    volunteerID: String
): Boolean {
    return try {
        eventRemoteDataSource.removeConfirmedPresenceByCoordinator(eventId, volunteerID) != null
    } catch (_: Exception) {
        false
    }
}

@Composable
fun EventListPresenceScreen(
    event_id : String,
    tokenProvider : TokenManager,
    onClickReturn : ()->Unit,
){
    val navController = rememberNavController()
    val client = remember { generateApolloClient(tokenProvider) }

    val authRemoteDataSource = remember { AuthRemoteDataSource(client) }
    val eventRemoteDataSource = remember { EventRemoteDataSource(client) }
    val coroutineScope = rememberCoroutineScope()

    var auth by remember { mutableStateOf<AuthQuery.Me?>(null) }
    var isAuthLoading by remember { mutableStateOf(true) }
    var event by remember { mutableStateOf<GetEventByIdQuery.Event?>(null) }
    var isEventLoading by remember { mutableStateOf(true) }

    var selectedTab by remember { mutableStateOf(EventTab.PARTICIPANTS) }

    var presenceMap by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }

    var isError by remember { mutableStateOf(false) }
    var msgError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            auth = authRemoteDataSource.auth()
            isAuthLoading = false
        }
        coroutineScope.launch {
            val fetchedEvent = eventRemoteDataSource.fetchEventById(event_id)
            event = fetchedEvent

            // Check presence for all participants and volunteers
            val newPresenceMap = mutableMapOf<String, Boolean>()
            fetchedEvent?.participants?.forEach { participant ->
                participant?.let {
                    val presence = eventRemoteDataSource.participantPresenceInDay(event_id, it.id, LocalDateTime.now())
                    if (presence != null) {
                        newPresenceMap[it.id] = true
                    }
                }
            }
            fetchedEvent?.volunteers?.forEach { volunteer ->
                volunteer?.let {
                    val presence = eventRemoteDataSource.participantPresenceInDay(event_id, it.id, LocalDateTime.now())
                    if (presence != null) {
                        newPresenceMap[it.id] = true
                    }
                }
            }
            presenceMap = newPresenceMap
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
            GenerateErrorToast(
                true,
                "Autenticação Falhada",
                "Erro ao verificar se você está autenticado, redirecione para o login",
                listOf(),
                {
                    tokenProvider.limparToken()
                    navController.navigate(Screen.Login)
                }
            )
        }
        return
    }

    val listParticipants: List<IMembersEvent> = event?.participants?.mapNotNull { participant ->
        participant?.let {
            object : IMembersEvent {
                override val id: String = it.id
                override val name: String = it.name
                override val role: String = it.role.initialism
                override val isChecked: Boolean = presenceMap[it.id] ?: false
            }
        }
    } ?: emptyList()

    val listVolunteers: List<IMembersEvent> = event?.volunteers?.mapNotNull { volunteer ->
        volunteer?.let {
            object : IMembersEvent {
                override val id: String = it.id
                override val name: String = it.name
                override val role: String = it.role.initialism
                override val isChecked: Boolean = presenceMap[it.id] ?: false
            }
        }
    } ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PRIMARY)
            .padding(8.dp)
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
                text = "Chamadas",
                color = SECONDARY,
                fontFamily = MPlus2,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = { selectedTab = EventTab.PARTICIPANTS },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == EventTab.PARTICIPANTS) SECONDARY else PRIMARY
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Participantes (${listParticipants.size})",  color=if (selectedTab == EventTab.VOLUNTEERS) SECONDARY else PRIMARY)
            }

            Button(
                onClick = { selectedTab = EventTab.VOLUNTEERS },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == EventTab.VOLUNTEERS) SECONDARY else PRIMARY
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Voluntários (${listVolunteers.size})", color= if (selectedTab == EventTab.VOLUNTEERS) PRIMARY else SECONDARY)
            }
        }

        Crossfade(
            targetState = selectedTab,
            label = "TableSwitchAnimation",
            modifier = Modifier.fillMaxSize()
        ) { tab ->
            val members = when (tab) {
                EventTab.PARTICIPANTS -> listParticipants
                EventTab.VOLUNTEERS -> listVolunteers
            }
            GenerateListPresence(
                event_id = event_id,
                members = members,
                onCheckedPresence = { eventId, memberId ->
                    val success = when (tab) {
                        EventTab.PARTICIPANTS -> confirmPresenceByVolunteer(eventRemoteDataSource, eventId, memberId)
                        EventTab.VOLUNTEERS -> confirmPresenceByCoordinator(eventRemoteDataSource, eventId, memberId)
                    }
                    if (!success) {
                        isError = true
                        msgError = "Erro ao fazer a confirmação para o usuário"
                    }
                    success
                },
                onCheckedRemovePresence = { eventId, memberId ->
                    val success = when (tab) {
                        EventTab.PARTICIPANTS -> removeConfirmPresenceByVolunteer(eventRemoteDataSource, eventId, memberId)
                        EventTab.VOLUNTEERS -> removeConfirmPresenceByCoordinator(eventRemoteDataSource, eventId, memberId)
                    }
                    if (!success) {
                        isError = true
                        msgError = "Erro ao remover a confirmação da presença para o usuário"
                    }
                    success
                }
            )
        }
    }

    if (isError && msgError != null) {
        GenerateErrorToast(
            true,
            "Confirmação de Presença",
            msgError!!,
            errors = listOf(),
            { isError = false }
        )
    }
}
