package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo para el pedido que viene del API
 */
data class PedidoAPI(
    val id: String,
    val cliente: ClienteAPI,
    val productos: List<ProductoAPI>
)
