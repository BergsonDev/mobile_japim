package com.example.japim.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.japim.ui.theme.ERROR
import com.example.japim.ui.theme.PRIMARY
import com.example.japim.ui.theme.SECONDARY
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun GenerateErrorToast(
    visible: Boolean = true,
    title: String = "Erro na Consulta GraphQL",
    message: String,
    errors: List<String> = emptyList(),
    onDismiss: () -> Unit,
    autoDismissDurationMillis: Long = 5000L
) {
    if (autoDismissDurationMillis > 0 && visible) {
        LaunchedEffect(message, errors) {
            delay(autoDismissDurationMillis.milliseconds)
            onDismiss()
        }
    }

    // A mensagem principal já é o primeiro erro retornado pela API; evita exibi-la
    // duplicada na lista de detalhes.
    val detailErrors = errors.filterNot { it == message }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color.Black,
                        spotColor = Color.Black
                    ),
                shape = RoundedCornerShape(16.dp),
                color = SECONDARY,
                border = BorderStroke(1.5.dp, PRIMARY),
                shadowElevation = 16.dp,
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Erro",
                        tint = ERROR,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = title,
                            color = PRIMARY,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = message,
                            color = PRIMARY,
                            fontSize = 13.sp
                        )
                        if (detailErrors.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            detailErrors.forEach { err ->
                                Text(
                                    text = "• $err",
                                    color = ERROR,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = PRIMARY
                        )
                    }
                }
            }
        }
    }
}
