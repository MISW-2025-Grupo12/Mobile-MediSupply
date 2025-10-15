package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

/**
 * Model to represent a scheduled visit from the backend
 */
data class VisitAPI(
    val id: String,
    @SerializedName("fecha_programada") val fechaProgramadaString: String,
    val direccion: String,
    val telefono: String,
    val estado: String,
    val descripcion: String,
    val vendedor: SellerAPI,
    val cliente: ClientVisitAPI
) {
    /**
     * Converts the date string to LocalDateTime
     */
    val fechaProgramada: LocalDateTime
        get() = try {
            LocalDateTime.parse(fechaProgramadaString)
        } catch (e: Exception) {
            LocalDateTime.now() // Fallback if there's an error in parsing
        }

    /**
     * Gets the client name
     */
    val nombreCliente: String
        get() = cliente.nombre

    /**
     * Gets the client phone
     */
    val telefonoCliente: String
        get() = cliente.telefono

    /**
     * Gets the client address
     */
    val direccionCliente: String
        get() = cliente.direccion

    /**
     * Gets the client email
     */
    val emailCliente: String
        get() = cliente.email
}
