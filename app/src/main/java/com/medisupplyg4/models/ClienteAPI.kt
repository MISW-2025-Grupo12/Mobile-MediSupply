package com.medisupplyg4.models

/**
 * Model to represent a client from the API
 */
data class ClienteAPI(
    val id: String,
    val nombre: String,
    val email: String,
    val telefono: String,
    val direccion: String
)
