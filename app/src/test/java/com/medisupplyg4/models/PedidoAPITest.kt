package com.medisupplyg4.models

import org.junit.Assert.*
import org.junit.Test

class PedidoAPITest {

    @Test
    fun `PedidoAPI should create instance with correct properties`() {
        // Given
        val id = "pedido-123"
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

        // When
        val pedido = PedidoAPI(
            id = id,
            cliente = cliente,
            productos = productos
        )

        // Then
        assertEquals(id, pedido.id)
        assertEquals(cliente, pedido.cliente)
        assertEquals(productos, pedido.productos)
        assertEquals(1, pedido.productos.size)
    }

    @Test
    fun `PedidoAPI should handle multiple products`() {
        // Given
        val id = "pedido-456"
        val cliente = createTestCliente()
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

        // When
        val pedido = PedidoAPI(
            id = id,
            cliente = cliente,
            productos = productos
        )

        // Then
        assertEquals(id, pedido.id)
        assertEquals(2, pedido.productos.size)
        assertEquals("Paracetamol", pedido.productos[0].nombre)
        assertEquals("Ibuprofeno", pedido.productos[1].nombre)
    }

    private fun createTestCliente(): ClienteAPI {
        return ClienteAPI(
            nombre = "Test Client",
            telefono = "3001234567",
            direccion = "Test Address 123",
            avatar = "https://example.com/avatar.jpg"
        )
    }
}
