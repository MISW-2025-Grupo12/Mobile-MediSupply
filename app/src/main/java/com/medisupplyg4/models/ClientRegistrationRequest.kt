package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName

data class ClientRegistrationRequest(
    val nombre: String,
    val email: String,
    val identificacion: String,
    val telefono: String,
    val direccion: String,
    val password: String
)
