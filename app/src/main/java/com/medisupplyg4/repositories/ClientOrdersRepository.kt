package com.medisupplyg4.repositories

import android.util.Log
import com.medisupplyg4.models.OrderItemUI
import com.medisupplyg4.models.OrderStatus
import com.medisupplyg4.models.OrderUI
import com.medisupplyg4.models.PedidoClienteAPI
import com.medisupplyg4.network.NetworkClient
import java.time.LocalDate

class ClientOrdersRepository {

    companion object { private const val TAG = "ClientOrdersRepository" }

    private val pedidosApi = NetworkClient.pedidosApiService

    suspend fun getPedidosCliente(
        token: String,
        clienteId: String,
        page: Int = 1,
        pageSize: Int = 20
    ): Result<List<OrderUI>> {
        return try {
            Log.d(TAG, "Obteniendo pedidos del cliente $clienteId (página $page, tamaño $pageSize)")
            val resp = pedidosApi.getPedidosCliente("Bearer $token", clienteId, page, pageSize)
            if (resp.isSuccessful) {
                val body = resp.body()
                val orders = body?.items?.map { it.toOrderUI() } ?: emptyList()
                Result.success(orders)
            } else {
                Result.failure(Exception("HTTP_${resp.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener pedidos de cliente", e)
            Result.failure(e)
        }
    }
}

private fun PedidoClienteAPI.toOrderUI(): OrderUI {
    val itemsUi = items.map {
        OrderItemUI(
            id = it.id,
            name = it.nombreProducto,
            quantity = it.cantidad,
            unitPrice = it.precioUnitario
        )
    }
    return OrderUI(
        id = id,
        number = "Pedido ${id.take(6)}",
        createdAt = LocalDate.now(),
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
