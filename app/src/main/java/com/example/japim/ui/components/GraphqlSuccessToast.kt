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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.japim.ui.theme.PRIMARY
import com.example.japim.ui.theme.SECONDARY
import com.example.japim.ui.theme.SUCCESS
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun GenerateSuccessToast(
    visible: Boolean = true,
    title: String = "Operação realizada com sucesso",
    message: String,
    details: List<String> = emptyList(),
    onDismiss: () -> Unit,
    autoDismissDurationMillis: Long = 4000L
) {
    if (autoDismissDurationMillis > 0 && visible) {
        LaunchedEffect(message, details) {
            delay(autoDismissDurationMillis.milliseconds)
            onDismiss()
        }
    }

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
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Sucesso",
                        tint = SUCCESS,
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
                        if (message.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = message,
                                color = PRIMARY,
                                fontSize = 13.sp
                            )
                        }
                        if (details.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            details.forEach { detail ->
                                Text(
                                    text = "• $detail",
                                    color = PRIMARY,
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

@Preview(showBackground = true)
@Composable
fun PreviewSuccessToast() {
    GenerateSuccessToast(
        visible = true,
        title = "Sucesso",
        message = "Operação realizada",
        onDismiss = {}
    )
}
