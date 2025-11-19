package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName

/**
 * Model to represent a complete order request
 */
data class PedidoCompletoRequest(
    @SerializedName("entidad_id") val vendedorId: String,
    @SerializedName("cliente_id") val clienteId: String,
    val items: List<ItemPedidoRequest>
)

/**
 * Model to represent an order item
 */
data class ItemPedidoRequest(
    @SerializedName("producto_id") val productoId: String,
    val cantidad: Int
)

/**
 * Model to represent a complete order response
 */
data class PedidoCompletoResponse(
    val message: String,
    @SerializedName("pedido_id") val pedidoId: String,
    val estado: String,
    val total: Double
)

/**
 * Model to represent an order item in the UI
 */
data class ItemPedido(
    val producto: ProductoAPI,
    var cantidad: Int
) {
    val subtotal: Double
        get() = producto.precio * cantidad
}