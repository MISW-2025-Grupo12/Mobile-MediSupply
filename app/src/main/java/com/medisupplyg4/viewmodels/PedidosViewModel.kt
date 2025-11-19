package com.medisupplyg4.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.medisupplyg4.R
import com.medisupplyg4.models.ClienteAPI
import com.medisupplyg4.models.InventarioAPI
import com.medisupplyg4.models.ItemPedido
import com.medisupplyg4.models.PedidoCompletoRequest
import com.medisupplyg4.models.ProductoConInventario
import com.medisupplyg4.network.InventarioSSEService
import com.medisupplyg4.repositories.PedidosRepository
import com.medisupplyg4.utils.SessionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
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
    private val sseService = InventarioSSEService()
    private var sseJob: Job? = null
    private var isSSEPaused: Boolean = false

    // Clients data
    private val _clientes = MutableLiveData<List<ClienteAPI>>()
    val clientes: LiveData<List<ClienteAPI>> = _clientes

    // Products with inventory data
    private val _productosConInventario = MutableLiveData<List<ProductoConInventario>>()
    val productosConInventario: LiveData<List<ProductoConInventario>> = _productosConInventario

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
    
    override fun onCleared() {
        super.onCleared()
        disconnectFromInventoryStream()
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
                
                // Connect to SSE stream after products are loaded (if not already connected)
                if (sseJob == null || sseJob?.isCompleted == true) {
                    connectToInventoryStream()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading products with inventory", e)
                _error.value = ERROR_NETWORK_CONNECTION
            } finally {
                _isLoadingProductos.value = false
            }
        }
    }
    
    /**
     * Connects to the inventory SSE stream for real-time updates
     */
    private fun connectToInventoryStream() {
        val token = SessionManager.getToken(getApplication()) ?: ""
        if (token.isEmpty()) {
            Log.w(TAG, "No token available, skipping SSE connection")
            return
        }
        
        // Cancel existing connection if any
        disconnectFromInventoryStream()
        
        sseJob = viewModelScope.launch {
            sseService.connect(token)
                .catch { e ->
                    Log.e(TAG, "Error in SSE stream: ${e.message}", e)
                }
                .collect { event ->
                    // Skip processing if SSE is paused (e.g., during order creation)
                    if (isSSEPaused) {
                        Log.d(TAG, "SSE paused, skipping event: ${event.type}")
                        return@collect
                    }
                    
                    when (event.type) {
                        InventarioSSEService.EventType.INVENTORY -> {
                            // Initial inventory state - update the product
                            event.data?.let { updateInventoryFromSSE(it) }
                        }
                        InventarioSSEService.EventType.UPDATE -> {
                            // Inventory update - update the product
                            event.data?.let { updateInventoryFromSSE(it) }
                        }
                        InventarioSSEService.EventType.HEARTBEAT -> {
                            // Heartbeat - just log, no action needed
                            Log.d(TAG, "Heartbeat received")
                        }
                    }
                }
        }
    }
    
    /**
     * Disconnects from the inventory SSE stream
     */
    private fun disconnectFromInventoryStream() {
        sseJob?.cancel()
        sseJob = null
        sseService.disconnect()
        isSSEPaused = false
    }
    
    /**
     * Pauses SSE updates (e.g., during order creation)
     */
    fun pauseSSEUpdates() {
        isSSEPaused = true
        Log.d(TAG, "SSE updates paused")
    }
    
    /**
     * Resumes SSE updates
     */
    fun resumeSSEUpdates() {
        isSSEPaused = false
        Log.d(TAG, "SSE updates resumed")
    }
    
    /**
     * Updates inventory for a product based on SSE event data
     * Note: Only updates products that are already in the list. New products must be loaded via loadProductosConInventario()
     * Also removes products from cart if they become out of stock
     */
    private fun updateInventoryFromSSE(sseEvent: com.medisupplyg4.models.InventarioSSEEvent) {
        val currentProductos = _productosConInventario.value ?: emptyList()
        val productoExists = currentProductos.any { it.id == sseEvent.productoId }
        
        if (!productoExists) {
            Log.d(TAG, "Product ${sseEvent.productoId} not in current list, skipping SSE update. Product will be included on next load.")
            return
        }
        
        val updatedProductos = currentProductos.map { productoConInventario ->
            if (productoConInventario.id == sseEvent.productoId) {
                // Update the inventory for this product
                val updatedInventario = InventarioAPI(
                    productoId = sseEvent.productoId,
                    totalDisponible = sseEvent.cantidadDisponible,
                    totalReservado = productoConInventario.inventario.totalReservado, // Keep existing reserved
                    lotes = productoConInventario.inventario.lotes // Keep existing lots
                )
                ProductoConInventario(productoConInventario.producto, updatedInventario)
            } else {
                productoConInventario
            }
        }
        
        _productosConInventario.value = updatedProductos
        
        // Re-apply filters to update the filtered list (this will show/hide products based on new stock)
        filterProductos(_searchQuery.value ?: "")
        
        // Remove products from cart if they become out of stock or insufficient stock
        removeOutOfStockItemsFromCart(sseEvent.productoId, sseEvent.cantidadDisponible)
        
        Log.d(TAG, "Inventory updated for product ${sseEvent.productoId}: ${sseEvent.cantidadDisponible}")
    }
    
    /**
     * Removes items from cart if they become out of stock or have insufficient stock
     * Only shows error messages to users who are NOT currently creating an order
     */
    private fun removeOutOfStockItemsFromCart(productoId: String, nuevaCantidadDisponible: Int) {
        val currentItems = _itemsPedido.value ?: emptyList()
        
        // Don't process if cart is empty (e.g., after successful order creation)
        if (currentItems.isEmpty()) {
            Log.d(TAG, "Cart is empty, skipping cart update for product $productoId")
            return
        }
        
        val itemInCart = currentItems.find { it.producto.id == productoId }
        val isCreatingOrder = _isCreatingOrder.value == true
        
        if (itemInCart != null) {
            if (nuevaCantidadDisponible <= 0) {
                // Product is completely out of stock, remove it
                val updatedItems = currentItems.filter { it.producto.id != productoId }
                _itemsPedido.value = updatedItems
                updateTotal()
                Log.d(TAG, "Product ${productoId} (${itemInCart.producto.nombre}) is out of stock, removed from cart")
                
                // Only show error message if NOT creating an order (to avoid showing to the user making the purchase)
                if (!isCreatingOrder) {
                    _error.value = getApplication<Application>().getString(R.string.product_out_of_stock_removed, itemInCart.producto.nombre)
                }
            } else if (itemInCart.cantidad > nuevaCantidadDisponible) {
                // Product has insufficient stock, adjust quantity to available stock
                val updatedItems = currentItems.map { item ->
                    if (item.producto.id == productoId) {
                        item.copy(cantidad = nuevaCantidadDisponible)
                    } else {
                        item
                    }
                }
                _itemsPedido.value = updatedItems
                updateTotal()
                Log.d(TAG, "Product ${productoId} (${itemInCart.producto.nombre}) quantity adjusted from ${itemInCart.cantidad} to $nuevaCantidadDisponible (available stock)")
                
                // Only show error message if NOT creating an order (to avoid showing to the user making the purchase)
                if (!isCreatingOrder) {
                    _error.value = getApplication<Application>().getString(R.string.product_quantity_adjusted, itemInCart.producto.nombre, nuevaCantidadDisponible)
                }
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

                // Pause SSE updates during order creation to avoid interference
                pauseSSEUpdates()

                // Clean up out of stock items before creating the order
                cleanOutOfStockItemsFromCart()
                
                // Re-validate after cleaning
                val validationErrorAfterCleanup = validarPedido()
                if (validationErrorAfterCleanup != null) {
                    _error.value = validationErrorAfterCleanup
                    _isCreatingOrder.value = false
                    // Resume SSE updates if validation fails
                    resumeSSEUpdates()
                    return@launch
                }

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
                    // Clear the cart immediately after successful order creation
                    // This prevents SSE events from processing items that were just ordered
                    _itemsPedido.value = emptyList()
                    updateTotal()
                    Log.d(TAG, "Cart cleared after successful order creation")
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
                // Resume SSE updates after order creation completes (success or error)
                // Cart is already cleared if order was successful, so SSE events won't affect it
                resumeSSEUpdates()
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
        // Reconnect to SSE stream to ensure we have the latest updates
        disconnectFromInventoryStream()
        connectToInventoryStream()
        // Ensure SSE is resumed when refreshing inventory
        resumeSSEUpdates()
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
    
    /**
     * Removes out of stock items from cart by checking current inventory
     */
    private fun cleanOutOfStockItemsFromCart() {
        val currentItems = _itemsPedido.value ?: emptyList()
        val productosConInventario = _productosConInventario.value ?: emptyList()
        
        val itemsToRemove = mutableListOf<String>()
        val itemsToAdjust = mutableMapOf<String, Int>()
        
        currentItems.forEach { item ->
            val productoConInventario = productosConInventario.find { it.id == item.producto.id }
            
            if (productoConInventario == null) {
                // Product not found in inventory, remove it
                itemsToRemove.add(item.producto.id)
                Log.d(TAG, "Product ${item.producto.id} (${item.producto.nombre}) not found in inventory, removing from cart")
            } else if (productoConInventario.cantidadDisponible <= 0) {
                // Product is out of stock, remove it
                itemsToRemove.add(item.producto.id)
                Log.d(TAG, "Product ${item.producto.id} (${item.producto.nombre}) is out of stock, removing from cart")
            } else if (item.cantidad > productoConInventario.cantidadDisponible) {
                // Product has insufficient stock, adjust quantity
                itemsToAdjust[item.producto.id] = productoConInventario.cantidadDisponible
                Log.d(TAG, "Product ${item.producto.id} (${item.producto.nombre}) has insufficient stock (requested: ${item.cantidad}, available: ${productoConInventario.cantidadDisponible}), adjusting quantity")
            }
        }
        
        if (itemsToRemove.isNotEmpty() || itemsToAdjust.isNotEmpty()) {
            val updatedItems = currentItems
                .filter { it.producto.id !in itemsToRemove }
                .map { item ->
                    itemsToAdjust[item.producto.id]?.let { newQuantity ->
                        item.copy(cantidad = newQuantity)
                    } ?: item
                }
            
            _itemsPedido.value = updatedItems
            updateTotal()
            
            if (itemsToRemove.isNotEmpty()) {
                val removedProductNames = itemsToRemove.mapNotNull { productId ->
                    currentItems.find { it.producto.id == productId }?.producto?.nombre
                }
                Log.w(TAG, "Removed ${itemsToRemove.size} out of stock items from cart: ${removedProductNames.joinToString(", ")}")
            }
            
            if (itemsToAdjust.isNotEmpty()) {
                val adjustedProductNames = itemsToAdjust.keys.mapNotNull { productId ->
                    currentItems.find { it.producto.id == productId }?.producto?.nombre
                }
                Log.w(TAG, "Adjusted quantity for ${itemsToAdjust.size} items: ${adjustedProductNames.joinToString(", ")}")
            }
        }
    }
}
