package com.medisupplyg4.models

/**
 * Model for the client that comes from the API
 */
data class ClientAPI(
    val nombre: String,
    val telefono: String,
    val direccion: String,
    val avatar: String
)
