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
    val cliente: ClienteAPI,
    val productos: List<ItemPedido>
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
    
    /**
     * Obtiene el nombre del cliente
     */
    val nombreCliente: String
        get() = cliente.nombre
    
    /**
     * Obtiene el teléfono del cliente
     */
    val telefonoCliente: String
        get() = cliente.telefono

    /**
     * Obtiene el avatar del cliente (placeholder)
     */
    val avatarCliente: String
        get() = "" // TODO: Add avatar field to ClienteAPI if needed

}
