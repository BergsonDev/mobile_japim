package com.example.japim.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.japim.globalsFunctions.parseReal
import com.example.japim.ui.theme.MPlus2
import com.example.japim.ui.theme.PRIMARY
import com.example.japim.ui.theme.SECONDARY
import com.example.japim.ui.theme.SUCCESS


@Composable
private fun GenerateCardEvent(
    total_participants : Int,
    max_participants : Int,
    total_volunteers : Int,
    max_volunteers : Int,
    workload : Int,
    value : Double,
    onClickParticipant : ()-> Unit,
    onClickVolunteer : ()-> Unit
    ){
    Card(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        shape = RoundedCornerShape(0),
        colors = CardDefaults.elevatedCardColors(
            containerColor = SECONDARY.copy(0.4f),
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround,

        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Participantes",
                    tint = PRIMARY
                )

                Text("Participantes", color= PRIMARY, fontFamily = MPlus2, fontSize = 13.sp, fontWeight = FontWeight.Bold);
                Text("$total_participants/$max_participants", color= PRIMARY, fontFamily = MPlus2, fontSize = 15.sp);

            }


            Column(
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Icon(
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = "Voluntários",
                    tint = PRIMARY
                )

                Text("Voluntários", color= PRIMARY, fontFamily = MPlus2, fontSize = 13.sp, fontWeight = FontWeight.Bold);
                Text("$total_volunteers/$max_volunteers", color= PRIMARY, fontFamily = MPlus2, fontSize = 15.sp);
            }


            Column(
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Icon(
                    imageVector = Icons.Default.MoreTime,
                    contentDescription = "Carga Horária",
                    tint = PRIMARY
                )

                Text("Carga Horária", color= PRIMARY, fontFamily = MPlus2, fontSize = 13.sp,fontWeight = FontWeight.Bold);
                Text("$workload", color= PRIMARY, fontFamily = MPlus2, fontSize = 15.sp);
            }


            Column(
                horizontalAlignment = Alignment.CenterHorizontally

            ) {

                Icon(
                    imageVector = Icons.Default.Paid,
                    contentDescription = "Valor",
                    tint = PRIMARY
                )

                Text("Valor", color= PRIMARY, fontFamily = MPlus2, fontSize = 13.sp,fontWeight = FontWeight.Bold);
                Text("R$ ${value.parseReal()}", color= PRIMARY, fontFamily = MPlus2, fontSize = 15.sp);
            }
        }

        Row(
            Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            Button(
                onClick = onClickParticipant,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 10.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SUCCESS
                ),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text("Ser Participante")
            }

            Button(
                onClick = onClickVolunteer,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 10.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SUCCESS
                ),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text("Ser Voluntário")
            }

        }


    }
}
@Composable
fun GenerateEventDetails(
    title: String,
    description: String,
    name_speakers: List<String>,
    total_participants: Int,
    max_participants: Int,
    total_volunteers: Int,
    max_volunteers: Int,
    workload: Int,
    value: Double,
    onClickParticipant: () -> Unit,
    onClickVolunteer: () -> Unit,
){
    Column(
        modifier = Modifier.fillMaxHeight(.9f).fillMaxWidth().padding(12.dp).clip(RoundedCornerShape(10.dp)).background(SECONDARY).padding(vertical = 12.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {

        Text(
            title,
            color = PRIMARY,
            fontFamily = MPlus2,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        );

        Text(description, color = PRIMARY, fontFamily = MPlus2, fontSize = 20.sp);

        Text(
            "Palestrante",
            color = PRIMARY,
            fontFamily = MPlus2,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        );

        name_speakers.forEach { speaker->
            Text(speaker, color = PRIMARY, fontFamily = MPlus2, fontSize = 25.sp);
        }

        GenerateCardEvent(
            total_participants,
            max_participants,
            total_volunteers,
            max_volunteers,
            workload,
            value,
            onClickParticipant,
            onClickVolunteer
        );

    }

}
