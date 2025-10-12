package com.medisupplyg4.models

import org.junit.Test
import kotlin.test.assertEquals

class ClienteAPITest {

    @Test
    fun `ClienteAPI should have correct properties`() {
        // Given
        val cliente = ClienteAPI(
            nombre = "Juan Pérez",
            telefono = "3001234567",
            direccion = "Calle 123 #45-67",
            avatar = "https://example.com/avatar.jpg"
        )

        // Then
        assertEquals("Juan Pérez", cliente.nombre)
        assertEquals("3001234567", cliente.telefono)
        assertEquals("Calle 123 #45-67", cliente.direccion)
        assertEquals("https://example.com/avatar.jpg", cliente.avatar)
    }

    @Test
    fun `ClienteAPI should handle empty strings`() {
        // Given
        val cliente = ClienteAPI(
            nombre = "",
            telefono = "",
            direccion = "",
            avatar = ""
        )

        // Then
        assertEquals("", cliente.nombre)
        assertEquals("", cliente.telefono)
        assertEquals("", cliente.direccion)
        assertEquals("", cliente.avatar)
    }

    @Test
    fun `ClienteAPI should handle special characters`() {
        // Given
        val cliente = ClienteAPI(
            nombre = "José María O'Connor",
            telefono = "+57 300-123-4567",
            direccion = "Calle 123 #45-67, Bogotá",
            avatar = "https://example.com/avatar-ñ.jpg"
        )

        // Then
        assertEquals("José María O'Connor", cliente.nombre)
        assertEquals("+57 300-123-4567", cliente.telefono)
        assertEquals("Calle 123 #45-67, Bogotá", cliente.direccion)
        assertEquals("https://example.com/avatar-ñ.jpg", cliente.avatar)
    }
}