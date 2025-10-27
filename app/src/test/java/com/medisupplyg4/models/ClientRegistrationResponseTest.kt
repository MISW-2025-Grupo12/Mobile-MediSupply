package com.medisupplyg4.models

import org.junit.Test
import org.junit.Assert.*

class ClientRegistrationResponseTest {

    @Test
    fun `ClientRegistrationResponse should create with correct values`() {
        // Given
        val mensaje = "Cliente registrado exitosamente"
        val cliente = ClientRegistrationData(
            id = "550e8400-e29b-41d4-a716-446655440000",
            nombre = "Juan Pérez",
            email = "juan@example.com",
            identificacion = "1234567890",
            telefono = "3001234567",
            direccion = "Calle 123 #45-67"
        )

        // When
        val response = ClientRegistrationResponse(
            mensaje = mensaje,
            cliente = cliente
        )

        // Then
        assertEquals(mensaje, response.mensaje)
        assertEquals(cliente, response.cliente)
    }

    @Test
    fun `ClientRegistrationData should create with correct values`() {
        // Given
        val id = "550e8400-e29b-41d4-a716-446655440000"
        val nombre = "María García"
        val email = "maria@example.com"
        val identificacion = "0987654321"
        val telefono = "3109876543"
        val direccion = "Avenida 456 #78-90"

        // When
        val cliente = ClientRegistrationData(
            id = id,
            nombre = nombre,
            email = email,
            identificacion = identificacion,
            telefono = telefono,
            direccion = direccion
        )

        // Then
        assertEquals(id, cliente.id)
        assertEquals(nombre, cliente.nombre)
        assertEquals(email, cliente.email)
        assertEquals(identificacion, cliente.identificacion)
        assertEquals(telefono, cliente.telefono)
        assertEquals(direccion, cliente.direccion)
    }

    @Test
    fun `ClientRegistrationResponse should handle empty values`() {
        // Given
        val emptyMensaje = ""
        val emptyCliente = ClientRegistrationData(
            id = "",
            nombre = "",
            email = "",
            identificacion = "",
            telefono = "",
            direccion = ""
        )

        // When
        val response = ClientRegistrationResponse(
            mensaje = emptyMensaje,
            cliente = emptyCliente
        )

        // Then
        assertEquals("", response.mensaje)
        assertEquals("", response.cliente.id)
        assertEquals("", response.cliente.nombre)
        assertEquals("", response.cliente.email)
        assertEquals("", response.cliente.identificacion)
        assertEquals("", response.cliente.telefono)
        assertEquals("", response.cliente.direccion)
    }

    @Test
    fun `ClientRegistrationData should handle special characters`() {
        // Given
        val cliente = ClientRegistrationData(
            id = "550e8400-e29b-41d4-a716-446655440000",
            nombre = "José María",
            email = "jose.maria+tag@example.com",
            identificacion = "1234567890",
            telefono = "+57-300-123-4567",
            direccion = "Calle 123 #45-67, Bogotá"
        )

        // Then
        assertEquals("José María", cliente.nombre)
        assertEquals("jose.maria+tag@example.com", cliente.email)
        assertEquals("+57-300-123-4567", cliente.telefono)
        assertEquals("Calle 123 #45-67, Bogotá", cliente.direccion)
    }

    @Test
    fun `ClientRegistrationResponse should handle different message types`() {
        // Test success message
        val successResponse = ClientRegistrationResponse(
            mensaje = "Cliente registrado exitosamente",
            cliente = ClientRegistrationData(
                id = "test-id",
                nombre = "Test User",
                email = "test@example.com",
                identificacion = "1234567890",
                telefono = "3001234567",
                direccion = "Test Address"
            )
        )
        assertEquals("Cliente registrado exitosamente", successResponse.mensaje)

        // Test error message
        val errorResponse = ClientRegistrationResponse(
            mensaje = "Error al registrar cliente",
            cliente = ClientRegistrationData(
                id = "",
                nombre = "",
                email = "",
                identificacion = "",
                telefono = "",
                direccion = ""
            )
        )
        assertEquals("Error al registrar cliente", errorResponse.mensaje)
    }

    @Test
    fun `ClientRegistrationData should handle different ID formats`() {
        // Test UUID format
        val uuidCliente = ClientRegistrationData(
            id = "550e8400-e29b-41d4-a716-446655440000",
            nombre = "Test User",
            email = "test@example.com",
            identificacion = "1234567890",
            telefono = "3001234567",
            direccion = "Test Address"
        )
        assertEquals("550e8400-e29b-41d4-a716-446655440000", uuidCliente.id)

        // Test numeric ID
        val numericCliente = ClientRegistrationData(
            id = "12345",
            nombre = "Test User",
            email = "test@example.com",
            identificacion = "1234567890",
            telefono = "3001234567",
            direccion = "Test Address"
        )
        assertEquals("12345", numericCliente.id)
    }

    @Test
    fun `ClientRegistrationData should handle different phone formats`() {
        val phoneFormats = listOf(
            "3001234567",
            "+573001234567",
            "+57-300-123-4567",
            "300-123-4567",
            "(300) 123-4567"
        )

        phoneFormats.forEach { phone ->
            val cliente = ClientRegistrationData(
                id = "test-id",
                nombre = "Test User",
                email = "test@example.com",
                identificacion = "1234567890",
                telefono = phone,
                direccion = "Test Address"
            )
            assertEquals(phone, cliente.telefono)
        }
    }
}
