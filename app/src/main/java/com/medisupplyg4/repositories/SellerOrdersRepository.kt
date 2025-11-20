package com.medisupplyg4.repositories

import android.util.Log
import com.medisupplyg4.models.OrderItemUI
import com.medisupplyg4.models.OrderStatus
import com.medisupplyg4.models.OrderUI
import com.medisupplyg4.models.PedidoClienteAPI
import com.medisupplyg4.network.NetworkClient
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SellerOrdersRepository {

    companion object { private const val TAG = "SellerOrdersRepository" }

    private val pedidosApi = NetworkClient.pedidosApiService

    suspend fun getPedidosVendedor(
        token: String,
        vendedorId: String,
        orderNumberPrefix: String = "Pedido",
        page: Int = 1,
        pageSize: Int = 20
    ): Result<List<OrderUI>> {
        return try {
            Log.d(TAG, "Obteniendo pedidos del vendedor $vendedorId (página $page, tamaño $pageSize)")
            val resp = pedidosApi.getPedidosVendedor("Bearer $token", vendedorId, page, pageSize)
            if (resp.isSuccessful) {
                val body = resp.body()
                val orders = body?.items?.map { it.toOrderUI(orderNumberPrefix) } ?: emptyList()
                Result.success(orders)
            } else {
                Result.failure(Exception("HTTP_${resp.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener pedidos de vendedor", e)
            Result.failure(e)
        }
    }
}

private fun PedidoClienteAPI.toOrderUI(orderNumberPrefix: String = "Pedido"): OrderUI {
    val itemsUi = items.map {
        OrderItemUI(
            id = it.id,
            name = it.nombreProducto,
            quantity = it.cantidad,
            unitPrice = it.precioUnitario
        )
    }
    
    // Parse fecha_creacion from ISO 8601 format (e.g., "2025-10-29T02:06:19.651168")
    val createdAt = try {
        val dateTime = LocalDateTime.parse(
            fechaCreacion,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        )
        dateTime.toLocalDate()
    } catch (e: Exception) {
        Log.w("SellerOrdersRepository", "Error parseando fecha_creacion: $fechaCreacion", e)
        LocalDate.now() // Fallback a fecha actual si hay error
    }
    
    return OrderUI(
        id = id,
        number = "$orderNumberPrefix ${id.take(6)}",
        createdAt = createdAt,
        status = mapBackendStatus(estado),
        items = itemsUi
    )
}

private fun mapBackendStatus(backend: String): OrderStatus = when (backend.lowercase()) {
    "borrador" -> OrderStatus.BORRADOR
    "confirmado" -> OrderStatus.CONFIRMADO
    "en_transito" -> OrderStatus.EN_TRANSITO
    "entregado" -> OrderStatus.ENTREGADO
    else -> OrderStatus.BORRADOR
}

