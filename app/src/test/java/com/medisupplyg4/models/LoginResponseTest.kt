package com.medisupplyg4.models

import org.junit.Test
import org.junit.Assert.*

class LoginResponseTest {

    @Test
    fun `LoginResponse should create with correct values`() {
        // Given
        val accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        val expiresIn = 86400
        val tokenType = "Bearer"
        val userInfo = UserInfo(
            email = "test@example.com",
            entidad_id = "550e8400-e29b-41d4-a716-446655440002",
            id = "5eb25b99-92bc-4022-8802-2cc00f5d5bde",
            identificacion = "0987654321",
            tipo_usuario = "CLIENTE"
        )

        // When
        val loginResponse = LoginResponse(
            access_token = accessToken,
            expires_in = expiresIn,
            token_type = tokenType,
            user_info = userInfo
        )

        // Then
        assertEquals(accessToken, loginResponse.access_token)
        assertEquals(expiresIn, loginResponse.expires_in)
        assertEquals(tokenType, loginResponse.token_type)
        assertEquals(userInfo, loginResponse.user_info)
    }

    @Test
    fun `UserInfo should create with correct values`() {
        // Given
        val email = "cliente@medisupply.com"
        val entidadId = "550e8400-e29b-41d4-a716-446655440002"
        val id = "5eb25b99-92bc-4022-8802-2cc00f5d5bde"
        val identificacion = "0987654321"
        val tipoUsuario = "CLIENTE"

        // When
        val userInfo = UserInfo(
            email = email,
            entidad_id = entidadId,
            id = id,
            identificacion = identificacion,
            tipo_usuario = tipoUsuario
        )

        // Then
        assertEquals(email, userInfo.email)
        assertEquals(entidadId, userInfo.entidad_id)
        assertEquals(id, userInfo.id)
        assertEquals(identificacion, userInfo.identificacion)
        assertEquals(tipoUsuario, userInfo.tipo_usuario)
    }

    @Test
    fun `UserInfo should handle different user types`() {
        // Test CLIENTE
        val clienteInfo = UserInfo(
            email = "cliente@medisupply.com",
            entidad_id = "cliente-id",
            id = "cliente-uuid",
            identificacion = "1234567890",
            tipo_usuario = "CLIENTE"
        )
        assertEquals("CLIENTE", clienteInfo.tipo_usuario)

        // Test VENDEDOR
        val vendedorInfo = UserInfo(
            email = "vendedor@medisupply.com",
            entidad_id = "vendedor-id",
            id = "vendedor-uuid",
            identificacion = "0987654321",
            tipo_usuario = "VENDEDOR"
        )
        assertEquals("VENDEDOR", vendedorInfo.tipo_usuario)

        // Test REPARTIDOR
        val repartidorInfo = UserInfo(
            email = "repartidor@medisupply.com",
            entidad_id = "repartidor-id",
            id = "repartidor-uuid",
            identificacion = "1122334455",
            tipo_usuario = "REPARTIDOR"
        )
        assertEquals("REPARTIDOR", repartidorInfo.tipo_usuario)
    }

    @Test
    fun `LoginResponse should handle empty token`() {
        // Given
        val emptyToken = ""
        val userInfo = UserInfo(
            email = "test@example.com",
            entidad_id = "test-id",
            id = "test-uuid",
            identificacion = "1234567890",
            tipo_usuario = "CLIENTE"
        )

        // When
        val loginResponse = LoginResponse(
            access_token = emptyToken,
            expires_in = 0,
            token_type = "",
            user_info = userInfo
        )

        // Then
        assertEquals("", loginResponse.access_token)
        assertEquals(0, loginResponse.expires_in)
        assertEquals("", loginResponse.token_type)
    }
}
