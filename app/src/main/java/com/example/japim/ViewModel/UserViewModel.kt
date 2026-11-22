
package com.example.japim.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.japim.graphql.MeQuery
import com.example.japim.repository.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _currentUser =
        MutableStateFlow<MeQuery.Me?>(null)

    val currentUser: StateFlow<MeQuery.Me?> =
        _currentUser.asStateFlow()

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error.asStateFlow()

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                _currentUser.value =
                    repository.getCurrentUser()

            } catch (exception: CancellationException) {
                throw exception

            } catch (exception: Exception) {
                _currentUser.value = null
                _error.value =
                    exception.message
                        ?: "Erro ao carregar o usuário."

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun hasPermission(permission: String): Boolean {
        return _currentUser.value
            ?.role
            ?.permissions
            ?.any { it?.name == permission }
            ?: false
    }
    fun isRole(role: String): Boolean {
        return _currentUser.value
            ?.role
            ?.initialism
            ?.equals(role, ignoreCase = true)
            ?: false
    }

    fun isEventOwner(createdById: String): Boolean {
        return _currentUser.value?.id == createdById
    }
}
