package com.medisupplyg4.models

import org.junit.Test
import org.junit.Assert.*

class LoginRequestTest {

    @Test
    fun `LoginRequest should create with correct values`() {
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
    fun `LoginRequest should handle empty values`() {
        // Given
        val email = ""
        val password = ""

        // When
        val loginRequest = LoginRequest(email = email, password = password)

        // Then
        assertEquals("", loginRequest.email)
        assertEquals("", loginRequest.password)
    }

    @Test
    fun `LoginRequest should handle special characters`() {
        // Given
        val email = "test+tag@example.com"
        val password = "p@ssw0rd!@#"

        // When
        val loginRequest = LoginRequest(email = email, password = password)

        // Then
        assertEquals(email, loginRequest.email)
        assertEquals(password, loginRequest.password)
    }
}
