package com.example.japim

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.japim.apolloClient.TokenManager
import com.example.japim.apolloClient.generateApolloClient
import com.example.japim.graphql.GetEventsQuery
import com.example.japim.remoteDataSource.EventRemoteDataSource
import com.example.japim.ui.components.GenerateErrorToast
import com.example.japim.ui.components.GenerateSuccessToast
import com.example.japim.ui.components.cards.GenerateCard
import com.example.japim.ui.components.search.GenerateSearchBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViewStarterPage(
    modifier: Modifier = Modifier,
    eventRemote: EventRemoteDataSource? = null,
    onNavigateToDetails: (String) -> Unit = {}
){
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var errorList by remember { mutableStateOf<List<String>>(emptyList()) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var events by remember { mutableStateOf<List<GetEventsQuery.Event?>?>(emptyList()) }
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    suspend fun loadEvents() {
        val result = eventRemote?.fetchEvents()
        result?.onSuccess { data, _ ->
            events = data
        }?.onError { msg, errors ->
            errorMessage = msg
            errorList = errors
        }
    }

    LaunchedEffect(Unit) {
        loadEvents()
    }

    Box(modifier = modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                coroutineScope.launch {
                    isRefreshing = true
                    loadEvents()
                    isRefreshing = false
                }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .verticalScroll(rememberScrollState())
            ) {

                GenerateSearchBar("teste", {})

                events?.forEach { event ->
                    if (event != null) {
                        GenerateCard(
                            event.name,
                            event.short_description,
                            event.speakers.firstOrNull()?.name ?: "",
                            event.total_participants,
                            event.total_participants,
                            event.is_payment
                        ) {
                            onNavigateToDetails(event.id)
                        }
                    }
                }
            }
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

        if (successMessage != null) {
            GenerateSuccessToast(
                message = successMessage!!,
                onDismiss = {
                    successMessage = null
                }
            )
        }
    }
}


@Composable
fun StarterPage(
    tokenProvider: TokenManager,
    onNavigateToCadastro: () -> Unit = {},
    onNavigateToDetails: (String) -> Unit = {},
    onNavigateToEvents : () -> Unit = {}
){
    val eventRemoteDataSource = remember { EventRemoteDataSource(generateApolloClient(tokenProvider)) }

    ViewStarterPage(
        eventRemote = eventRemoteDataSource,
        onNavigateToDetails = onNavigateToDetails
    )
}

@Preview(showBackground = true)
@Composable
fun StarterPagePreview(
    onNavigateToCadastro: () -> Unit = {},
    onNavigateToDetails: (String) -> Unit = {},
    onNavigateToEvents : () -> Unit = {}
){
    val context = LocalContext.current
    val tokenProvider = remember { TokenManager(context) }
    val eventRemoteDataSource = remember { EventRemoteDataSource(generateApolloClient(tokenProvider)) }

    ViewStarterPage(
        eventRemote = eventRemoteDataSource,
        onNavigateToDetails = onNavigateToDetails
    )
}
