package com.example.japim.ui.events

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.japim.advancedFeatures.generateQrCode
import com.example.japim.ui.theme.FOURTH
import com.example.japim.ui.theme.MPlus2
import com.example.japim.ui.theme.PRIMARY
import com.example.japim.ui.theme.SECONDARY


@Composable
fun GenerateEventRequesterPresence(
    generateQrCode :@Composable ()-> Unit,
    onClickReturn : ()->Unit,
    code_presence : String,

    ){
    Column(
        modifier = Modifier.fillMaxSize().background(PRIMARY),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Start
        ) {

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
                    tint = PRIMARY,


                    )
            }

        }

        Column(
            modifier = Modifier.padding(10.dp).fillMaxSize().background(SECONDARY, shape = RoundedCornerShape(10.dp)),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Solicitar Presença",
                    fontSize = 25.sp,
                    fontFamily = MPlus2,
                    fontWeight= FontWeight.Bold,
                    color=PRIMARY
                )
            }

            Column(
                modifier = Modifier.fillMaxHeight(.8f)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(
                        "Código de Validação",
                        fontSize = 25.sp,
                        fontFamily = MPlus2,
                        fontWeight = FontWeight.Bold,
                        color = PRIMARY
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    generateQrCode()
                }


                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        code_presence,
                        fontSize = 25.sp,
                        fontFamily = MPlus2,
                        fontWeight = FontWeight.Bold,
                        color = PRIMARY
                    )

                }


            }


        }
    }
}