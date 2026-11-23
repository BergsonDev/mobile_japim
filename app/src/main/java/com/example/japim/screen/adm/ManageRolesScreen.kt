import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.japim.graphql.RolesQuery
import com.example.japim.viewmodel.RoleViewModel
import com.example.japim.graphql.PermissionsQuery
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


@Composable
fun ManageRolesScreen(
    viewModel: RoleViewModel
) {
    val roles by viewModel.roles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var search by remember { mutableStateOf("") }

    var showCreateDialog by remember {
        mutableStateOf(false)
    }

    var editingRole by remember {
        mutableStateOf<RolesQuery.Role?>(null)
    }

    var deletingRole by remember {
        mutableStateOf<RolesQuery.Role?>(null)
    }

    var managingRoleId by remember {
        mutableStateOf<String?>(null)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error, successMessage) {
        val message = error ?: successMessage

        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessages()
        }
    }

    val filteredRoles = roles.filter {
        it.name.contains(search, ignoreCase = true) ||
                it.initialism.contains(search, ignoreCase = true)
    }

    val permissions by viewModel.permissions.collectAsState()

    Scaffold(
        containerColor = Color.Black,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                text = "Gerenciar Cargos",
                color = Color.Yellow,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = {
                    Text("Pesquisar cargos...")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.Yellow,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color.Yellow,
                    unfocusedLabelColor = Color.LightGray,
                    cursorColor = Color.Yellow
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { showCreateDialog = true },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text(
                    text = "+ Criar novo cargo",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Yellow
                )
            }

            when {
                isLoading && roles.isEmpty() -> {
                    // Aguarda o carregamento
                }

                error != null && roles.isEmpty() -> {
                    Text(
                        text = "Não foi possível carregar os cargos.",
                        color = Color.Red
                    )

                    TextButton(
                        onClick = { viewModel.loadRoles() },
                        enabled = !isLoading
                    ) {
                        Text("Tentar novamente")
                    }
                }

                roles.isEmpty() -> {
                    Text(
                        text = "Nenhum cargo cadastrado.",
                        color = Color.White
                    )
                }

                filteredRoles.isEmpty() -> {
                    Text(
                        text = "Nenhum cargo encontrado.",
                        color = Color.White
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(
                            items = filteredRoles,
                            key = { it.id }
                        ) { role ->

                            RoleCard(
                                role = role,
                                enabled = !isLoading,
                                onEdit = {
                                    editingRole = role
                                },
                                onDelete = {
                                    deletingRole = role
                                },
                                onManagePermissions = {
                                    managingRoleId = role.id
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        RoleFormDialog(
            title = "Criar cargo",
            onDismiss = {
                showCreateDialog = false
            },
            onConfirm = { name, initialism ->
                viewModel.createRole(name, initialism)
                showCreateDialog = false
            }
        )
    }

    editingRole?.let { role ->

        RoleFormDialog(
            title = "Editar cargo",
            initialName = role.name,
            initialInitialism = role.initialism,
            onDismiss = {
                editingRole = null
            },
            onConfirm = { name, initialism ->
                viewModel.updateRole(
                    id = role.id,
                    name = name,
                    initialism = initialism
                )

                editingRole = null
            }
        )
    }

    deletingRole?.let { role ->

        AlertDialog(
            onDismissRequest = {
                deletingRole = null
            },
            title = {
                Text("Excluir cargo")
            },
            text = {
                Text(
                    "Deseja realmente excluir o cargo ${role.name}?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRole(role.id)
                        deletingRole = null
                    }
                ) {
                    Text(
                        text = "Excluir",
                        color = Color.Red
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        deletingRole = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    managingRoleId?.let { roleId ->

        val role = roles.find { it.id == roleId }

        if (role != null) {

            RolePermissionsDialog(
                role = role,
                permissions = permissions,
                isLoading = isLoading,

                onDismiss = {
                    managingRoleId = null
                },

                onAddPermission = { permissionId ->
                    viewModel.addPermissions(
                        roleId = role.id,
                        permissionIds = listOf(permissionId)
                    )
                },

                onRemovePermission = { permissionId ->
                    viewModel.removePermissions(
                        roleId = role.id,
                        permissionIds = listOf(permissionId)
                    )
                },

                onReload = {
                    viewModel.loadPermissions()
                }
            )
        }
    }
}

@Composable
private fun RoleCard(
    role: RolesQuery.Role,
    enabled: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onManagePermissions: () -> Unit

) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Yellow
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = role.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Sigla: ${role.initialism}",
                color = Color.Black
            )

            Text(
                text = "Permissões: ${role.permissions.filterNotNull().size}",
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = onEdit,
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text(
                        text = "Editar",
                        color = Color.White
                    )
                }

                Button(
                    onClick = onDelete,
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text(
                        text = "Excluir",
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onManagePermissions,
                    enabled = enabled,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0)
                    )
                ) {
                    Text(
                        text = "Gerenciar permissões",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleFormDialog(
    title: String,
    initialName: String = "",
    initialInitialism: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {

    var name by remember {
        mutableStateOf(initialName)
    }

    var initialism by remember {
        mutableStateOf(initialInitialism)
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(title)
        },

        text = {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text("Nome do cargo")
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = initialism,
                    onValueChange = { initialism = it },
                    label = {
                        Text("Sigla do cargo")
                    },
                    singleLine = true
                )
            }
        },

        confirmButton = {

            TextButton(
                enabled = name.isNotBlank() &&
                        initialism.isNotBlank(),

                onClick = {
                    onConfirm(
                        name.trim(),
                        initialism.trim()
                    )
                }
            ) {
                Text("Salvar")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun RolePermissionsDialog(
    role: RolesQuery.Role,
    permissions: List<PermissionsQuery.Permission>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onAddPermission: (String) -> Unit,
    onRemovePermission: (String) -> Unit,
    onReload: () -> Unit
) {

    val currentPermissions = role.permissions
        .filterNotNull()
        .map { it.id }
        .toSet()

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Permissões: ${role.name}")
        },

        text = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                Text(
                    text = "Selecione as permissões deste cargo.",
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (permissions.isEmpty() && !isLoading) {

                    Text("Nenhuma permissão disponível.")

                    TextButton(onClick = onReload) {
                        Text("Recarregar permissões")
                    }
                }

                permissions.forEach { permission ->

                    val isChecked =
                        permission.id in currentPermissions

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked = isChecked,
                            enabled = !isLoading,
                            onCheckedChange = { checked ->

                                if (checked) {
                                    onAddPermission(permission.id)
                                } else {
                                    onRemovePermission(permission.id)
                                }
                            }
                        )

                        Text(
                            text = permission.name,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider()
                }
            }
        },

        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Concluir")
            }
        }
    )
}