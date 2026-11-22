package com.example.japim.navigation

import ManageRolesScreen
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
import com.example.japim.remoteDataSource.EventRemoteDataSource
import com.example.japim.screen.events.EventDetailScreen
import com.example.japim.screen.events.EventListScreen
import com.example.japim.ui.components.GenerateHeader
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.japim.screen.adm.GerenciarUsuarios
import com.example.japim.screen.Cadastro
import com.example.japim.EventFormData
import com.example.japim.screen.events.EventosQueParticipo
import com.example.japim.GerenciarEventos
import com.example.japim.screen.Login
import com.example.japim.screen.certificates.MeusCertificados
import com.example.japim.screen.adm.PaginaAdmin
import com.example.japim.RegisterEvent
import com.example.japim.screen.home.StarterPage
import com.example.japim.apolloClient.TokenManager
import com.example.japim.apolloClient.generateApolloClient
import com.example.japim.toEventFormData
import com.example.japim.toEventInputCreate
import com.example.japim.toEventInputUpdate
import com.example.japim.remoteDataSource.CertificateRemoteDataSource
import com.example.japim.repository.CertificateRepository
import com.example.japim.viewmodel.CertificateViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.navArgument
import androidx.compose.runtime.collectAsState
import com.example.japim.screen.certificates.CertificateDetailScreen
import com.example.japim.util.rememberCertificateDownload
import com.example.japim.remoteDataSource.RoleRemoteDataSource
import com.example.japim.repository.RoleRepository
import com.example.japim.viewmodel.RoleViewModel
import com.example.japim.repository.EventRepository
import com.example.japim.viewmodel.EventViewModel
import com.example.japim.remoteDataSource.UserRemoteDataSource
import com.example.japim.repository.UserRepository
import com.example.japim.viewmodel.UserViewModel



@Composable
private fun ContentPage(
    navController: NavController,
    content: @Composable () -> Unit
) {
    GenerateHeader(
        onNavigateToEvents = {
            navController.navigate(Screen.Events.route)
        },
        onNavigateToAdmin = {
            navController.navigate(Screen.Admin.route)
        },
        onNavigateToCertificates = {
            navController.navigate(Screen.MyCertificates.route)
        },
        onNavigateToParticipatingEvents = {
            navController.navigate(Screen.ParticipatingEvents.route)
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
        startDestination = "login"
    ) {

        // LOGIN
        composable("login") {
            Login(
                tokenProvider = tokenProvider,
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
        }

        // CADASTRO
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

            val eventId = backStackEntry.arguments
                ?.getString("event_id")

            if (eventId != null) {
                ContentPage(navController) {
                    EventDetailScreen(
                        tokenProvider = tokenProvider,
                        event_id = eventId
                    )
                }
            }
        }

        // LISTA DE EVENTOS
        composable(Screen.Events.route) {

            val userViewModel: UserViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        val remoteDataSource =
                            UserRemoteDataSource(apolloClient)

                        val repository =
                            UserRepository(remoteDataSource)

                        UserViewModel(repository)
                    }
                }
            )

            val eventViewModel: EventViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        val remoteDataSource =
                            EventRemoteDataSource(apolloClient)

                        val repository =
                            EventRepository(remoteDataSource)

                        EventViewModel(repository)
                    }
                }
            )


            val currentUser by userViewModel.currentUser.collectAsState()
            val isUserLoading by userViewModel.isLoading.collectAsState()
            val userError by userViewModel.error.collectAsState()


            LaunchedEffect(currentUser, userError) {
                android.util.Log.d(
                    "JapimUser",
                    "Usuário: ${currentUser?.name}, " +
                            "Cargo: ${currentUser?.role?.initialism}, " +
                            "Permissões: ${currentUser?.role?.permissions?.mapNotNull { it?.name }}, " +
                            "Erro: $userError"
                )
            }


            val userRole = currentUser?.role?.initialism?.uppercase()

            val canCreateEvent = userViewModel.hasPermission("events-create") &&
                    userRole in setOf("ADMIN", "COORD", "DIR", "SPE")

            val canUpdateEvent = userViewModel.hasPermission("events-update") &&
                    userRole in setOf("ADMIN", "COORD", "DIR", "SPE")

            val canDeleteEvent = userViewModel.hasPermission("events-delete") &&
                    userRole in setOf("ADMIN", "COORD", "DIR", "SPE")


            ContentPage(navController) {
                EventListScreen(
                    viewModel = eventViewModel,

                    onClickRegister = {
                        navController.navigate(
                            Screen.RegisterEvent.route
                        )
                    },

                    onClickUpdate = { eventId ->
                        navController.navigate(
                            Screen.UpdateEvent.createRoute(eventId)
                        )
                    },

                    onClickDetails = { eventId ->
                        navController.navigate(
                            Screen.EventDetails.createRoute(eventId)
                        )
                    },

                    canCreate = canCreateEvent,
                    canUpdate = canUpdateEvent,
                    canDelete = canDeleteEvent,
                    currentUserId = currentUser?.id,
                    currentUserRole = userRole
                )
            }
        }


// CRIAÇÃO DE EVENTO
        composable(route = Screen.RegisterEvent.route) {
            val scope = rememberCoroutineScope()
            val context = LocalContext.current

            ContentPage(navController) {
                RegisterEvent(
                    onVoltar = {
                        navController.popBackStack()
                    },
                    onSalvar = { dados ->
                        scope.launch {
                            try {
                                val input = dados.toEventInputCreate()

                                val eventoCriado =
                                    eventRemoteDataSource.createEvent(input)

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
            }
        }

        // EDIÇÃO DE EVENTO
        composable(
            route = Screen.UpdateEvent.route,
            arguments = listOf(
                navArgument("event_id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val eventId = backStackEntry.arguments
                ?.getString("event_id")
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
                                    scope.launch {
                                        try {
                                            val input = dados.toEventInputUpdate(eventId)

                                            val eventoAtualizado =
                                                eventRemoteDataSource.updateEvent(input)

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
                            Text(
                                "Não foi possível carregar o evento."
                            )
                        }
                    }
                }
            }
        }
        ////PAGINA ADM
        composable(Screen.Admin.route) {
            ContentPage(navController) {
                PaginaAdmin(
                    onGerenciarUsuarios = {
                        navController.navigate(
                            Screen.ManageUsers.route
                        )
                    },
                    onGerenciarCargos = {
                        navController.navigate(
                            Screen.ManageRoles.route
                        )
                    },
                    onVoltar = {
                        navController.popBackStack()
                    }
                )
            }
        }
        ////CADASTRO COM CARGO
        composable(Screen.RegisterUser.route) {

            val roleViewModel: RoleViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        val remoteDataSource =
                            RoleRemoteDataSource(apolloClient)

                        val repository =
                            RoleRepository(remoteDataSource)

                        RoleViewModel(repository)
                    }
                }
            )

            ContentPage(navController) {
                Cadastro(
                    tokenProvider = tokenProvider,
                    isAdminRegistration = true,
                    roleViewModel = roleViewModel,
                    onVoltarLogin = {
                        navController.popBackStack()
                    }
                )
            }
        }
        ////GERENCIAR USUARIOS
        composable(Screen.ManageUsers.route) {
            ContentPage(navController) {
                GerenciarUsuarios(
                    onCadastrarUsuario = {
                        navController.navigate(
                            Screen.RegisterUser.route
                        )
                    },
                    onVoltar = {
                        navController.popBackStack()
                    }
                )
            }
        }


////GERENCIAR EVENTOS
        composable(Screen.ManageEvents.route) {
            val scope = rememberCoroutineScope()
            val context = LocalContext.current

            var atualizarLista by remember {
                mutableStateOf(0)
            }

            ContentPage(navController) {
                GerenciarEventos(
                    tokenProvider = tokenProvider,
                    atualizarLista = atualizarLista,
                    onEditarEvento = { eventId ->
                        navController.navigate(Screen.UpdateEvent.createRoute(eventId))
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
            }
        }
////Gerencia cargos
        composable(Screen.ManageRoles.route) {

            val roleViewModel: RoleViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        val remoteDataSource =
                            RoleRemoteDataSource(apolloClient)

                        val repository =
                            RoleRepository(remoteDataSource)

                        RoleViewModel(repository)
                    }
                }
            )

            ContentPage(navController) {
                ManageRolesScreen(
                    viewModel = roleViewModel
                )
            }
        }

        ////MEUS CERTIFICADOS
        composable(Screen.MyCertificates.route) {

            val certificateViewModel: CertificateViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        val remoteDataSource =
                            CertificateRemoteDataSource(apolloClient)

                        val repository =
                            CertificateRepository(remoteDataSource)

                        CertificateViewModel(repository)
                    }
                }
            )

            val downloadCertificate = rememberCertificateDownload()

            val certificates =
                certificateViewModel.certificates.collectAsState().value

            ContentPage(navController) {
                MeusCertificados(
                    viewModel = certificateViewModel,

                    onBaixarCertificado = { certificateId ->
                        certificates.find {
                            it.id == certificateId
                        }?.let { certificate ->
                            downloadCertificate(certificate)
                        }
                    },

                    onVerCertificado = { certificateId ->
                        navController.navigate(
                            Screen.CertificateDetails.createRoute(
                                certificateId
                            )
                        )
                    }
                )
            }
        }
        composable(
            route = Screen.CertificateDetails.route,
            arguments = listOf(
                navArgument("certificate_id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val certificateId =
                backStackEntry.arguments?.getString("certificate_id")

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(
                    Screen.MyCertificates.route
                )
            }

            val certificateViewModel: CertificateViewModel = viewModel(
                viewModelStoreOwner = parentEntry
            )

            val certificates =
                certificateViewModel.certificates.collectAsState().value

            val isLoading =
                certificateViewModel.isLoading.collectAsState().value

            val certificate = certificates.find {
                it.id == certificateId
            }
            val downloadCertificate = rememberCertificateDownload()

            ContentPage(navController) {

                when {
                    isLoading -> {
                        CircularProgressIndicator()
                    }

                    certificate != null -> {
                        CertificateDetailScreen(
                            certificate = certificate,
                            onBack = {
                                navController.popBackStack()
                            },
                            onDownload = {
                                downloadCertificate(certificate)
                            }
                        )
                    }

                    else -> {
                        Text("Certificado não encontrado.")
                    }
                }
            }
        }

        //// EVENTOS QUE PARTICIPO
        composable(Screen.ParticipatingEvents.route) {
            ContentPage(navController) {
                EventosQueParticipo()
            }
        }
    }
}

