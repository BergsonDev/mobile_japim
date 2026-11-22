package com.example.japim.ui.components.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.japim.ui.theme.*

@Composable
fun EventCard(
    title: String,
    text: String,
    speaker: String,
    totalParticipants: Int,
    totalVolunteers: Int,
    isPayment: Boolean,
    canUpdate: Boolean = false,
    canDelete: Boolean = false,
    actionsEnabled: Boolean = true,
    onClickDetails: () -> Unit,
    onClickUpdate: () -> Unit = {},
    onClickDelete: () -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = SECONDARY
        )
    ) {

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Event title
            Text(
                text = title,
                color = PRIMARY,
                fontFamily = MPlus2,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Event description
            Text(
                text = text,
                color = PRIMARY,
                fontFamily = MPlus2,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Event details
            Button(
                onClick = onClickDetails,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SUCCESS
                ),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    text = "Saiba Mais",
                    color = PRIMARY,
                    fontFamily = MPlus2,
                    fontWeight = FontWeight.Bold
                )
            }

            // Management actions
            if (canUpdate || canDelete) {

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    if (canUpdate) {
                        Button(
                            onClick = onClickUpdate,
                            enabled = actionsEnabled,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WARNING
                            ),
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Text(
                                text = "Editar",
                                color = PRIMARY,
                                fontFamily = MPlus2
                            )
                        }
                    }

                    if (canDelete) {
                        Button(
                            onClick = onClickDelete,
                            enabled = actionsEnabled,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ERROR
                            ),
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Text(
                                text = "Excluir",
                                color = FOURTH,
                                fontFamily = MPlus2
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Event information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Participantes",
                        tint = FIFTH
                    )

                    Text(
                        text = totalParticipants.toString(),
                        fontFamily = MPlus2,
                        fontWeight = FontWeight.Bold,
                        color = FIFTH
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "Voluntários",
                        tint = FIFTH
                    )

                    Text(
                        text = totalVolunteers.toString(),
                        fontFamily = MPlus2,
                        fontWeight = FontWeight.Bold,
                        color = FIFTH
                    )
                }

                if (isPayment) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = "Evento pago",
                            tint = FIFTH
                        )

                        Text(
                            text = "Pago",
                            fontFamily = MPlus2,
                            fontWeight = FontWeight.Bold,
                            color = FIFTH
                        )
                    }
                }
            }

            if (speaker.isNotBlank()) {

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Palestrante",
                        tint = FIFTH
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = speaker,
                        fontFamily = MPlus2,
                        fontWeight = FontWeight.Bold,
                        color = FIFTH
                    )
                }
            }
        }
    }
}
