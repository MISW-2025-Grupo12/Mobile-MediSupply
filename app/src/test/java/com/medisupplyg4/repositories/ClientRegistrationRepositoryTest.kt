package com.medisupplyg4.repositories

import android.content.Context
import com.medisupplyg4.R
import com.medisupplyg4.models.ClientRegistrationRequest
import com.medisupplyg4.models.ClientRegistrationResponse
import com.medisupplyg4.models.ClientRegistrationData
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class ClientRegistrationRepositoryTest {

    private lateinit var repository: ClientRegistrationRepository
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        mockContext = mockk()
        
        // Mock context.getString calls
        every { mockContext.getString(R.string.error_server_empty_response) } returns "Empty server response"
        every { mockContext.getString(R.string.error_server_error, any(), any()) } returns "Server error: 500 - Internal Server Error"
        
        repository = ClientRegistrationRepository()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `registerClient should create ClientRegistrationRequest with correct values`() {
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
        val cliente = ClientRegistrationData(
            id = "550e8400-e29b-41d4-a716-446655440000",
            nombre = "María García",
            email = "maria@example.com",
            identificacion = "0987654321",
            telefono = "3109876543",
            direccion = "Avenida 456 #78-90"
        )

        // Then
        assertEquals("550e8400-e29b-41d4-a716-446655440000", cliente.id)
        assertEquals("María García", cliente.nombre)
        assertEquals("maria@example.com", cliente.email)
        assertEquals("0987654321", cliente.identificacion)
        assertEquals("3109876543", cliente.telefono)
        assertEquals("Avenida 456 #78-90", cliente.direccion)
    }

    @Test
    fun `ClientRegistrationRequest should handle different email formats`() {
        // Test various email formats
        val emails = listOf(
            "test@example.com",
            "user+tag@domain.co.uk",
            "test.email@subdomain.example.org",
            "jose.maria@example.com"
        )
        
        emails.forEach { email ->
            val request = ClientRegistrationRequest(
                nombre = "Test User",
                email = email,
                identificacion = "1234567890",
                telefono = "3001234567",
                direccion = "Test Address",
                password = "password123"
            )
            assertEquals(email, request.email)
        }
    }

    @Test
    fun `ClientRegistrationRequest should handle different phone formats`() {
        // Test various phone formats
        val phones = listOf(
            "3001234567",
            "+573001234567",
            "+57-300-123-4567",
            "300-123-4567",
            "(300) 123-4567"
        )
        
        phones.forEach { phone ->
            val request = ClientRegistrationRequest(
                nombre = "Test User",
                email = "test@example.com",
                identificacion = "1234567890",
                telefono = phone,
                direccion = "Test Address",
                password = "password123"
            )
            assertEquals(phone, request.telefono)
        }
    }

    @Test
    fun `ClientRegistrationRequest should handle different identification formats`() {
        // Test various identification formats
        val identifications = listOf(
            "1234567890",
            "123456789012345",
            "12345678",
            "12345678901234567890"
        )
        
        identifications.forEach { identification ->
            val request = ClientRegistrationRequest(
                nombre = "Test User",
                email = "test@example.com",
                identificacion = identification,
                telefono = "3001234567",
                direccion = "Test Address",
                password = "password123"
            )
            assertEquals(identification, request.identificacion)
        }
    }

    @Test
    fun `ClientRegistrationRequest should handle different password formats`() {
        // Test various password formats
        val passwords = listOf(
            "simplepassword",
            "P@ssw0rd123",
            "password-with-special-chars!@#",
            "VeryLongPasswordWithSpecialCharacters!@#$%^&*()_+"
        )
        
        passwords.forEach { password ->
            val request = ClientRegistrationRequest(
                nombre = "Test User",
                email = "test@example.com",
                identificacion = "1234567890",
                telefono = "3001234567",
                direccion = "Test Address",
                password = password
            )
            assertEquals(password, request.password)
        }
    }

    @Test
    fun `ClientRegistrationRequest should handle different address formats`() {
        // Test various address formats
        val addresses = listOf(
            "Calle 123 #45-67",
            "Avenida 456 #78-90, Barrio El Poblado",
            "Calle 123 #45-67, Apartamento 8-901, Bogotá",
            "Carrera 7 #32-16, Oficina 201, Medellín, Antioquia"
        )
        
        addresses.forEach { address ->
            val request = ClientRegistrationRequest(
                nombre = "Test User",
                email = "test@example.com",
                identificacion = "1234567890",
                telefono = "3001234567",
                direccion = address,
                password = "password123"
            )
            assertEquals(address, request.direccion)
        }
    }

    @Test
    fun `ClientRegistrationRequest should handle different name formats`() {
        // Test various name formats
        val names = listOf(
            "Juan Pérez",
            "José María",
            "Juan Carlos Pérez González",
            "María del Carmen García López"
        )
        
        names.forEach { name ->
            val request = ClientRegistrationRequest(
                nombre = name,
                email = "test@example.com",
                identificacion = "1234567890",
                telefono = "3001234567",
                direccion = "Test Address",
                password = "password123"
            )
            assertEquals(name, request.nombre)
        }
    }
}
