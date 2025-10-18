package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName

/**
 * Model to represent error response from the API
 */
data class ErrorResponse(
    val success: Boolean,
    val error: String,
    val detalle: String? = null,
    @SerializedName("items_con_problemas") val itemsConProblemas: List<ItemConProblema>? = null,
    @SerializedName("items_validos") val itemsValidos: List<ItemValido>? = null,
    val resumen: ResumenError? = null
)

/**
 * Model to represent item with problems in error response
 */
data class ItemConProblema(
    @SerializedName("producto_id") val productoId: String,
    val nombre: String,
    @SerializedName("cantidad_solicitada") val cantidadSolicitada: Int,
    @SerializedName("cantidad_disponible") val cantidadDisponible: Int,
    val problema: String,
    val mensaje: String
)

/**
 * Model to represent valid item in error response
 */
data class ItemValido(
    @SerializedName("producto_id") val productoId: String,
    val nombre: String,
    val cantidad: Int,
    val precio: Double
)

/**
 * Model to represent error summary
 */
data class ResumenError(
    @SerializedName("total_items_solicitados") val totalItemsSolicitados: Int,
    @SerializedName("items_validos") val itemsValidos: Int,
    @SerializedName("items_con_problemas") val itemsConProblemas: Int,
    @SerializedName("productos_no_existen") val productosNoExisten: Int,
    @SerializedName("productos_stock_insuficiente") val productosStockInsuficiente: Int,
    @SerializedName("productos_precio_invalido") val productosPrecioInvalido: Int
)
