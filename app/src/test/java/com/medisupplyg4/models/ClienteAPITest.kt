package com.medisupplyg4.models

import org.junit.Test
import org.junit.Assert.*

class ClienteAPITest {

    @Test
    fun `ClienteAPI should create instance with all required fields`() {
        // Given
        val id = "test-id"
        val nombre = "Test Client"
        val email = "test@example.com"
        val identificacion = "1234567890"
        val telefono = "3001234567"
        val direccion = "Test Address"
        val estado = "ACTIVO"

        // When
        val cliente = ClienteAPI(
            id = id,
            nombre = nombre,
            email = email,
            identificacion = identificacion,
            telefono = telefono,
            direccion = direccion,
            estado = estado
        )

        // Then
        assertEquals(id, cliente.id)
        assertEquals(nombre, cliente.nombre)
        assertEquals(email, cliente.email)
        assertEquals(identificacion, cliente.identificacion)
        assertEquals(telefono, cliente.telefono)
        assertEquals(direccion, cliente.direccion)
        assertEquals(estado, cliente.estado)
    }

    @Test
    fun `ClienteAPI should handle empty strings`() {
        // Given
        val cliente = ClienteAPI(
            id = "",
            nombre = "",
            email = "",
            identificacion = "",
            telefono = "",
            direccion = "",
            estado = ""
        )

        // Then
        assertEquals("", cliente.id)
        assertEquals("", cliente.nombre)
        assertEquals("", cliente.email)
        assertEquals("", cliente.identificacion)
        assertEquals("", cliente.telefono)
        assertEquals("", cliente.direccion)
        assertEquals("", cliente.estado)
    }

    @Test
    fun `ClienteAPI should handle ACTIVO estado`() {
        // Given
        val cliente = ClienteAPI(
            id = "1",
            nombre = "Active Client",
            email = "active@test.com",
            identificacion = "1234567890",
            telefono = "3001234567",
            direccion = "Active Address",
            estado = "ACTIVO"
        )

        // Then
        assertEquals("ACTIVO", cliente.estado)
    }

    @Test
    fun `ClienteAPI should handle INACTIVO estado`() {
        // Given
        val cliente = ClienteAPI(
            id = "2",
            nombre = "Inactive Client",
            email = "inactive@test.com",
            identificacion = "0987654321",
            telefono = "3007654321",
            direccion = "Inactive Address",
            estado = "INACTIVO"
        )

        // Then
        assertEquals("INACTIVO", cliente.estado)
    }

    @Test
    fun `ClienteAPI should handle different email formats`() {
        // Given
        val emails = listOf(
            "test@example.com",
            "user.name@domain.co.uk",
            "test+tag@example.org",
            "123@test.com"
        )

        emails.forEach { email ->
            // When
            val cliente = ClienteAPI(
                id = "1",
                nombre = "Test Client",
                email = email,
                identificacion = "1234567890",
                telefono = "3001234567",
                direccion = "Test Address",
                estado = "ACTIVO"
            )

            // Then
            assertEquals(email, cliente.email)
        }
    }

    @Test
    fun `ClienteAPI should handle different phone number formats`() {
        // Given
        val phones = listOf(
            "3001234567",
            "+573001234567",
            "300-123-4567",
            "300 123 4567"
        )

        phones.forEach { phone ->
            // When
            val cliente = ClienteAPI(
                id = "1",
                nombre = "Test Client",
                email = "test@example.com",
                identificacion = "1234567890",
                telefono = phone,
                direccion = "Test Address",
                estado = "ACTIVO"
            )

            // Then
            assertEquals(phone, cliente.telefono)
        }
    }

    @Test
    fun `ClienteAPI should handle different identification formats`() {
        // Given
        val identifications = listOf(
            "1234567890",
            "12.345.678-9",
            "12345678-9",
            "123456789"
        )

        identifications.forEach { identification ->
            // When
            val cliente = ClienteAPI(
                id = "1",
                nombre = "Test Client",
                email = "test@example.com",
                identificacion = identification,
                telefono = "3001234567",
                direccion = "Test Address",
                estado = "ACTIVO"
            )

            // Then
            assertEquals(identification, cliente.identificacion)
        }
    }

    @Test
    fun `ClienteAPI should handle long names`() {
        // Given
        val longName = "Cliente con un nombre muy largo que podría causar problemas de visualización en la interfaz de usuario"
        
        // When
        val cliente = ClienteAPI(
            id = "1",
            nombre = longName,
            email = "test@example.com",
            identificacion = "1234567890",
            telefono = "3001234567",
            direccion = "Test Address",
            estado = "ACTIVO"
        )

        // Then
        assertEquals(longName, cliente.nombre)
    }

    @Test
    fun `ClienteAPI should handle special characters in name`() {
        // Given
        val specialName = "Cliente José María O'Connor-Sánchez"
        
        // When
        val cliente = ClienteAPI(
            id = "1",
            nombre = specialName,
            email = "test@example.com",
            identificacion = "1234567890",
            telefono = "3001234567",
            direccion = "Test Address",
            estado = "ACTIVO"
        )

        // Then
        assertEquals(specialName, cliente.nombre)
    }

    @Test
    fun `ClienteAPI should handle complex addresses`() {
        // Given
        val complexAddress = "Carrera 7 #32-16, Oficina 201, Bogotá D.C., Colombia"
        
        // When
        val cliente = ClienteAPI(
            id = "1",
            nombre = "Test Client",
            email = "test@example.com",
            identificacion = "1234567890",
            telefono = "3001234567",
            direccion = complexAddress,
            estado = "ACTIVO"
        )

        // Then
        assertEquals(complexAddress, cliente.direccion)
    }

    @Test
    fun `ClienteAPI should handle case sensitive estado values`() {
        // Given
        val estados = listOf("ACTIVO", "INACTIVO", "activo", "inactivo", "Activo", "Inactivo")
        
        estados.forEach { estado ->
            // When
            val cliente = ClienteAPI(
                id = "1",
                nombre = "Test Client",
                email = "test@example.com",
                identificacion = "1234567890",
                telefono = "3001234567",
                direccion = "Test Address",
                estado = estado
            )

            // Then
            assertEquals(estado, cliente.estado)
        }
    }

    @Test
    fun `ClienteAPI should be immutable data class`() {
        // Given
        val cliente = ClienteAPI(
            id = "1",
            nombre = "Test Client",
            email = "test@example.com",
            identificacion = "1234567890",
            telefono = "3001234567",
            direccion = "Test Address",
            estado = "ACTIVO"
        )

        // When & Then
        // Data class should be immutable - we can't modify properties after creation
        // This test verifies that the data class is properly defined
        assertNotNull(cliente)
        assertEquals("1", cliente.id)
        assertEquals("Test Client", cliente.nombre)
    }

    @Test
    fun `ClienteAPI should support copy method`() {
        // Given
        val originalCliente = ClienteAPI(
            id = "1",
            nombre = "Original Client",
            email = "original@example.com",
            identificacion = "1234567890",
            telefono = "3001234567",
            direccion = "Original Address",
            estado = "ACTIVO"
        )

        // When
        val copiedCliente = originalCliente.copy(
            nombre = "Copied Client",
            email = "copied@example.com",
            estado = "INACTIVO"
        )

        // Then
        assertEquals("1", copiedCliente.id) // Same as original
        assertEquals("Copied Client", copiedCliente.nombre) // Changed
        assertEquals("copied@example.com", copiedCliente.email) // Changed
        assertEquals("1234567890", copiedCliente.identificacion) // Same as original
        assertEquals("3001234567", copiedCliente.telefono) // Same as original
        assertEquals("Original Address", copiedCliente.direccion) // Same as original
        assertEquals("INACTIVO", copiedCliente.estado) // Changed
    }

    @Test
    fun `ClienteAPI should support equals method`() {
        // Given
        val cliente1 = ClienteAPI(
            id = "1",
            nombre = "Test Client",
            email = "test@example.com",
            identificacion = "1234567890",
            telefono = "3001234567",
            direccion = "Test Address",
            estado = "ACTIVO"
        )

        val cliente2 = ClienteAPI(
            id = "1",
            nombre = "Test Client",
            email = "test@example.com",
            identificacion = "1234567890",
            telefono = "3001234567",
            direccion = "Test Address",
            estado = "ACTIVO"
        )

        val cliente3 = ClienteAPI(
            id = "2",
            nombre = "Different Client",
            email = "different@example.com",
            identificacion = "0987654321",
            telefono = "3007654321",
            direccion = "Different Address",
            estado = "INACTIVO"
        )

        // When & Then
        assertEquals(cliente1, cliente2)
        assertNotEquals(cliente1, cliente3)
    }

    @Test
    fun `ClienteAPI should support hashCode method`() {
        // Given
        val cliente1 = ClienteAPI(
            id = "1",
            nombre = "Test Client",
            email = "test@example.com",
            identificacion = "1234567890",
            telefono = "3001234567",
            direccion = "Test Address",
            estado = "ACTIVO"
        )

        val cliente2 = ClienteAPI(
            id = "1",
            nombre = "Test Client",
            email = "test@example.com",
            identificacion = "1234567890",
            telefono = "3001234567",
            direccion = "Test Address",
            estado = "ACTIVO"
        )

        // When & Then
        assertEquals(cliente1.hashCode(), cliente2.hashCode())
    }

    @Test
    fun `ClienteAPI should support toString method`() {
        // Given
        val cliente = ClienteAPI(
            id = "1",
            nombre = "Test Client",
            email = "test@example.com",
            identificacion = "1234567890",
            telefono = "3001234567",
            direccion = "Test Address",
            estado = "ACTIVO"
        )

        // When
        val stringRepresentation = cliente.toString()

        // Then
        assertNotNull(stringRepresentation)
        assertTrue(stringRepresentation.contains("Test Client"))
        assertTrue(stringRepresentation.contains("test@example.com"))
        assertTrue(stringRepresentation.contains("1234567890"))
        assertTrue(stringRepresentation.contains("ACTIVO"))
    }
}
