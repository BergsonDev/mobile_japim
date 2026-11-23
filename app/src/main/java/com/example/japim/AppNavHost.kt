package com.example.japim

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import com.example.japim.ADM.CadastroAdm
import com.example.japim.ADM.GerenciarUsuarios
import com.example.japim.apolloClient.TokenManager
import com.example.japim.apolloClient.generateApolloClient
import com.example.japim.navigation.Screen
import com.example.japim.remoteDataSource.AuthRemoteDataSource
import com.example.japim.remoteDataSource.EventRemoteDataSource
import com.example.japim.screen.auth.MyEventsScreen
import com.example.japim.screen.events.EventDetailScreen
import com.example.japim.screen.events.EventListPresenceScreen
import com.example.japim.screen.events.EventListScreen
import com.example.japim.screen.events.RequesterPresenceScreen
import com.example.japim.screen.events.ValidateRequesterPresenceScreen
import com.example.japim.ui.components.GenerateHeader
import com.example.japim.ui.theme.PRIMARY

@Composable
private fun ContentPage(navController: NavController, content: @Composable () -> Unit, tokenProvider: TokenManager) {
    GenerateHeader(
        onNavigateToEvents = {
            navController.navigate(Screen.Events.route)
        },
        onNavigateToAdmin = {
            navController.navigate("pagina_admin")
        },
        onNavigateToCertificates = {
            navController.navigate("meus_certificados")
        },
        onNavigateToParticipatingEvents = {
            navController.navigate(Screen.MyEvents.route)
        },
        contentPage = { paddingInterno ->
            Box(
                modifier = Modifier.padding(paddingInterno)
            ) {
                content()
            }
        },
        onExitAccount = {
            tokenProvider.limparToken()
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    )
}

@Composable
fun AppNavHost(tokenProvider: TokenManager) {
    val navController = rememberNavController()

    // Cria o Apollo Client utilizando o TokenManager
    val apolloClient = remember(tokenProvider) {
        generateApolloClient(tokenProvider)
    }

    // Reutiliza o Apollo Client para gerenciar os eventos
    val eventRemoteDataSource = remember(apolloClient) {
        EventRemoteDataSource(apolloClient)
    }

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
                            return@LaunchedEffect
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

        composable(Screen.Login.route) {
            Login(
                tokenProvider = tokenProvider,
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
                tokenProvider = tokenProvider,
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
            val eventId = backStackEntry.arguments?.getString("event_id")

            if (eventId != null) {
                ContentPage(
                    navController,
                    {
                        EventDetailScreen(
                            tokenProvider = tokenProvider,
                            event_id = eventId,
                            onNavigateRequestPresence = { id ->
                                navController.navigate(Screen.RequesterPresence.createRoute(id))
                            },
                            onNavigateValidateRequestPresence = { id ->
                                navController.navigate(Screen.ValidateRequesterPresence.createRoute(id))
                            },
                            {id->
                                navController.navigate(Screen.ListPresencesInEvent.createRoute(id))
                            },
                            {
                                navController.popBackStack()
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
                    EventListScreen(
                        apolloClient = apolloClient,
                        onClickRegister = {
                            navController.navigate("register_event")
                        },
                        onClickUpdate = { eventId ->
                            navController.navigate("register_event/$eventId")
                        }
                    )
                },
                tokenProvider
            )
        }

        // CRIAÇÃO DE EVENTO
        composable(route = "register_event") {
            val scope = rememberCoroutineScope()
            val context = LocalContext.current

            ContentPage(
                navController,
                {
                    RegisterEvent(
                        onVoltar = {
                            navController.popBackStack()
                        },
                        onSalvar = { dados ->
                            scope.launch {
                                try {
                                    val input = dados.toEventInputCreate()
                                    val eventoCriado = eventRemoteDataSource.createEvent(input)

                                    if (eventoCriado != null) {
                                        Toast.makeText(
                                            context,
                                            "Evento criado com sucesso!",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        navController.popBackStack()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Não foi possível criar o evento. Verifique os dados e tente novamente.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Erro ao criar evento: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    )
                },
                tokenProvider
            )
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
            val eventId = backStackEntry.arguments?.getString("event_id")
            val scope = rememberCoroutineScope()
            val context = LocalContext.current

            if (eventId != null) {
                var dadosEvento by remember(eventId) {
                    mutableStateOf<EventFormData?>(null)
                }

                var carregando by remember(eventId) {
                    mutableStateOf(true)
                }

                LaunchedEffect(eventId) {
                    carregando = true
                    val evento = eventRemoteDataSource.fetchEventById(eventId)
                    dadosEvento = evento?.toEventFormData()
                    carregando = false
                }

                ContentPage(
                    navController,
                    {
                        when {
                            carregando -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = PRIMARY)
                                }
                            }

                            dadosEvento != null -> {
                                RegisterEvent(
                                    eventId = eventId,
                                    dadosIniciais = dadosEvento,
                                    onVoltar = {
                                        navController.popBackStack()
                                    },
                                    onSalvar = { dados ->
                                        scope.launch {
                                            try {
                                                val input = dados.toEventInputUpdate(eventId)
                                                val eventoAtualizado = eventRemoteDataSource.updateEvent(input)

                                                if (eventoAtualizado != null) {
                                                    Toast.makeText(
                                                        context,
                                                        "Evento atualizado com sucesso!",
                                                        Toast.LENGTH_LONG
                                                    ).show()

                                                    navController.popBackStack()
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Não foi possível atualizar o evento.",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(
                                                    context,
                                                    "Erro ao atualizar evento: ${e.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    }
                                )
                            }

                            else -> {
                                Text("Não foi possível carregar o evento.")
                            }
                        }
                    },
                    tokenProvider
                )
            }
        }

        // PAGINA ADM
        composable("pagina_admin") {
            PaginaAdmin(
                onGerenciarUsuarios = {
                    navController.navigate("gerenciar_usuarios")
                },
                onCadastrarUsuario = {
                    navController.navigate("cadastro_adm")
                },
                onGerenciarEventos = {
                    navController.navigate("gerenciar_eventos")
                },
                onVoltar = {
                    navController.popBackStack()
                }
            )
        }

        // CADASTRO ADM
        composable("cadastro_adm") {
            CadastroAdm(
                onVoltar = {
                    navController.popBackStack()
                },
                tokenProvider = tokenProvider
            )
        }

        // GERENCIAR USUARIOS
        composable("gerenciar_usuarios") {
            GerenciarUsuarios(
                onVoltar = {
                    navController.popBackStack()
                }
            )
        }

        // GERENCIAR EVENTOS
        composable("gerenciar_eventos") {
            val scope = rememberCoroutineScope()
            val context = LocalContext.current

            var atualizarLista by remember {
                mutableStateOf(0)
            }

            ContentPage(
                navController,
                {
                    GerenciarEventos(
                        tokenProvider = tokenProvider,
                        atualizarLista = atualizarLista,
                        onEditarEvento = { eventId ->
                            navController.navigate("register_event/$eventId")
                        },
                        onDeletarEvento = { eventId ->
                            scope.launch {
                                val resultado = eventRemoteDataSource.deleteEvent(eventId)

                                if (resultado != null) {
                                    Toast.makeText(
                                        context,
                                        "Evento excluído com sucesso!",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    atualizarLista++
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Não foi possível excluir o evento.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    )
                },
                tokenProvider
            )
        }

        // MEUS CERTIFICADOS
        composable("meus_certificados") {
            ContentPage(
                navController,
                {
                    MeusCertificados()
                },
                tokenProvider
            )
        }

        // EVENTOS QUE PARTICIPO
        composable("eventos_que_participo") {
            ContentPage(
                navController,
                {
                    EventosQueParticipo()
                },
                tokenProvider
            )
        }

        // REQUESTER PRESENCE
        composable(
            route = Screen.RequesterPresence.route,
            arguments = listOf(
                navArgument("event_id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("event_id")

            if (eventId != null) {
                ContentPage(
                    navController,
                    {
                        RequesterPresenceScreen(
                            tokenProvider = tokenProvider,
                            onClickReturn = {
                                navController.popBackStack()
                            },
                            event_id = eventId
                        )
                    },
                    tokenProvider
                )
            }
        }

        // VALIDATE PRESENCE
        composable(
            route = Screen.ValidateRequesterPresence.route,
            arguments = listOf(
                navArgument("event_id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("event_id")

            if (eventId != null) {
                ContentPage(
                    navController,
                    {
                        ValidateRequesterPresenceScreen(
                            tokenProvider = tokenProvider,
                            onClickReturn = {
                                navController.popBackStack()
                            },
                            event_id = eventId
                        )
                    },
                    tokenProvider
                )
            }
        }

        //list of presences in event
        composable(
            Screen.ListPresencesInEvent.route,
            arguments = listOf(
                navArgument("event_id"){
                    type = NavType.StringType
                }
            )
        ) {
            backStackEntry->
                val eventId = backStackEntry.arguments?.getString("event_id")
                if(eventId != null){
                    ContentPage(
                        navController,
                        {
                            EventListPresenceScreen(
                                eventId,
                                tokenProvider,
                                {
                                    navController.popBackStack()
                                }
                            )
                        },
                        tokenProvider
                    )
                }
        }

        composable(
            route = Screen.MyEvents.route,
        ) {
            backStackEntry->
            ContentPage(
                navController,
                {
                    MyEventsScreen(
                        {
                            navController.popBackStack()
                        },
                        tokenProvider,
                        {
//                            event_id->///
                        },
                        {
                            event_id->navController.navigate(Screen.EventDetails.createRoute(event_id))
                        }
                    )
                },
                tokenProvider
            )
        }
    }
}
