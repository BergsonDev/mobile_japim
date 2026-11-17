package com.example.japim

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.japim.navigation.Screen
import com.example.japim.screen.events.EventDetailScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        composable(Screen.Home.route) {
            StarterPage(
                onNavigateToCadastro = {
                    navController.navigate(Screen.RegisterAccount.route)
                },
                onNavigateToDetails = { eventId ->
                    navController.navigate(Screen.EventDetails.createRoute(eventId))
                }
            )
        }

        composable(Screen.RegisterAccount.route) {
            Cadastro()
        }

        composable(
            route = Screen.EventDetails.route,
            arguments = listOf(navArgument("event_id") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("event_id")
            if(eventId != null){
                EventDetailScreen(eventId)
            }
        }
    }
}