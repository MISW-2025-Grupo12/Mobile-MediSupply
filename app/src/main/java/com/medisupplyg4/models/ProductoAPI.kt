package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo para el producto que viene del API
 */
data class ProductoAPI(
    val nombre: String,
    val cantidad: Int,
    @SerializedName("precio_unitario") val precioUnitario: Int,
    val subtotal: Int,
    val avatar: String
)
