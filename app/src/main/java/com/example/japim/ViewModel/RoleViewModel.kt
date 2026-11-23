package com.example.japim.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Optional
import com.example.japim.graphql.RolesQuery
import com.example.japim.graphql.PermissionsQuery
import com.example.japim.graphql.type.InputRoleCreate
import com.example.japim.graphql.type.InputRoleUpdate
import com.example.japim.graphql.type.InputRolePermission
import com.example.japim.repository.RoleRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoleViewModel(
    private val repository: RoleRepository
) : ViewModel() {

    private val _roles =
        MutableStateFlow<List<RolesQuery.Role>>(emptyList())
    val roles = _roles.asStateFlow()

    private val _permissions =
        MutableStateFlow<List<PermissionsQuery.Permission>>(emptyList())
    val permissions = _permissions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage = _successMessage.asStateFlow()

    init {
        loadRolesAndPermissions()
    }

    fun loadRolesAndPermissions() {
        executeOperation {
            _roles.value = repository.getRoles()
            _permissions.value = repository.getPermissions()
        }
    }

    fun loadRoles() {
        executeOperation {
            _roles.value = repository.getRoles()
        }
    }

    fun loadPermissions() {
        executeOperation {
            _permissions.value = repository.getPermissions()
        }
    }

    fun createRole(
        name: String,
        initialism: String,
        description: String? = null
    ) {
        executeOperation {
            val input = InputRoleCreate(
                name = name,
                initialism = initialism,
                description = Optional.presentIfNotNull(description)
            )

            repository.createRole(input)

            _successMessage.value = "Cargo criado com sucesso!"
            _roles.value = repository.getRoles()
        }
    }

    fun updateRole(
        id: String,
        name: String,
        initialism: String
    ) {
        executeOperation {
            val input = InputRoleUpdate(
                id = id,
                name = name,
                initialism = initialism,
                description = Optional.Absent
            )

            repository.updateRole(input)

            _successMessage.value = "Cargo atualizado com sucesso!"
            _roles.value = repository.getRoles()
        }
    }

    fun deleteRole(id: String) {
        executeOperation {
            repository.deleteRole(id)

            _successMessage.value = "Cargo excluído com sucesso!"
            _roles.value = repository.getRoles()
        }
    }

    fun addPermissions(
        roleId: String,
        permissionIds: List<String>
    ) {
        if (permissionIds.isEmpty()) return

        executeOperation {
            val input = InputRolePermission(
                id = roleId,
                permissions_id = permissionIds
            )

            repository.addPermission(input)

            _successMessage.value = "Permissões adicionadas!"
            _roles.value = repository.getRoles()
        }
    }

    fun removePermissions(
        roleId: String,
        permissionIds: List<String>
    ) {
        if (permissionIds.isEmpty()) return

        executeOperation {
            val input = InputRolePermission(
                id = roleId,
                permissions_id = permissionIds
            )

            repository.removePermission(input)

            _successMessage.value = "Permissões removidas!"
            _roles.value = repository.getRoles()
        }
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }

    private fun executeOperation(
        operation: suspend () -> Unit
    ) {
        if (_isLoading.value) return

        _isLoading.value = true
        _error.value = null
        _successMessage.value = null

        viewModelScope.launch {
            try {
                operation()

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _error.value =
                    e.message ?: "Erro ao realizar operação."

            } finally {
                _isLoading.value = false
            }
        }
    }
}