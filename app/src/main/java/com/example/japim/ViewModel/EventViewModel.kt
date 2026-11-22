package com.example.japim.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.japim.graphql.GetEventsQuery
import com.example.japim.repository.EventRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventViewModel(
    private val repository: EventRepository
) : ViewModel() {

    private val _events =
        MutableStateFlow<List<GetEventsQuery.Event>>(emptyList())

    val events = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting = _isDeleting.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage = _successMessage.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {

        if (_isLoading.value || _isDeleting.value) return

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {

                _events.value = repository.getEvents()

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                _error.value = e.message
                    ?: "Não foi possível carregar os eventos."

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteEvent(eventId: String) {

        if (_isDeleting.value || _isLoading.value) return

        _isDeleting.value = true
        _error.value = null
        _successMessage.value = null

        viewModelScope.launch {
            try {

                repository.deleteEvent(eventId)

                _events.value = _events.value.filterNot {
                    it.id == eventId
                }

                _successMessage.value =
                    "Evento excluído com sucesso!"

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                _error.value = e.message
                    ?: "Não foi possível excluir o evento."

            } finally {
                _isDeleting.value = false
            }
        }
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}