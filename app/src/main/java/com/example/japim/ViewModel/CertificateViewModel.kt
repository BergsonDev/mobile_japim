package com.example.japim.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.japim.graphql.MyCertificatesQuery
import com.example.japim.repository.CertificateRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CertificateViewModel(
    private val repository: CertificateRepository
) : ViewModel() {

    private val _certificates =
        MutableStateFlow<List<MyCertificatesQuery.MyCertificate>>(emptyList())

    val certificates = _certificates.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadCertificates()
    }

    fun loadCertificates() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                _certificates.value = repository.getMyCertificates()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading certificates"
            } finally {
                _isLoading.value = false
            }
        }
    }
}