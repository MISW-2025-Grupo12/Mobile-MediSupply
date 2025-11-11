package com.medisupplyg4.models

/**
 * Model to represent a seller from the backend
 */
data class SellerAPI(
    val id: String,
    val nombre: String,
    val email: String,
    val identificacion: String? = null,
    val telefono: String,
    val direccion: String
)
