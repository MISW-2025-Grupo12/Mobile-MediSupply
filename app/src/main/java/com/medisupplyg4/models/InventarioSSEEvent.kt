package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName

/**
 * Model to represent inventory SSE event data
 * Format: { "producto_id": "...", "cantidad_disponible": 149 }
 */
data class InventarioSSEEvent(
    @SerializedName("producto_id") val productoId: String,
    @SerializedName("cantidad_disponible") val cantidadDisponible: Int
)

