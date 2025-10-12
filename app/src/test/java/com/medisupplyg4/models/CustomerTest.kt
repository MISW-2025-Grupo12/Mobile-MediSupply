package com.medisupplyg4.models

import org.junit.Assert.*
import org.junit.Test

class CustomerTest {

    @Test
    fun `Customer should create instance with correct properties`() {
        // Given
        val id = "customer-123"
        val name = "Test Customer"
        val address = "Test Address 123"
        val phone = "123-456-7890"
        val email = "test@example.com"
        val type = CustomerType.MEDICAL_CENTER

        // When
        val customer = Customer(
            id = id,
            name = name,
            address = address,
            phone = phone,
            email = email,
            type = type
        )

        // Then
        assertEquals(id, customer.id)
        assertEquals(name, customer.name)
        assertEquals(address, customer.address)
        assertEquals(phone, customer.phone)
        assertEquals(email, customer.email)
        assertEquals(type, customer.type)
    }
}
