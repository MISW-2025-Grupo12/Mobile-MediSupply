package com.medisupplyg4.models

/**
 * Model to represent a seller from the backend
 */
data class SellerAPI(
    val id: String,
    val nombre: String,
    val email: String,
    val telefono: String,
    val direccion: String
)
