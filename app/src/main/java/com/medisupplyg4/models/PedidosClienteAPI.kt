package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName

/** API model for an item inside an order in list response */
data class PedidoClienteItemAPI(
    val id: String,
    @SerializedName("producto_id") val productoId: String,
    @SerializedName("nombre_producto") val nombreProducto: String,
    val cantidad: Int,
    @SerializedName("precio_unitario") val precioUnitario: Double,
    val subtotal: Double
)

/** API model for order in list response */
data class PedidoClienteAPI(
    val id: String,
    @SerializedName("vendedor_id") val vendedorId: String,
    @SerializedName("cliente_id") val clienteId: String,
    val estado: String,
    val total: Double,
    val items: List<PedidoClienteItemAPI>
)
