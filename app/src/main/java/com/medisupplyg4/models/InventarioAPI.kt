package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

/**
 * Model to represent inventory from the API
 */
data class InventarioAPI(
    @SerializedName("producto_id") val productoId: String,
    @SerializedName("total_disponible") val totalDisponible: Int,
    @SerializedName("total_reservado") val totalReservado: Int,
    val lotes: List<LoteAPI>
) {
    /**
     * Gets the available quantity (only total_disponible, not subtracting reserved)
     */
    val cantidadDisponible: Int
        get() = totalDisponible
}

/**
 * Model to represent a product lot
 */
data class LoteAPI(
    @SerializedName("fecha_vencimiento") val fechaVencimientoString: String,
    @SerializedName("cantidad_disponible") val cantidadDisponible: Int,
    @SerializedName("cantidad_reservada") val cantidadReservada: Int
)
