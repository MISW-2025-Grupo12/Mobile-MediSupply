package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

/**
 * Modelo para representar una visita programada del backend
 */
data class VisitaAPI(
    val id: String,
    @SerializedName("fecha_programada") val fechaProgramadaString: String,
    val direccion: String,
    val telefono: String,
    val estado: String,
    val descripcion: String,
    val vendedor: VendedorAPI,
    val cliente: ClienteVisitaAPI
) {
    /**
     * Convierte la fecha string a LocalDateTime
     */
    val fechaProgramada: LocalDateTime
        get() = try {
            LocalDateTime.parse(fechaProgramadaString)
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
     * Obtiene la dirección del cliente
     */
    val direccionCliente: String
        get() = cliente.direccion

    /**
     * Obtiene el email del cliente
     */
    val emailCliente: String
        get() = cliente.email
}
