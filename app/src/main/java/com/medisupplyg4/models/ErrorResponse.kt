package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName

/**
 * Model to represent error response from the API
 */
data class ErrorResponse(
    val success: Boolean,
    val error: String,
    val detalle: String? = null,
    @SerializedName("items_pedido") val itemsPedido: List<ItemPedidoError>? = null
)

/**
 * Model to represent order item in error response
 */
data class ItemPedidoError(
    @SerializedName("producto_id") val productoId: String,
    val nombre: String,
    val cantidad: Int,
    @SerializedName("precio_unitario") val precioUnitario: Double,
    val subtotal: Double
)
