package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

/**
 * Modelo simplificado para las entregas del backend
 */
data class SimpleDelivery(
    val id: String,
    val direccion: String,
    @SerializedName("fecha_entrega") val fechaEntregaString: String,
    @SerializedName("producto_id") val productoId: String,
    @SerializedName("cliente_id") val clienteId: String
) {
    /**
     * Convierte la fecha string a LocalDateTime
     */
    val fechaEntrega: LocalDateTime
        get() = try {
            LocalDateTime.parse(fechaEntregaString)
        } catch (e: Exception) {
            LocalDateTime.now() // Fallback si hay error en el parsing
        }
}
