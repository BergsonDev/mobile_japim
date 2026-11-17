package com.example.japim.screen.events

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.japim.graphql.GetEventByIdQuery
import com.example.japim.remoteDataSource.EventRemoteDataSource
import com.example.japim.ui.components.GenerateHeader
import com.example.japim.ui.events.GenerateEventDetails
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Composable
fun EventDetailScreen(event_id : String){

    val eventRemoteDataSource = remember { EventRemoteDataSource(com.example.japim.apolloClient.apolloClient) }
    GenerateHeader(
        contentPage = { paddingResponse ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingResponse) // O padding do header entra aqui
            ) {

                val eventRemoteDataSource = remember { EventRemoteDataSource(com.example.japim.apolloClient.apolloClient) }
                var event by remember { mutableStateOf<GetEventByIdQuery.Event?>(null) }
                LaunchedEffect(Unit) {
                    event = eventRemoteDataSource.fetchEventById(event_id);
                }
                Log.e("teste", event?.id ?: "não pegou o id");
                event?.let {
                    GenerateEventDetails(
                        title = it.name,
                        description = it.description,
                        name_speakers = it.speakers.map { speaker -> speaker?.name ?: "" },
                        total_participants = it.max_participants,
                        max_participants = it.max_participants,
                        total_volunteers = it.max_volunteers,
                        max_volunteers = it.max_volunteers,
                        workload = it.workload,
                        value = it.value,
                        onClickParticipant = {},
                        onClickVolunteer = {}
                    )
                }

            }
        }
    );
}
