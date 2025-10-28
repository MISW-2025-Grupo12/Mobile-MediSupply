package com.medisupplyg4.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.medisupplyg4.R
import com.medisupplyg4.models.ClienteAPI
import com.medisupplyg4.repositories.ClientesRepository
import kotlinx.coroutines.launch

class ClientesViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "ClientesViewModel"
    }

    private val repository = ClientesRepository()

    // Estados de la UI
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _clientes = MutableLiveData<List<ClienteAPI>>()
    val clientes: LiveData<List<ClienteAPI>> = _clientes

    private val _filteredClientes = MutableLiveData<List<ClienteAPI>>()
    val filteredClientes: LiveData<List<ClienteAPI>> = _filteredClientes

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    // Filtros
    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    private val _selectedStatus = MutableLiveData<String>()
    val selectedStatus: LiveData<String> = _selectedStatus

    // Valores internos del API (no localizados)
    private val estadosInternos = listOf("TODOS", "ACTIVO", "INACTIVO")

    init {
        _searchQuery.value = ""
        _selectedStatus.value = "TODOS" // Valor interno del API
    }

    fun loadClientes(token: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Log.d(TAG, "Cargando clientes")
                val result = repository.getClientes(token, getApplication())

                if (result.isSuccess) {
                    val clientesList = result.getOrNull() ?: emptyList()
                    _clientes.value = clientesList.sortedBy { it.nombre } // Ordenar alfabéticamente
                    applyFilters()
                    Log.d(TAG, "Clientes cargados exitosamente: ${clientesList.size} clientes")
                } else {
                    Log.e(TAG, "Error al cargar clientes: ${result.exceptionOrNull()?.message}")
                    _error.value = result.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.error_unknown)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción al cargar clientes", e)
                _error.value = getApplication<Application>().getString(R.string.error_connection_error, e.message ?: "")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query.trim()
        applyFilters()
    }

    fun updateStatusFilter(status: String) {
        _selectedStatus.value = status
        applyFilters()
    }

    private fun applyFilters() {
        val allClientes = _clientes.value ?: emptyList()
        val query = _searchQuery.value ?: ""
        val internalStatus = _selectedStatus.value ?: "TODOS"

        Log.d(TAG, "Aplicando filtros - InternalStatus: '$internalStatus'")

        val filtered = allClientes.filter { cliente ->
            val matchesSearch = if (query.isBlank()) {
                true
            } else {
                cliente.nombre.contains(query, ignoreCase = true) ||
                cliente.identificacion.contains(query, ignoreCase = true)
            }

            val matchesStatus = if (internalStatus == "TODOS") {
                true
            } else {
                val matches = cliente.estado.equals(internalStatus, ignoreCase = true)
                Log.d(TAG, "Cliente: ${cliente.nombre}, Estado: '${cliente.estado}', Matches: $matches")
                matches
            }

            matchesSearch && matchesStatus
        }

        Log.d(TAG, "Filtros aplicados - Total: ${allClientes.size}, Filtrados: ${filtered.size}")
        _filteredClientes.value = filtered.sortedBy { it.nombre }
        _isEmpty.value = filtered.isEmpty()
    }

    fun refreshClientes(token: String) {
        loadClientes(token)
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSearch() {
        _searchQuery.value = ""
        applyFilters()
    }

    fun clearStatusFilter() {
        _selectedStatus.value = "TODOS"
        applyFilters()
    }
}
