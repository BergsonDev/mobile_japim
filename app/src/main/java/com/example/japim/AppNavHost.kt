
package com.example.japim

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.japim.apolloClient.apolloClient
import com.example.japim.navigation.Screen
import com.example.japim.remoteDataSource.EventRemoteDataSource
import com.example.japim.screen.events.EventDetailScreen
import com.example.japim.screen.events.EventListScreen
import com.example.japim.ui.components.GenerateHeader

@Composable
private fun ContentPage(
    navController: NavController,
    content: @Composable () -> Unit
) {
    GenerateHeader(
        onNavigateToEvents = {
            navController.navigate(Screen.Events.route)
        },
        contentPage = { paddingInterno ->
            Box(
                modifier = Modifier.padding(paddingInterno)
            ) {
                content()
            }
        }
    )
}

@Composable
fun AppNavHost() {

    val navController = rememberNavController()

    // Reutiliza o Apollo Client já configurado no Japim
    val eventRemoteDataSource = remember {
        EventRemoteDataSource(apolloClient)
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // LOGIN
        composable("login") {
            Login(
                onCriarConta = {
                    navController.navigate(
                        Screen.RegisterAccount.route
                    )
                },
                onLoginSucesso = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo("login") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // TELA INICIAL
        composable(Screen.Home.route) {
            ContentPage(navController) {
                StarterPage(
                    onNavigateToCadastro = {
                        navController.navigate(
                            Screen.RegisterAccount.route
                        )
                    },
                    onNavigateToDetails = { eventId ->
                        navController.navigate(
                            Screen.EventDetails.createRoute(eventId)
                        )
                    }
                )
            }
        }

        // CADASTRO
        composable(Screen.RegisterAccount.route) {
            Cadastro(
                onVoltarLogin = {
                    navController.popBackStack("login", false)
                }
            )
        }

        // DETALHES DO EVENTO
        composable(
            route = Screen.EventDetails.route,
            arguments = listOf(
                navArgument("event_id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val eventId = backStackEntry.arguments
                ?.getString("event_id")

            if (eventId != null) {
                ContentPage(navController) {
                    EventDetailScreen(eventId)
                }
            }
        }

        // LISTA DE EVENTOS
        composable(Screen.Events.route) {
            ContentPage(navController) {
                EventListScreen()
            }
        }

        // CRIAÇÃO DE EVENTO
        composable("register_event") {
            ContentPage(navController) {
                RegisterEvent(
                    onVoltar = {
                        navController.popBackStack()
                    },
                    onSalvar = { dados ->
                        // TODO: integrar mutation de criação
                    }
                )
            }
        }

        // EDIÇÃO DE EVENTO
        composable(
            route = "register_event/{event_id}",
            arguments = listOf(
                navArgument("event_id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val eventId = backStackEntry.arguments
                ?.getString("event_id")

            if (eventId != null) {

                var dadosEvento by remember(eventId) {
                    mutableStateOf<EventFormData?>(null)
                }

                var carregando by remember(eventId) {
                    mutableStateOf(true)
                }

                LaunchedEffect(eventId) {
                    carregando = true

                    val evento = eventRemoteDataSource
                        .fetchEventById(eventId)

                    dadosEvento = evento?.toEventFormData()

                    carregando = false
                }

                ContentPage(navController) {
                    when {
                        carregando -> {
                            CircularProgressIndicator()
                        }

                        dadosEvento != null -> {
                            RegisterEvent(
                                eventId = eventId,
                                dadosIniciais = dadosEvento,
                                onVoltar = {
                                    navController.popBackStack()
                                },
                                onSalvar = { dados ->
                                    // TODO: integrar mutation de atualização
                                }
                            )
                        }

                        else -> {
                            Text(
                                "Não foi possível carregar o evento."
                            )
                        }
                    }
                }
            }
        }
    }
}
