package com.medisupplyg4.models

/**
 * Modelo para el cliente que viene del API
 */
data class ClienteAPI(
    val nombre: String,
    val telefono: String,
    val direccion: String,
    val avatar: String
)
