package com.medisupplyg4.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.medisupplyg4.models.OrderUI
import com.medisupplyg4.repositories.ClientOrdersRepository
import com.medisupplyg4.utils.SessionManager
import kotlinx.coroutines.launch
import java.time.LocalDate

class ClientOrdersViewModel(application: Application) : AndroidViewModel(application) {

    companion object { private const val TAG = "ClientOrdersVM" }

    private val repository = ClientOrdersRepository()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _orders = MutableLiveData<List<OrderUI>>(emptyList())
    val orders: LiveData<List<OrderUI>> = _orders

    private val _filteredOrders = MutableLiveData<List<OrderUI>>(emptyList())
    val filteredOrders: LiveData<List<OrderUI>> = _filteredOrders

    private val _selectedStartDate = MutableLiveData<LocalDate?>(null)
    val selectedStartDate: LiveData<LocalDate?> = _selectedStartDate

    private val _selectedEndDate = MutableLiveData<LocalDate?>(null)
    val selectedEndDate: LiveData<LocalDate?> = _selectedEndDate

    init {
        loadOrders()
    }

    fun loadOrders(page: Int = 1, pageSize: Int = 20) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = SessionManager.getToken(getApplication()) ?: ""
                val clienteId = SessionManager.getUserId(getApplication()) ?: ""
                val result = repository.getPedidosCliente(token, clienteId, page, pageSize)
                if (result.isSuccess) {
                    _orders.value = result.getOrNull()?.sortedByDescending { it.createdAt } ?: emptyList()
                } else {
                    Log.w(TAG, "Fallo cargando pedidos desde API: ${result.exceptionOrNull()?.message}")
                    _orders.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error cargando pedidos", e)
                _orders.value = emptyList()
            } finally {
                applyDateFilter()
                _isLoading.value = false
            }
        }
    }

    fun setDate(date: LocalDate?) {
        _selectedStartDate.value = date
        _selectedEndDate.value = null
        applyDateFilter()
    }

    fun setDateRange(start: LocalDate?, end: LocalDate?) {
        _selectedStartDate.value = start
        _selectedEndDate.value = end
        applyDateFilter()
    }

    private fun applyDateFilter() {
        val list = _orders.value ?: emptyList()
        val start = _selectedStartDate.value
        val end = _selectedEndDate.value
        val filtered = when {
            start == null && end == null -> list
            start != null && end == null -> list.filter { !it.createdAt.isBefore(start) }
            start != null && end != null -> list.filter { !it.createdAt.isBefore(start) && !it.createdAt.isAfter(end) }
            else -> list
        }
        _filteredOrders.value = filtered.sortedByDescending { it.createdAt }
    }
}
