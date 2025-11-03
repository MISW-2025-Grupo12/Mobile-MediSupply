package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName

data class ClientRegistrationResponse(
    val mensaje: String,
    val cliente: ClientRegistrationData
)

data class ClientRegistrationData(
    val id: String,
    val nombre: String,
    val email: String,
    val identificacion: String,
    val telefono: String,
    val direccion: String
)
