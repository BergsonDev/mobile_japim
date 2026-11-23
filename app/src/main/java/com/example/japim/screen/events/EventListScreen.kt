package com.example.japim.screen.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.japim.ui.components.cards.EventCard
import com.example.japim.ui.components.search.GenerateSearchBar
import com.example.japim.ui.theme.*
import com.example.japim.viewmodel.EventViewModel

@Composable
fun EventListScreen(
    viewModel: EventViewModel,
    onClickRegister: () -> Unit,
    onClickUpdate: (String) -> Unit,
    onClickDetails: (String) -> Unit,
    canCreate: Boolean = false,
    canUpdate: Boolean = false,
    canDelete: Boolean = false,
    currentUserId: String? = null,
    currentUserRole: String? = null
) {

    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isDeleting by viewModel.isDeleting.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var search by rememberSaveable {
        mutableStateOf("")
    }

    var eventToDelete by remember {
        mutableStateOf<String?>(null)
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val filteredEvents = remember(events, search) {
        events.filter { event ->
            event.name.contains(
                search.trim(),
                ignoreCase = true
            )
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(error) {
        if (error != null && events.isNotEmpty()) {
            snackbarHostState.showSnackbar(error!!)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = PRIMARY,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PRIMARY)
                .padding(paddingValues)
        ) {

            // Register button
            if (canCreate) {
                Button(
                    onClick = onClickRegister,
                    enabled = !isLoading && !isDeleting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SUCCESS
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = PRIMARY
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "NOVO",
                        color = PRIMARY,
                        fontFamily = MPlus2
                    )
                }
            }

            // Search bar
            GenerateSearchBar(
                placeholder = "Pesquisar eventos",
                actionSearch = {
                    search = it
                }
            )

            // Delete progress
            if (isDeleting) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = SECONDARY
                )
            }

            // Event list
            when {

                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = SECONDARY
                        )
                    }
                }

                error != null && events.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = error
                                ?: "Erro ao carregar eventos.",
                            color = SECONDARY,
                            fontFamily = MPlus2,
                            textAlign = TextAlign.Center
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        Button(
                            onClick = {
                                viewModel.loadEvents()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SUCCESS
                            )
                        ) {
                            Text(
                                text = "Tentar novamente",
                                color = PRIMARY,
                                fontFamily = MPlus2
                            )
                        }
                    }
                }

                events.isEmpty() -> {
                    Text(
                        text = "Nenhum evento cadastrado no momento.",
                        color = SECONDARY,
                        fontFamily = MPlus2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp),
                        textAlign = TextAlign.Center
                    )
                }

                filteredEvents.isEmpty() -> {
                    Text(
                        text = "Nenhum evento encontrado para \"$search\".",
                        color = SECONDARY,
                        fontFamily = MPlus2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp),
                        textAlign = TextAlign.Center
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {

                        items(
                            items = filteredEvents,
                            key = { it.id }
                        ) { event ->


                            val role = currentUserRole?.uppercase()

                            val isOwner = currentUserId != null &&
                                    event.created_by.id == currentUserId

                            val canManageThisEvent = when (role) {
                                "ADMIN", "COORD", "DIR" -> true
                                "SPE" -> isOwner
                                else -> false
                            }

                            val canUpdateThisEvent =
                                canUpdate && canManageThisEvent

                            val canDeleteThisEvent =
                                canDelete && canManageThisEvent


                            EventCard(
                                title = event.name,
                                text = event.short_description,
                                speaker = event.speakers
                                    .firstOrNull()?.name ?: "",
                                totalParticipants =
                                    event.total_participants,
                                totalVolunteers =
                                    event.total_volunteers,
                                isPayment = event.is_payment,
                                canUpdate = canUpdateThisEvent,
                                canDelete = canDeleteThisEvent,
                                actionsEnabled = !isDeleting,

                                onClickDetails = {
                                    onClickDetails(event.id)
                                },

                                onClickUpdate = {
                                    onClickUpdate(event.id)
                                },

                                onClickDelete = {
                                    eventToDelete = event.id
                                }
                            )
                        }
                    }
                }
            }
        }

        // Delete confirmation
        eventToDelete?.let { eventId ->

            val eventName = events.find {
                it.id == eventId
            }?.name.orEmpty()

            AlertDialog(
                onDismissRequest = {
                    eventToDelete = null
                },

                title = {
                    Text("Excluir evento")
                },

                text = {
                    Text(
                        "Tem certeza de que deseja excluir " +
                                "o evento \"$eventName\"? " +
                                "Essa ação não poderá ser desfeita."
                    )
                },

                confirmButton = {
                    TextButton(

                        enabled = canDelete && !isDeleting &&
                                events.any { event ->
                                    event.id == eventId &&
                                            when (currentUserRole?.uppercase()) {
                                                "ADMIN", "COORD", "DIR" -> true
                                                "SPE" ->
                                                    currentUserId != null &&
                                                            event.created_by.id == currentUserId
                                                else -> false
                                            }
                                },

                        onClick = {
                            eventToDelete = null
                            viewModel.deleteEvent(eventId)
                        }
                    ) {
                        Text(
                            text = "Excluir",
                            color = ERROR
                        )
                    }
                },

                dismissButton = {
                    TextButton(
                        onClick = {
                            eventToDelete = null
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}