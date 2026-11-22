package com.example.japim.ui.components.menu.contentMenu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.japim.ui.theme.FIFTH
import com.example.japim.ui.theme.FOURTH
import com.example.japim.ui.theme.MPlus2

@Composable
private fun generateButton (title : String, onClick : ()-> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth(.7f).padding(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FOURTH
        ),
        shape = RoundedCornerShape(10.dp),
        onClick = onClick,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 10.dp
        ),
    ){
        Text(title, color=FIFTH, fontFamily = MPlus2, fontSize = 20.sp);
    }
}

@Composable
fun GenerateContentMenu(
    onNavigateToEvents : ()-> Unit,
    onNavigateToAdmin: () -> Unit = {},
    onNavigateToCertificates: () -> Unit = {},
    onNavigateToParticipatingEvents: () -> Unit = {},
    onExitNavigate : ()-> Unit
){

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        generateButton("Meus Certificados") {
            onNavigateToCertificates()
        }
        generateButton("Eventos que Participo") {
            onNavigateToParticipatingEvents()
        }
        generateButton("Eventos") {
            onNavigateToEvents()
        }
        generateButton("Validar Certificado") {
        }
        generateButton("Página Admin") {
            onNavigateToAdmin()
        }
        generateButton("Sair da Conta") {
            onExitNavigate()
        }
    }
}
