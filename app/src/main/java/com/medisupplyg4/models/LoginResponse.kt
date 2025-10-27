package com.medisupplyg4.models

data class LoginResponse(
    val access_token: String,
    val expires_in: Int,
    val token_type: String,
    val user_info: UserInfo
)

data class UserInfo(
    val email: String,
    val entidad_id: String,
    val id: String,
    val identificacion: String,
    val tipo_usuario: String
)
