package com.example.japim.screen.adm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.japim.ui.theme.*

@Composable
fun PaginaAdmin(
    onGerenciarUsuarios: () -> Unit = {},
    onGerenciarCargos: () -> Unit = {},
    onVoltar: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PRIMARY)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Painel Administrativo",
            color = SECONDARY,
            fontFamily = MPlus2,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Gerenciamento do sistema Japim",
            color = SECONDARY,
            fontFamily = MPlus2,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        BotaoAdmin(
            "Gerenciar Usuários",
            onGerenciarUsuarios
        )

        Spacer(modifier = Modifier.height(16.dp))

        BotaoAdmin(
            "Gerenciar Cargos",
            onGerenciarCargos
        )

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = onVoltar,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "Voltar",
                color = SECONDARY,
                fontFamily = MPlus2
            )
        }
    }
}

@Composable
private fun BotaoAdmin(
    titulo: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FOURTH
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp
        )
    ) {
        Text(
            text = titulo,
            color = FIFTH,
            fontFamily = MPlus2,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )
    }
}