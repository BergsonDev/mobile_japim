
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

@Composable
fun GerenciarEventos(
    tokenProvider: TokenManager,
    onEditarEvento: (String) -> Unit = {},
    onDeletarEvento: (String) -> Unit = {}
) {
    val eventRemoteDataSource = remember(tokenProvider) {
        EventRemoteDataSource(generateApolloClient(tokenProvider))
    }

    var events by remember {
        mutableStateOf<List<GetEventsQuery.Event?>?>(emptyList())
    }

    LaunchedEffect(eventRemoteDataSource) {
        events = eventRemoteDataSource.fetchEvents()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
    ) {
        GenerateSearchBar("Pesquisar eventos", {})

        events?.forEach { event ->
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
                        onDeletarEvento(event.id)
                    }
                )
            }
        }
    }
}
