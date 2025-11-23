package com.medisupplyg4.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.medisupplyg4.R
import com.medisupplyg4.models.ClienteAPI
import com.medisupplyg4.models.OrderUI
import com.medisupplyg4.repositories.ClientesRepository
import com.medisupplyg4.repositories.SellerOrdersRepository
import com.medisupplyg4.utils.SessionManager
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

class SellerOrdersViewModel(application: Application) : AndroidViewModel(application) {

    companion object { private const val TAG = "SellerOrdersVM" }

    private val repository = SellerOrdersRepository()
    private val clientesRepository = ClientesRepository()

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

    // Detalle del pedido
    private val _orderDetail = MutableLiveData<OrderUI?>()
    val orderDetail: LiveData<OrderUI?> = _orderDetail

    private val _isLoadingDetail = MutableLiveData(false)
    val isLoadingDetail: LiveData<Boolean> = _isLoadingDetail

    // Cliente del pedido
    private val _cliente = MutableLiveData<ClienteAPI?>()
    val cliente: LiveData<ClienteAPI?> = _cliente

    private val _isLoadingCliente = MutableLiveData(false)
    val isLoadingCliente: LiveData<Boolean> = _isLoadingCliente

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        // Establecer filtro por defecto: semana actual (lunes a domingo)
        setDefaultWeekFilter()
        loadOrders()
    }

    /**
     * Establece el filtro por defecto a la semana actual (lunes a domingo)
     */
    private fun setDefaultWeekFilter() {
        val today = LocalDate.now()
        val monday = today.with(DayOfWeek.MONDAY)
        val sunday = today.with(DayOfWeek.SUNDAY)
        _selectedStartDate.value = monday
        _selectedEndDate.value = sunday
    }

    fun loadOrders(page: Int = 1, pageSize: Int = 20) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = SessionManager.getToken(getApplication()) ?: ""
                val vendedorId = SessionManager.getUserId(getApplication()) ?: ""
                val orderPrefix = getApplication<Application>().getString(R.string.order_number_prefix)
                val result = repository.getPedidosVendedor(token, vendedorId, orderPrefix, page, pageSize)
                if (result.isSuccess) {
                    _orders.value = result.getOrNull()?.sortedBy { it.createdAt } ?: emptyList()
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

    /**
     * Limpia el filtro de fechas y restablece el filtro por defecto de la semana actual
     */
    fun clearDateFilter() {
        setDefaultWeekFilter()
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
        _filteredOrders.value = filtered.sortedBy { it.createdAt }
    }

    /**
     * Carga el detalle de un pedido por ID
     */
    fun loadOrderDetail(pedidoId: String) {
        viewModelScope.launch {
            _isLoadingDetail.value = true
            _error.value = null
            try {
                val token = SessionManager.getToken(getApplication()) ?: ""
                val orderPrefix = getApplication<Application>().getString(R.string.order_number_prefix)
                val result = repository.getPedidoDetail(token, pedidoId, orderPrefix)
                if (result.isSuccess) {
                    val (order, clienteId) = result.getOrNull() ?: return@launch
                    _orderDetail.value = order
                    // Cargar información del cliente
                    loadCliente(clienteId)
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Error al cargar el detalle del pedido"
                    _error.value = errorMsg
                    Log.e(TAG, "Error cargando detalle del pedido: $errorMsg")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción cargando detalle del pedido", e)
                _error.value = e.message ?: "Error desconocido"
            } finally {
                _isLoadingDetail.value = false
            }
        }
    }

    /**
     * Carga la información del cliente por ID
     */
    fun loadCliente(clienteId: String) {
        viewModelScope.launch {
            _isLoadingCliente.value = true
            try {
                val token = SessionManager.getToken(getApplication()) ?: ""
                val result = clientesRepository.getClienteById(token, clienteId)
                if (result.isSuccess) {
                    _cliente.value = result.getOrNull()
                } else {
                    Log.e(TAG, "Error cargando cliente: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción cargando cliente", e)
            } finally {
                _isLoadingCliente.value = false
            }
        }
    }
}

