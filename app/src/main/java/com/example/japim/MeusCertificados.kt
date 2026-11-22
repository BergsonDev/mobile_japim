
package com.example.japim

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.japim.ui.components.search.GenerateSearchBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun MeusCertificados(
    onBaixarCertificado: (String) -> Unit = {},
    onVerCertificado: (String) -> Unit = {}
) {
    var pesquisa by remember { mutableStateOf("") }

    val certificados = listOf(
        "Nome do Evento" to
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    )

    val certificadosFiltrados = certificados.filter {
        it.first.contains(pesquisa, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
    ) {
        GenerateSearchBar(
            placeholder = "Pesquisar certificados...",
            actionSearch = { pesquisa = it }
        )

        certificadosFiltrados.forEachIndexed { index, certificado ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Yellow
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                )  {
                    Text(
                        text = certificado.first,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = certificado.second,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                onBaixarCertificado(index.toString())
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text(
                                text = "Baixar Certificado",
                                color = Color.White,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp
                            )
                        }

                        Button(
                            onClick = {
                                onVerCertificado(index.toString())
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text(
                                text = "Ver Certificado",
                                color = Color.White,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
