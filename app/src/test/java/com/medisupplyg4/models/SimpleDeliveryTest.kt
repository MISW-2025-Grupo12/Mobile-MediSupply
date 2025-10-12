package com.medisupplyg4.models

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class SimpleDeliveryTest {

    @Test
    fun `SimpleDelivery should create instance with correct properties`() {
        // Given
        val id = "test-id"
        val direccion = "Test Address 123"
        val fechaEntregaString = "2025-10-15T14:30:00"
        val productoId = "product-123"
        val clienteId = "client-456"

        // When
        val delivery = SimpleDelivery(
            id = id,
            direccion = direccion,
            fechaEntregaString = fechaEntregaString,
            productoId = productoId,
            clienteId = clienteId
        )

        // Then
        assertEquals(id, delivery.id)
        assertEquals(direccion, delivery.direccion)
        assertEquals(fechaEntregaString, delivery.fechaEntregaString)
        assertEquals(productoId, delivery.productoId)
        assertEquals(clienteId, delivery.clienteId)
    }

    @Test
    fun `fechaEntrega should parse string correctly`() {
        // Given
        val fechaString = "2025-10-15T14:30:00"
        val expectedDateTime = LocalDateTime.of(2025, 10, 15, 14, 30)

        // When
        val delivery = SimpleDelivery(
            id = "test-id",
            direccion = "Test Address",
            fechaEntregaString = fechaString,
            productoId = "product-123",
            clienteId = "client-456"
        )

        // Then
        assertEquals(expectedDateTime, delivery.fechaEntrega)
    }

    @Test
    fun `fechaEntrega should handle different date formats`() {
        // Given
        val fechaString = "2025-12-25T09:15:30"
        val expectedDateTime = LocalDateTime.of(2025, 12, 25, 9, 15, 30)

        // When
        val delivery = SimpleDelivery(
            id = "test-id",
            direccion = "Test Address",
            fechaEntregaString = fechaString,
            productoId = "product-123",
            clienteId = "client-456"
        )

        // Then
        assertEquals(expectedDateTime, delivery.fechaEntrega)
    }
}
