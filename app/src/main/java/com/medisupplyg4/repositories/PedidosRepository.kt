package com.medisupplyg4.repositories

import android.util.Log
import com.medisupplyg4.models.ClienteAPI
import com.medisupplyg4.models.InventarioAPI
import com.medisupplyg4.models.PedidoCompletoRequest
import com.medisupplyg4.models.PedidoCompletoResponse

import com.medisupplyg4.models.ProductoConInventario
import com.medisupplyg4.network.NetworkClient
import com.google.gson.Gson

/**
 * Repository to handle orders, clients and products data
 */
class PedidosRepository {

    companion object {
        private const val TAG = "PedidosRepository"
        
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
    }

    private val clientesApiService = NetworkClient.clientesApiService
    private val productosApiService = NetworkClient.productosApiService
    private val inventarioApiService = NetworkClient.inventarioApiService
    private val pedidosApiService = NetworkClient.pedidosApiService

    /**
     * Gets all available clients
     */
    suspend fun getClientes(): List<ClienteAPI>? {
        return try {
            Log.d(TAG, "Obteniendo lista de clientes")

            val response = clientesApiService.getClientes()

            if (response.isSuccessful) {
                val clientes = response.body()
                Log.d(TAG, "Clientes obtenidos exitosamente: ${clientes?.size ?: 0}")
                clientes
            } else {
                Log.e(TAG, "Error al obtener clientes: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener clientes", e)
            null
        }
    }

    /**
     * Gets all available products with inventory
     */
    suspend fun getProductosConInventario(): List<ProductoConInventario>? {
        return try {
            Log.d(TAG, "Obteniendo lista de productos con inventario")

            // Get products first
            val productosResponse = productosApiService.getProductos()
            
            if (!productosResponse.isSuccessful) {
                Log.e(TAG, "Error al obtener productos: ${productosResponse.code()}")
                return null
            }
            
            val productos = productosResponse.body() ?: emptyList()
            Log.d(TAG, "Productos obtenidos exitosamente: ${productos.size}")
            
            // Try to get inventory
            val inventarioResponse = inventarioApiService.getInventario()
            val inventario = if (inventarioResponse.isSuccessful) {
                Log.d(TAG, "Inventario obtenido exitosamente: ${inventarioResponse.body()?.size ?: 0}")
                inventarioResponse.body() ?: emptyList()
            } else {
                Log.w(TAG, "Inventario no disponible (${inventarioResponse.code()}), usando inventario por defecto")
                emptyList()
            }
            
            // Combine products with their inventory
            val productosConInventario = productos.map { producto ->
                val inventarioProducto = inventario.find { it.productoId == producto.id }
                if (inventarioProducto != null) {
                    ProductoConInventario(producto, inventarioProducto)
                } else {
                    // If no inventory found, create a default one with 0 available
                    val inventarioDefault = InventarioAPI(
                        productoId = producto.id,
                        totalDisponible = 0,
                        totalReservado = 0,
                        lotes = emptyList()
                    )
                    ProductoConInventario(producto, inventarioDefault)
                }
            }.sortedBy { normalizeString(it.nombre) } // Sort alphabetically by product name (normalized)
            
            Log.d(TAG, "Productos con inventario procesados exitosamente: ${productosConInventario.size}")
            productosConInventario
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener productos con inventario", e)
            null
        }
    }

    /**
     * Creates a complete order
     */
    suspend fun crearPedidoCompleto(request: PedidoCompletoRequest): PedidoCompletoResponse? {
        return try {
            Log.d(TAG, "Creando pedido completo para cliente: ${request.clienteId}")
            Log.d(TAG, "Request details:")
            Log.d(TAG, "  vendedorId: ${request.vendedorId}")
            Log.d(TAG, "  clienteId: ${request.clienteId}")
            Log.d(TAG, "  items: ${request.items.size}")
            request.items.forEachIndexed { index, item ->
                Log.d(TAG, "    [$index] productoId: ${item.productoId}, cantidad: ${item.cantidad}")
            }

            // Log the JSON that will be sent
            val gson = Gson()
            val jsonRequest = gson.toJson(request)
            Log.d(TAG, "JSON Request: $jsonRequest")

            val response = pedidosApiService.crearPedidoCompleto(request)

            if (response.isSuccessful) {
                val result = response.body()
                Log.d(TAG, "Pedido creado exitosamente: ${result?.message}")
                result
            } else {
                Log.e(TAG, "Error al crear pedido: ${response.code()} - ${response.message()}")
                Log.e(TAG, "Response body: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al crear pedido", e)
            null
        }
    }
}
