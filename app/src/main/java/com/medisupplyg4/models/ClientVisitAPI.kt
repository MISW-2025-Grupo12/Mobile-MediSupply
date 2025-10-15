package com.medisupplyg4.models

/**
 * Model to represent a client in the context of visits
 */
data class ClientVisitAPI(
    val id: String,
    val nombre: String,
    val email: String,
    val telefono: String,
    val direccion: String
)
