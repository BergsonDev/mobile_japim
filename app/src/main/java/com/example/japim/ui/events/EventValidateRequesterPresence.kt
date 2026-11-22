package com.example.japim.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.japim.ui.theme.FOURTH
import com.example.japim.ui.theme.MPlus2
import com.example.japim.ui.theme.PRIMARY
import com.example.japim.ui.theme.SECONDARY

@Composable
fun GenerateEventValidateRequesterPresence(
    onClickReturn: () -> Unit,
    cameraQrCodePreview: @Composable () -> Unit,
    codeDecrypt: MutableState<String?>,
    user_name : MutableState<String?>
) {
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
                    tint = PRIMARY
                )
            }
        }

        // Main Yellow Column Container
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
                .background(SECONDARY, shape = RoundedCornerShape(10.dp)),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Confirmar Presença",
                    fontSize = 25.sp,
                    fontFamily = MPlus2,
                    fontWeight = FontWeight.Bold,
                    color = PRIMARY
                )
            }

            // Square Camera Preview Component
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                cameraQrCodePreview()
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Confirmar Presença",
                    fontSize = 22.sp,
                    fontFamily = MPlus2,
                    fontWeight = FontWeight.Bold,
                    color = PRIMARY
                )

                if (codeDecrypt.value != null) {
                    Text(
                        codeDecrypt.value.toString(),
                        fontSize = 18.sp,
                        fontFamily = MPlus2,
                        fontWeight = FontWeight.Bold,
                        color = PRIMARY,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (user_name.value != null) {
                    Text(
                        user_name.value.toString(),
                        fontSize = 18.sp,
                        fontFamily = MPlus2,
                        fontWeight = FontWeight.Bold,
                        color = PRIMARY,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
