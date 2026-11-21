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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.japim.navigation.Screen
import com.example.japim.remoteDataSource.AuthRemoteDataSource
import com.example.japim.screen.events.EventDetailScreen
import com.example.japim.screen.events.EventListScreen
import com.example.japim.ui.components.GenerateHeader
import com.example.japim.apolloClient.TokenManager
import com.example.japim.apolloClient.generateApolloClient
import com.example.japim.screen.events.RequesterPresenceScreen
import com.example.japim.screen.events.ValidateRequesterPresenceScreen
import com.example.japim.ui.events.GenerateEventValidateRequesterPresence
import com.example.japim.ui.theme.PRIMARY


@Composable
private fun ContentPage(navController : NavController, content : @Composable () -> Unit, tokenProvider: TokenManager){
    GenerateHeader(
        onNavigateToEvents = {navController.navigate(Screen.Events.route)},
        contentPage = { paddingInterno ->
            Box(modifier = Modifier.padding(paddingInterno)) {
                content()
            }
        },
        {
            tokenProvider.limparToken()
            navController.navigate(Screen.Login.route)
        }
    );
}

@Composable
fun AppNavHost(tokenProvider : TokenManager) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PRIMARY)
            }

            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    val token = tokenProvider.getToken()
                    if (!token.isNullOrBlank()) {
                        try {
                            val client = generateApolloClient(tokenProvider)
                            val authRemoteDataSource = AuthRemoteDataSource(client)
                            val me = authRemoteDataSource.auth()
                            if (me != null) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo("splash") { inclusive = true }
                                    launchSingleTop = true
                                }
                                return@launch
                            }
                        } catch (e: Exception) {
                            Log.e("AppNavHost", "Erro ao validar token", e)
                        }
                    }
                    tokenProvider.limparToken()
                    navController.navigate(Screen.Login.route) {
                        popUpTo("splash") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }

        composable(Screen.Login.route) {
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
                },
                tokenProvider
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

        // details for events screen
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
                            event_id = eventId,
                            { eventId->
                                navController.navigate(Screen.RequesterPresence.createRoute(eventId))
                            },
                            {
                                eventId -> navController.navigate(Screen.ValidateRequesterPresence.createRoute(eventId))

                            }
                        )
                    },
                    tokenProvider
                )
            }
        }

        // list of events
        composable(Screen.Events.route) {
            ContentPage(
                navController,
                {
                    EventListScreen(tokenProvider = tokenProvider)
                },
                tokenProvider

            )
        }

        // requester presence
        composable(
            route= Screen.RequesterPresence.route,
            arguments = listOf(
                navArgument("event_id"){
                    type = NavType.StringType
                }
            )
        ){
            backStackEntry->
            val eventId = backStackEntry.arguments?.getString("event_id")

            if(eventId!=null){
                ContentPage(
                    navController,
                    {
                        RequesterPresenceScreen(
                            tokenProvider,
                            {
                                navController.navigate(Screen.EventDetails)
                            },
                            eventId
                        )
                    },
                    tokenProvider

                )
            }
        }

        //validate presence
        composable(
            route= Screen.ValidateRequesterPresence.route,
            arguments = listOf(
                navArgument("event_id"){
                    type = NavType.StringType
                }
            )
        ){
                backStackEntry->
            val eventId = backStackEntry.arguments?.getString("event_id")

            if(eventId!=null){
                ContentPage(
                    navController,
                    {
                        ValidateRequesterPresenceScreen(
                            tokenProvider,
                            onClickReturn = {
                                navController.navigate(Screen.EventDetails.createRoute(eventId))
                            },
                            eventId
                        )
                    },
                    tokenProvider

                )
            }
        }
    }
}
