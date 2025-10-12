package com.medisupplyg4.models

import org.junit.Assert.*
import org.junit.Test

class BackendDeliveryTest {

    @Test
    fun `BackendDelivery should create instance with correct properties`() {
        // Given
        val id = "test-id"
        val direccion = "Test Address 123"
        val fechaEntrega = "2025-10-15T14:30:00"
        val productoId = "product-123"
        val clienteId = "client-456"

        // When
        val delivery = BackendDelivery(
            id = id,
            direccion = direccion,
            fechaEntrega = fechaEntrega,
            productoId = productoId,
            clienteId = clienteId
        )

        // Then
        assertEquals(id, delivery.id)
        assertEquals(direccion, delivery.direccion)
        assertEquals(fechaEntrega, delivery.fechaEntrega)
        assertEquals(productoId, delivery.productoId)
        assertEquals(clienteId, delivery.clienteId)
    }
}

