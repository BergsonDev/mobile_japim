
package com.example.japim

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.japim.apolloClient.TokenManager
import com.example.japim.apolloClient.generateApolloClient
import com.example.japim.graphql.GetEventsQuery
import com.example.japim.remoteDataSource.EventRemoteDataSource
import com.example.japim.ui.components.cards.GenerateCardCrud
import com.example.japim.ui.components.search.GenerateSearchBar
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

@Composable
fun GerenciarEventos(
    tokenProvider: TokenManager,
    atualizarLista: Int = 0,
    onEditarEvento: (String) -> Unit = {},
    onDeletarEvento: (String) -> Unit = {}
) {
    val eventRemoteDataSource = remember(tokenProvider) {
        EventRemoteDataSource(generateApolloClient(tokenProvider))
    }

    var events by remember {
        mutableStateOf<List<GetEventsQuery.Event?>?>(null)
    }

    var carregando by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(eventRemoteDataSource, atualizarLista) {
        carregando = true
        events = null

        try {
            events = eventRemoteDataSource.fetchEvents()
        } finally {
            carregando = false
        }
    }
    var eventoParaExcluir by remember {
        mutableStateOf<String?>(null)
    }

    var pesquisa by remember {
        mutableStateOf("")
    }

    val eventosFiltrados = events?.filter { event ->
        event != null && event.name.contains(
            pesquisa.trim(),
            ignoreCase = true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
    ) {
        GenerateSearchBar(
            placeholder = "Pesquisar eventos",
            actionSearch = { texto ->
                pesquisa = texto
            }
        )

        when {
            carregando -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            events == null -> {
                Text(
                    text = "Não foi possível carregar os eventos.",
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    textAlign = TextAlign.Center
                )
            }

            events!!.isEmpty() -> {
                Text(
                    text = "Nenhum evento cadastrado no momento.",
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    textAlign = TextAlign.Center
                )
            }

            eventosFiltrados.isNullOrEmpty() -> {
                Text(
                    text = "Nenhum evento encontrado para \"$pesquisa\".",
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    textAlign = TextAlign.Center
                )
            }

            else -> {
                eventosFiltrados?.forEach { event ->
                    if (event != null) {
                        GenerateCardCrud(
                            title = event.name,
                            text = event.short_description,
                            speaker = event.speakers.firstOrNull()?.name ?: "",
                            totalParticipants = event.total_participants,
                            totalVolunteers = event.total_participants,
                            isPayment = event.is_payment,
                            onClickUpdate = {
                                onEditarEvento(event.id)
                            },
                            onClickDelete = {
                                eventoParaExcluir = event.id
                            }
                        )
                    }
                }
            }
        }

        eventoParaExcluir?.let { eventId ->
            AlertDialog(
                onDismissRequest = {
                    eventoParaExcluir = null
                },
                title = {
                    Text("Excluir evento")
                },
                text = {
                    Text(
                        "Tem certeza de que deseja excluir este evento? " +
                                "Essa ação não poderá ser desfeita."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            eventoParaExcluir = null
                            onDeletarEvento(eventId)
                        }
                    ) {
                        Text("Excluir", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            eventoParaExcluir = null
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }

    }
}
