package com.example.japim

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.example.japim.navigation.Screen
import com.example.japim.screen.events.EventDetailScreen
import com.example.japim.screen.events.EventListScreen
import com.example.japim.ui.components.GenerateHeader

@Composable
private fun ContentPage(navController : NavController, content : @Composable () -> Unit){
    GenerateHeader(
        onNavigateToEvents = {navController.navigate(Screen.Events.route)},
        contentPage = { paddingInterno ->
            Box(modifier = Modifier.padding(paddingInterno)) {
                content()
            }
        }
    );
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // Tela de Login
        composable("login") {
            Login(
                onCriarConta = {
                    navController.navigate(Screen.RegisterAccount.route)
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

        // Tela inicial do aplicativo
        composable(Screen.Home.route) {
            ContentPage(
                navController,
                {
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
            )
        }

        // Tela de Cadastro
        composable(Screen.RegisterAccount.route) {
            Cadastro(
                onVoltarLogin = {
                    navController.popBackStack("login", false)
                }
            )
        }

        // Detalhes de um evento
        composable(
            route = Screen.EventDetails.route,
            arguments = listOf(
                navArgument("event_id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val eventId =
                backStackEntry.arguments?.getString("event_id")

            if (eventId != null) {
                ContentPage(
                    navController,
                    {
                        EventDetailScreen(eventId)
                    }
                )
            }
        }

        // Lista de eventos
        composable(Screen.Events.route) {
            ContentPage(
                navController,
                {
                    EventListScreen()
                }
            )
        }
    }
}
