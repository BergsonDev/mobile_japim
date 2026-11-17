package com.example.japim

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.japim.graphql.GetEventsQuery
import com.example.japim.graphql.type.Event
import com.example.japim.remoteDataSource.EventRemoteDataSource
import com.example.japim.ui.components.GenerateHeader
import com.example.japim.ui.components.cards.GenerateCard
import com.example.japim.ui.components.search.GenerateSearchBar

@Composable
private fun ViewStarterPage(
    modifier: Modifier = Modifier,
    eventRemote: EventRemoteDataSource? = null,
    onNavigateToDetails: (String) -> Unit = {}
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
    ) {

        GenerateSearchBar("teste",{});

        var events by remember {
            mutableStateOf<List<GetEventsQuery.Event?>?>(emptyList())
        }
        LaunchedEffect(Unit) {
            events = eventRemote?.fetchEvents();

        }

        events?.forEach { event ->
            if (event != null) {
                GenerateCard(
                    event.name,
                    event.short_description,
                    event.speakers.firstOrNull()?.name ?: "",
                    10,
                    10,
                    event.is_payment,
                    {
                        onNavigateToDetails(event.id)
                    }
                )
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun StarterPage(
    onNavigateToCadastro: () -> Unit = {},
    onNavigateToDetails: (String) -> Unit = {}
){
    val eventRemoteDataSource = remember { EventRemoteDataSource(com.example.japim.apolloClient.apolloClient) }
    GenerateHeader(
        contentPage = { paddingResponse ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingResponse) // O padding do header entra aqui
            ) {
                ViewStarterPage(
                    eventRemote = eventRemoteDataSource,
                    onNavigateToDetails = onNavigateToDetails
                )
            }
        }
    );
}
