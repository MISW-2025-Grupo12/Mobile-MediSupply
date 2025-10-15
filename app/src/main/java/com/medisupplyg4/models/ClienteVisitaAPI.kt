package com.medisupplyg4.models

/**
 * Modelo para representar un cliente en el contexto de visitas
 */
data class ClienteVisitaAPI(
    val id: String,
    val nombre: String,
    val email: String,
    val telefono: String,
    val direccion: String
)
