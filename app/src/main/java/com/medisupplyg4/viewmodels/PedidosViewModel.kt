package com.medisupplyg4.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.medisupplyg4.models.ClienteAPI
import com.medisupplyg4.models.ItemPedido
import com.medisupplyg4.models.PedidoCompletoRequest
import com.medisupplyg4.models.ProductoConInventario
import com.medisupplyg4.repositories.PedidosRepository
import com.medisupplyg4.utils.SessionManager
import kotlinx.coroutines.launch

/**
 * ViewModel to handle orders logic
 */
class PedidosViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "PedidosViewModel"
        private const val MAX_SEARCH_LENGTH = 100
        
        /**
         * Normalizes a string by removing accents and converting to lowercase for better search and sorting
         */
        private fun normalizeString(input: String): String {
            return input.lowercase()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("ñ", "n")
                .replace("ü", "u")
        }

        // Error messages - these should be replaced with string resources in the UI layer
        const val ERROR_CREATING_ORDER = "ERROR_CREATING_ORDER"
        const val ERROR_NETWORK_CONNECTION = "ERROR_NETWORK_CONNECTION"
        const val ERROR_CLIENT_REQUIRED = "ERROR_CLIENT_REQUIRED"
        const val ERROR_SEARCH_TOO_LONG = "ERROR_SEARCH_TOO_LONG"
        const val ERROR_NO_PRODUCTS_IN_ORDER = "ERROR_NO_PRODUCTS_IN_ORDER"
        const val ERROR_INSUFFICIENT_INVENTORY = "ERROR_INSUFFICIENT_INVENTORY"
    }

    private val repository = PedidosRepository()

    // Clients data
    private val _clientes = MutableLiveData<List<ClienteAPI>>()
    val clientes: LiveData<List<ClienteAPI>> = _clientes

    // Products with inventory data
    private val _productosConInventario = MutableLiveData<List<ProductoConInventario>>()

    // Filtered products based on search
    private val _productosFiltrados = MutableLiveData<List<ProductoConInventario>>()
    val productosFiltrados: LiveData<List<ProductoConInventario>> = _productosFiltrados

    // Search query for products
    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    // Search query for clients
    private val _clientSearchQuery = MutableLiveData("")
    val clientSearchQuery: LiveData<String> = _clientSearchQuery

    // Filtered clients based on search
    private val _clientesFiltrados = MutableLiveData<List<ClienteAPI>>()
    val clientesFiltrados: LiveData<List<ClienteAPI>> = _clientesFiltrados

    // Selected client
    private val _clienteSeleccionado = MutableLiveData<ClienteAPI?>()
    val clienteSeleccionado: LiveData<ClienteAPI?> = _clienteSeleccionado

    // Order items
    private val _itemsPedido = MutableLiveData<List<ItemPedido>>()
    val itemsPedido: LiveData<List<ItemPedido>> = _itemsPedido

    // Loading states
    private val _isLoadingClientes = MutableLiveData(false)
    val isLoadingClientes: LiveData<Boolean> = _isLoadingClientes

    private val _isLoadingProductos = MutableLiveData(false)
    val isLoadingProductos: LiveData<Boolean> = _isLoadingProductos

    private val _isCreatingOrder = MutableLiveData(false)
    val isCreatingOrder: LiveData<Boolean> = _isCreatingOrder

    // Success and error states
    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Order summary
    val totalPedido: LiveData<Double> = MutableLiveData<Double>().apply {
        value = 0.0
    }

    init {
        loadClientes()
        loadProductosConInventario()
        observeSearchQuery()
    }

    /**
     * Loads all available clients
     */
    fun loadClientes(page: Int = 1, pageSize: Int = 20) {
        viewModelScope.launch {
            try {
                _isLoadingClientes.value = true
                _error.value = null

                // Obtener token de autenticación
                val token = SessionManager.getToken(getApplication()) ?: ""
                val clientes = repository.getClientes(token, page, pageSize)
                _clientes.value = clientes ?: emptyList()
                _clientesFiltrados.value = clientes ?: emptyList()
            } catch (e: Exception) {
                Log.e(TAG, "Error loading clients", e)
                _error.value = ERROR_NETWORK_CONNECTION
            } finally {
                _isLoadingClientes.value = false
            }
        }
    }

    /**
     * Loads all available products with inventory
     */
    fun loadProductosConInventario(page: Int = 1, pageSize: Int = 20) {
        viewModelScope.launch {
            try {
                _isLoadingProductos.value = true
                _error.value = null

                // Obtener token de autenticación
                val token = SessionManager.getToken(getApplication()) ?: ""
                val productosConInventario = repository.getProductosConInventario(token, page, pageSize)
                _productosConInventario.value = productosConInventario ?: emptyList()
                
                // Apply stock filter after loading
                filterProductos(_searchQuery.value ?: "")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading products with inventory", e)
                _error.value = ERROR_NETWORK_CONNECTION
            } finally {
                _isLoadingProductos.value = false
            }
        }
    }

    /**
     * Sets the search query and filters products
     */
    fun setSearchQuery(query: String) {
        if (query.length > MAX_SEARCH_LENGTH) {
            _error.value = ERROR_SEARCH_TOO_LONG
            return
        }

        _searchQuery.value = query
        filterProductos(query)
    }

    /**
     * Sets the client search query and filters clients
     */
    fun setClientSearchQuery(query: String) {
        if (query.length > MAX_SEARCH_LENGTH) {
            _error.value = ERROR_SEARCH_TOO_LONG
            return
        }

        _clientSearchQuery.value = query
        filterClientes(query)
    }

    /**
     * Sets the selected client
     */
    fun setClienteSeleccionado(cliente: ClienteAPI?) {
        _clienteSeleccionado.value = cliente
    }

    /**
     * Adds a product to the order
     */
    fun agregarProducto(productoConInventario: ProductoConInventario) {
        val currentItems = _itemsPedido.value ?: emptyList()
        val existingItem = currentItems.find { it.producto.id == productoConInventario.id }

        if (existingItem != null) {
            // If product already exists, increment quantity if inventory allows
            if (existingItem.cantidad < productoConInventario.cantidadDisponible) {
                val updatedItems = currentItems.map { item ->
                    if (item.producto.id == productoConInventario.id) {
                        item.copy(cantidad = item.cantidad + 1)
                    } else {
                        item
                    }
                }
                _itemsPedido.value = updatedItems
            } else {
                _error.value = ERROR_INSUFFICIENT_INVENTORY
            }
        } else {
            // Add new product to order
            val newItem = ItemPedido(productoConInventario.producto, 1)
            _itemsPedido.value = currentItems + newItem
        }
        updateTotal()
    }

    /**
     * Updates the quantity of a product in the order
     */
    fun actualizarCantidadProducto(productoId: String, nuevaCantidad: Int) {
        val currentItems = _itemsPedido.value ?: emptyList()
        val productoConInventario = _productosConInventario.value?.find { it.id == productoId }

        if (productoConInventario != null && nuevaCantidad > 0 && nuevaCantidad <= productoConInventario.cantidadDisponible) {
            val updatedItems = currentItems.map { item ->
                if (item.producto.id == productoId) {
                    item.copy(cantidad = nuevaCantidad)
                } else {
                    item
                }
            }
            _itemsPedido.value = updatedItems
        } else if (nuevaCantidad == 0) {
            // Remove product from order
            _itemsPedido.value = currentItems.filter { it.producto.id != productoId }
        }
        updateTotal()
    }

    /**
     * Removes a product from the order
     */
    fun removerProducto(productoId: String) {
        val currentItems = _itemsPedido.value ?: emptyList()
        _itemsPedido.value = currentItems.filter { it.producto.id != productoId }
        updateTotal()
    }

    /**
     * Validates if the order can be created
     */
    fun validarPedido(): String? {
        if (_clienteSeleccionado.value == null) {
            return ERROR_CLIENT_REQUIRED
        }
        if (_itemsPedido.value.isNullOrEmpty()) {
            return ERROR_NO_PRODUCTS_IN_ORDER
        }
        return null
    }

    /**
     * Creates the order
     */
    fun crearPedido(vendedorId: String) {
        val validationError = validarPedido()
        if (validationError != null) {
            _error.value = validationError
            return
        }

        viewModelScope.launch {
            try {
                _isCreatingOrder.value = true
                _error.value = null

                val items = _itemsPedido.value?.map { item ->
                    com.medisupplyg4.models.ItemPedidoRequest(
                        productoId = item.producto.id,
                        cantidad = item.cantidad
                    )
                } ?: emptyList()

                val request = PedidoCompletoRequest(
                    vendedorId = vendedorId,
                    clienteId = _clienteSeleccionado.value!!.id,
                    items = items
                )

                // Log the request details
                Log.d(TAG, "Enviando pedido:")
                Log.d(TAG, "  vendedorId: $vendedorId")
                Log.d(TAG, "  clienteId: ${_clienteSeleccionado.value!!.id}")
                Log.d(TAG, "  items count: ${items.size}")
                items.forEachIndexed { index, item ->
                    Log.d(TAG, "    item[$index]: productoId=${item.productoId}, cantidad=${item.cantidad}")
                }

                // Obtener token de autenticación
                val token = SessionManager.getToken(getApplication()) ?: ""
                val response = repository.crearPedidoCompleto(token, request)

                if (response != null) {
                    _success.value = true
                    Log.d(TAG, "Pedido creado exitosamente: ${response.message}")
                } else {
                    _error.value = ERROR_CREATING_ORDER
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción al crear pedido: ${e.message}", e)
                // Check if the exception contains a detail message from the API
                if (e.message?.contains("No se puede crear el pedido") == true || 
                    e.message?.contains("Problemas con productos") == true) {
                    _error.value = e.message ?: ERROR_CREATING_ORDER
                } else {
                    _error.value = ERROR_NETWORK_CONNECTION
                }
            } finally {
                _isCreatingOrder.value = false
            }
        }
    }

    /**
     * Clears the order and resets the form
     */
    fun limpiarPedido() {
        _clienteSeleccionado.value = null
        _itemsPedido.value = emptyList()
        _searchQuery.value = ""
        _clientSearchQuery.value = ""
        _productosFiltrados.value = _productosConInventario.value ?: emptyList()
        _clientesFiltrados.value = _clientes.value ?: emptyList()
        _success.value = false
        _error.value = null
        updateTotal()
    }

    /**
     * Refreshes the inventory data
     */
    fun refrescarInventario() {
        loadProductosConInventario()
    }

    /**
     * Clears error state
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Filters products based on search query
     */
    private fun filterProductos(query: String) {
        val allProductos = _productosConInventario.value ?: emptyList()
        
        // First filter by stock availability (only products with stock > 0)
        val productosConStock = allProductos.filter { productoConInventario ->
            productoConInventario.cantidadDisponible > 0
        }
        
        if (query.isEmpty()) {
            _productosFiltrados.value = productosConStock
        } else {
            val normalizedQuery = normalizeString(query)
            val filtered = productosConStock.filter { productoConInventario ->
                normalizeString(productoConInventario.nombre).contains(normalizedQuery, ignoreCase = true) ||
                normalizeString(productoConInventario.descripcion).contains(normalizedQuery, ignoreCase = true) ||
                normalizeString(productoConInventario.categoria.nombre).contains(normalizedQuery, ignoreCase = true)
            }
            _productosFiltrados.value = filtered
        }
    }

    /**
     * Filters clients based on search query
     */
    private fun filterClientes(query: String) {
        val allClientes = _clientes.value ?: emptyList()
        if (query.isEmpty()) {
            _clientesFiltrados.value = allClientes
        } else {
            val normalizedQuery = normalizeString(query)
            val filtered = allClientes.filter { cliente ->
                normalizeString(cliente.nombre).contains(normalizedQuery, ignoreCase = true) ||
                normalizeString(cliente.email).contains(normalizedQuery, ignoreCase = true) ||
                normalizeString(cliente.telefono).contains(normalizedQuery, ignoreCase = true)
            }
            _clientesFiltrados.value = filtered
        }
    }

    /**
     * Observes search query changes to filter products
     */
    private fun observeSearchQuery() {
        // This will be handled by setSearchQuery method
    }

    /**
     * Updates the total order amount
     */
    private fun updateTotal() {
        val total = _itemsPedido.value?.sumOf { it.subtotal } ?: 0.0
        (totalPedido as MutableLiveData).value = total
    }
}
