package com.medisupplyg4.models

import org.junit.Assert.*
import org.junit.Test

class ClienteAPITest {

    @Test
    fun `ClienteAPI should create instance with correct properties`() {
        // Given
        val nombre = "Test Client"
        val telefono = "3001234567"
        val direccion = "Test Address 123"
        val avatar = "https://example.com/avatar.jpg"

        // When
        val cliente = ClienteAPI(
            nombre = nombre,
            telefono = telefono,
            direccion = direccion,
            avatar = avatar
        )

        // Then
        assertEquals(nombre, cliente.nombre)
        assertEquals(telefono, cliente.telefono)
        assertEquals(direccion, cliente.direccion)
        assertEquals(avatar, cliente.avatar)
    }
}
