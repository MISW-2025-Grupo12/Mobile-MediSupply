package com.medisupplyg4.models

import org.junit.Test
import org.junit.Assert.*

class ClientRegistrationRequestTest {

    @Test
    fun `ClientRegistrationRequest should create with correct values`() {
        // Given
        val nombre = "Juan Pérez"
        val email = "juan@example.com"
        val identificacion = "1234567890"
        val telefono = "3001234567"
        val direccion = "Calle 123 #45-67"
        val password = "password123"

        // When
        val request = ClientRegistrationRequest(
            nombre = nombre,
            email = email,
            identificacion = identificacion,
            telefono = telefono,
            direccion = direccion,
            password = password
        )

        // Then
        assertEquals(nombre, request.nombre)
        assertEquals(email, request.email)
        assertEquals(identificacion, request.identificacion)
        assertEquals(telefono, request.telefono)
        assertEquals(direccion, request.direccion)
        assertEquals(password, request.password)
    }

    @Test
    fun `ClientRegistrationRequest should handle empty values`() {
        // Given
        val emptyValues = ClientRegistrationRequest(
            nombre = "",
            email = "",
            identificacion = "",
            telefono = "",
            direccion = "",
            password = ""
        )

        // Then
        assertEquals("", emptyValues.nombre)
        assertEquals("", emptyValues.email)
        assertEquals("", emptyValues.identificacion)
        assertEquals("", emptyValues.telefono)
        assertEquals("", emptyValues.direccion)
        assertEquals("", emptyValues.password)
    }

    @Test
    fun `ClientRegistrationRequest should handle special characters`() {
        // Given
        val nombre = "José María"
        val email = "jose.maria+tag@example.com"
        val identificacion = "1234567890"
        val telefono = "+57-300-123-4567"
        val direccion = "Calle 123 #45-67, Bogotá"
        val password = "P@ssw0rd!@#"

        // When
        val request = ClientRegistrationRequest(
            nombre = nombre,
            email = email,
            identificacion = identificacion,
            telefono = telefono,
            direccion = direccion,
            password = password
        )

        // Then
        assertEquals(nombre, request.nombre)
        assertEquals(email, request.email)
        assertEquals(identificacion, request.identificacion)
        assertEquals(telefono, request.telefono)
        assertEquals(direccion, request.direccion)
        assertEquals(password, request.password)
    }

    @Test
    fun `ClientRegistrationRequest should handle long values`() {
        // Given
        val longNombre = "Juan Carlos Pérez González de la Cruz"
        val longEmail = "juan.carlos.perez.gonzalez.de.la.cruz@verylongdomainname.example.com"
        val longIdentificacion = "123456789012345"
        val longTelefono = "+57-300-123-4567-890"
        val longDireccion = "Calle 123 #45-67, Apartamento 8-901, Barrio El Poblado, Medellín, Antioquia, Colombia"
        val longPassword = "VeryLongPasswordWithSpecialCharacters!@#$%^&*()_+"

        // When
        val request = ClientRegistrationRequest(
            nombre = longNombre,
            email = longEmail,
            identificacion = longIdentificacion,
            telefono = longTelefono,
            direccion = longDireccion,
            password = longPassword
        )

        // Then
        assertEquals(longNombre, request.nombre)
        assertEquals(longEmail, request.email)
        assertEquals(longIdentificacion, request.identificacion)
        assertEquals(longTelefono, request.telefono)
        assertEquals(longDireccion, request.direccion)
        assertEquals(longPassword, request.password)
    }
}
