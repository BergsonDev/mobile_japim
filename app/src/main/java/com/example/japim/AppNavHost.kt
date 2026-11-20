package com.example.japim

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.japim.graphql.AuthQuery
import com.example.japim.navigation.Screen
import com.example.japim.remoteDataSource.AuthRemoteDataSource
import com.example.japim.remoteDataSource.EventRemoteDataSource
import com.example.japim.screen.events.EventDetailScreen
import com.example.japim.screen.events.EventListScreen
import com.example.japim.ui.components.GenerateHeader
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.example.japim.apolloClient.TokenManager


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
fun AppNavHost(tokenProvider : TokenManager) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            Login(
                tokenProvider=tokenProvider,
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
                        tokenProvider = tokenProvider,
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
                tokenProvider=tokenProvider,
                onVoltarLogin = {
                    navController.popBackStack("login", false)
                }
            )
        }

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
                        EventDetailScreen(
                            tokenProvider = tokenProvider,
                            event_id = eventId
                        )
                    }
                )
            }
        }

        // Lista de eventos
        composable(Screen.Events.route) {
            ContentPage(
                navController,
                {
                    EventListScreen(tokenProvider = tokenProvider)
                }
            )
        }
    }
}
