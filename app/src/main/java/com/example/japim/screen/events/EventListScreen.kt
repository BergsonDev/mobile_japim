package com.example.japim.screen.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.japim.graphql.GetEventsQuery
import com.example.japim.remoteDataSource.EventRemoteDataSource
import com.example.japim.ui.components.GenerateHeader
import com.example.japim.ui.components.cards.GenerateCard
import com.example.japim.ui.components.cards.GenerateCardCrud
import com.example.japim.ui.components.search.GenerateSearchBar
import com.example.japim.ui.theme.SECONDARY
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import com.example.japim.ui.theme.SUCCESS
import com.example.japim.ui.theme.PRIMARY
import com.example.japim.ui.theme.MPlus2
import com.apollographql.apollo.ApolloClient



@Composable
fun EventListScreen(
    apolloClient: ApolloClient,
    onClickRegister: () -> Unit,
    onClickUpdate: (String) -> Unit
) {
    val eventRemoteDataSource = remember(apolloClient) {
        EventRemoteDataSource(apolloClient)
    }

    val coroutineScope = rememberCoroutineScope()

    var events by remember {
        mutableStateOf<List<GetEventsQuery.Event?>?>(emptyList())
    }

    val loadEvents: () -> Unit = {
        coroutineScope.launch {
            events = eventRemoteDataSource.fetchEvents().getOrNull()
        }
    }

    LaunchedEffect(Unit) {
        loadEvents()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // Botão de cadastro
        Button(
            onClick = onClickRegister,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
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

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "NOVO",
                color = PRIMARY,
                fontFamily = MPlus2
            )
        }


        // Barra de pesquisa
        GenerateSearchBar("teste", {})


        // Lista de eventos
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            events?.forEach { event ->
                if (event != null) {
                    GenerateCardCrud(
                        event.name,
                        event.short_description,
                        event.speakers.firstOrNull()?.name ?: "",
                        10,
                        10,
                        event.is_payment,
                        {onClickUpdate(event.id)} , // Editar: integrar depois
                        {
                            coroutineScope.launch {
                                eventRemoteDataSource.deleteEvent(event.id)
                                loadEvents()
                            }
                        }
                    )
                }
            }
        }
    }
}
