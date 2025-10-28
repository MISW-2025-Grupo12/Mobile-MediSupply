package com.medisupplyg4.repositories

import android.content.Context
import com.medisupplyg4.R
import com.medisupplyg4.models.LoginRequest
import com.medisupplyg4.models.LoginResponse
import com.medisupplyg4.models.UserInfo
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class LoginRepositoryTest {

    private lateinit var repository: LoginRepository
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        mockContext = mockk()
        
        // Mock context.getString calls
        every { mockContext.getString(R.string.error_server_empty_response) } returns "Empty server response"
        every { mockContext.getString(R.string.error_server_error, any(), any()) } returns "Server error: 500 - Internal Server Error"
        
        repository = LoginRepository()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `login should create LoginRequest with correct values`() {
        // Given
        val email = "test@example.com"
        val password = "password123"

        // When
        val loginRequest = LoginRequest(email = email, password = password)

        // Then
        assertEquals(email, loginRequest.email)
        assertEquals(password, loginRequest.password)
    }

    @Test
    fun `UserInfo should create with correct values`() {
        // Given
        val userInfo = UserInfo(
            email = "test@example.com",
            entidad_id = "test-id",
            id = "test-uuid",
            identificacion = "1234567890",
            tipo_usuario = "CLIENTE"
        )

        // Then
        assertEquals("test@example.com", userInfo.email)
        assertEquals("test-id", userInfo.entidad_id)
        assertEquals("test-uuid", userInfo.id)
        assertEquals("1234567890", userInfo.identificacion)
        assertEquals("CLIENTE", userInfo.tipo_usuario)
    }

    @Test
    fun `LoginResponse should create with correct values`() {
        // Given
        val userInfo = UserInfo(
            email = "test@example.com",
            entidad_id = "test-id",
            id = "test-uuid",
            identificacion = "1234567890",
            tipo_usuario = "CLIENTE"
        )
        val loginResponse = LoginResponse(
            access_token = "token",
            expires_in = 86400,
            token_type = "Bearer",
            user_info = userInfo
        )

        // Then
        assertEquals("token", loginResponse.access_token)
        assertEquals(86400, loginResponse.expires_in)
        assertEquals("Bearer", loginResponse.token_type)
        assertEquals(userInfo, loginResponse.user_info)
    }

    @Test
    fun `LoginRequest should handle different email formats`() {
        // Test various email formats
        val emails = listOf(
            "test@example.com",
            "user+tag@domain.co.uk",
            "test.email@subdomain.example.org"
        )
        
        emails.forEach { email ->
            val request = LoginRequest(email = email, password = "password")
            assertEquals(email, request.email)
        }
    }

    @Test
    fun `LoginRequest should handle different password formats`() {
        // Test various password formats
        val passwords = listOf(
            "simplepassword",
            "P@ssw0rd123",
            "password-with-special-chars!@#"
        )
        
        passwords.forEach { password ->
            val request = LoginRequest(email = "test@example.com", password = password)
            assertEquals(password, request.password)
        }
    }

    @Test
    fun `UserInfo should handle different user types`() {
        // Test all user types
        val userTypes = listOf("CLIENTE", "VENDEDOR", "REPARTIDOR")
        
        userTypes.forEach { tipoUsuario ->
            val userInfo = UserInfo(
                email = "test@example.com",
                entidad_id = "test-id",
                id = "test-uuid",
                identificacion = "1234567890",
                tipo_usuario = tipoUsuario
            )
            assertEquals(tipoUsuario, userInfo.tipo_usuario)
        }
    }
}
