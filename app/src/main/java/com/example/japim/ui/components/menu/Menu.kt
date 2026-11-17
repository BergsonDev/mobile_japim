package com.example.japim.ui.components.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.japim.ui.theme.FIFTH
import com.example.japim.ui.theme.FOURTH
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue // IMPORTANTE para o 'by' funcionar
import androidx.compose.runtime.setValue // IMPORTANTE para o 'by' funcionar
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.japim.R
import com.example.japim.ui.theme.MPlus2
import com.example.japim.ui.theme.THIRD

@Composable
fun GenerateMenu(
    tintIcone: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = THIRD,
    contentMenu: @Composable (closeMenu: () -> Unit) -> Unit
) {
    var viewDialog by remember { mutableStateOf(false) }

    var animationStart by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val closeAnimation: () -> Unit = {
        scope.launch {
            animationStart = false
            delay(300)
            viewDialog = false
        }
    }


    IconButton(onClick = { viewDialog = true}) {
        Image(
            painter = painterResource(
                id = R.drawable.logo_japim_branco
            ),
                contentDescription = "Logo do Japim",
                modifier = Modifier.size(50.dp)
            )
    }
    if (viewDialog) {
        Dialog(
            onDismissRequest = closeAnimation,
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {

            LaunchedEffect(Unit) {
                animationStart = true
            }

            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedVisibility(
                    visible = animationStart,
                    enter = slideInHorizontally(
                        initialOffsetX = { totalWidth -> totalWidth },
                        animationSpec = tween(durationMillis = 300)
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { totalWidth -> totalWidth },
                        animationSpec = tween(durationMillis = 300)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundColor)
                            .padding(16.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = closeAnimation) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "Fechar menu",
                                    tint = FOURTH,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Menu Principal",
                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp),
                                color= FOURTH,
                                fontFamily = MPlus2,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        contentMenu(closeAnimation)
                    }
                }
            }
        }
    }
}