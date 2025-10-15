package com.medisupplyg4.models

/**
 * Modelo para representar un vendedor del backend
 */
data class VendedorAPI(
    val id: String,
    val nombre: String,
    val email: String,
    val telefono: String,
    val direccion: String
)
