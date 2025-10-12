package com.medisupplyg4.models

import org.junit.Assert.*
import org.junit.Test

class BackendDeliveryTest {

    @Test
    fun `BackendDelivery should be alias of SimpleDelivery`() {
        // Given
        val id = "test-id"
        val direccion = "Test Address 123"
        val fechaEntregaString = "2025-10-15T14:30:00"
        val pedido = PedidoAPI(
            id = "pedido-123",
            cliente = ClienteAPI(
                nombre = "Test Client",
                telefono = "3001234567",
                direccion = "Test Address 123",
                avatar = "https://example.com/avatar.jpg"
            ),
            productos = listOf(
                ProductoAPI(
                    nombre = "Test Product",
                    cantidad = 1,
                    precioUnitario = 10000,
                    subtotal = 10000,
                    avatar = "https://example.com/product.jpg"
                )
            )
        )

        // When
        val delivery = BackendDelivery(
            id = id,
            direccion = direccion,
            fechaEntregaString = fechaEntregaString,
            pedido = pedido
        )

        // Then
        assertEquals(id, delivery.id)
        assertEquals(direccion, delivery.direccion)
        assertEquals(fechaEntregaString, delivery.fechaEntregaString)
        assertEquals(pedido, delivery.pedido)
        assertEquals("Test Client", delivery.nombreCliente)
    }
}

