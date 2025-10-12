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
        val cliente = ClienteAPI(
            nombre = "Test Client",
            telefono = "3001234567",
            direccion = "Test Address 123",
            avatar = "https://example.com/avatar.jpg"
        )
        val productos = listOf(
            ProductoAPI(
                nombre = "Paracetamol",
                cantidad = 2,
                precioUnitario = 8000,
                subtotal = 16000,
                avatar = "https://example.com/product.jpg"
            )
        )
        val pedido = PedidoAPI(
            id = "pedido-123",
            cliente = cliente,
            productos = productos
        )

        // When
        val delivery = SimpleDelivery(
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
        assertEquals("3001234567", delivery.telefonoCliente)
        assertEquals(16000, delivery.totalPedido)
        assertEquals(2, delivery.cantidadTotalProductos)
    }

    @Test
    fun `fechaEntrega should parse string correctly`() {
        // Given
        val fechaString = "2025-10-15T14:30:00"
        val expectedDateTime = LocalDateTime.of(2025, 10, 15, 14, 30)
        val pedido = createTestPedido()

        // When
        val delivery = SimpleDelivery(
            id = "test-id",
            direccion = "Test Address",
            fechaEntregaString = fechaString,
            pedido = pedido
        )

        // Then
        assertEquals(expectedDateTime, delivery.fechaEntrega)
    }

    @Test
    fun `totalPedido should calculate correctly with multiple products`() {
        // Given
        val productos = listOf(
            ProductoAPI(
                nombre = "Paracetamol",
                cantidad = 2,
                precioUnitario = 8000,
                subtotal = 16000,
                avatar = "https://example.com/p1.jpg"
            ),
            ProductoAPI(
                nombre = "Ibuprofeno",
                cantidad = 1,
                precioUnitario = 10000,
                subtotal = 10000,
                avatar = "https://example.com/p2.jpg"
            )
        )
        val pedido = PedidoAPI(
            id = "pedido-123",
            cliente = createTestCliente(),
            productos = productos
        )

        // When
        val delivery = SimpleDelivery(
            id = "test-id",
            direccion = "Test Address",
            fechaEntregaString = "2025-10-15T14:30:00",
            pedido = pedido
        )

        // Then
        assertEquals(26000, delivery.totalPedido)
        assertEquals(3, delivery.cantidadTotalProductos)
    }

    private fun createTestCliente(): ClienteAPI {
        return ClienteAPI(
            nombre = "Test Client",
            telefono = "3001234567",
            direccion = "Test Address 123",
            avatar = "https://example.com/avatar.jpg"
        )
    }

    private fun createTestPedido(): PedidoAPI {
        return PedidoAPI(
            id = "pedido-123",
            cliente = createTestCliente(),
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
    }
}
