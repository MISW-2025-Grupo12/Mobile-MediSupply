package com.medisupplyg4.models

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BackendDeliveryTest {

    @Test
    fun `BackendDelivery should be alias of SimpleDelivery`() {
        // Given
        val cliente = ClienteAPI(
            nombre = "Test Client",
            telefono = "3000000000",
            direccion = "Test Address",
            avatar = "https://example.com/avatar.jpg"
        )
        
        val productos = listOf(
            ProductoAPI(
                nombre = "Test Product",
                cantidad = 1,
                precioUnitario = 1000,
                subtotal = 1000,
                avatar = "https://example.com/product.jpg"
            )
        )
        
        val pedido = PedidoAPI(
            id = "test-pedido",
            cliente = cliente,
            productos = productos
        )
        
        val simpleDelivery = SimpleDelivery(
            id = "test-delivery",
            direccion = "Test Address",
            fechaEntregaString = "2025-01-15T10:00:00",
            pedido = pedido
        )

        // When
        val backendDelivery: BackendDelivery = simpleDelivery

        // Then
        assertEquals(simpleDelivery.id, backendDelivery.id)
        assertEquals(simpleDelivery.direccion, backendDelivery.direccion)
        assertEquals(simpleDelivery.fechaEntregaString, backendDelivery.fechaEntregaString)
        assertEquals(simpleDelivery.pedido, backendDelivery.pedido)
        assertEquals(simpleDelivery.nombreCliente, backendDelivery.nombreCliente)
        assertEquals(simpleDelivery.telefonoCliente, backendDelivery.telefonoCliente)
        assertEquals(simpleDelivery.direccionCliente, backendDelivery.direccionCliente)
        assertEquals(simpleDelivery.avatarCliente, backendDelivery.avatarCliente)
        assertEquals(simpleDelivery.productos, backendDelivery.productos)
        assertEquals(simpleDelivery.totalPedido, backendDelivery.totalPedido)
        assertEquals(simpleDelivery.cantidadTotalProductos, backendDelivery.cantidadTotalProductos)
    }

    @Test
    fun `BackendDelivery should have same fechaEntrega as SimpleDelivery`() {
        // Given
        val cliente = ClienteAPI("Test", "3000000000", "Test", "")
        val pedido = PedidoAPI("test", cliente, emptyList())
        
        val simpleDelivery = SimpleDelivery(
            id = "test",
            direccion = "Test",
            fechaEntregaString = "2025-01-15T10:30:00",
            pedido = pedido
        )

        // When
        val backendDelivery: BackendDelivery = simpleDelivery

        // Then
        assertEquals(simpleDelivery.fechaEntrega, backendDelivery.fechaEntrega)
    }

    @Test
    fun `BackendDelivery should work with multiple products`() {
        // Given
        val cliente = ClienteAPI("Test", "3000000000", "Test", "")
        val productos = listOf(
            ProductoAPI("Product 1", 2, 1000, 2000, ""),
            ProductoAPI("Product 2", 3, 1500, 4500, "")
        )
        val pedido = PedidoAPI("test", cliente, productos)
        
        val simpleDelivery = SimpleDelivery(
            id = "test",
            direccion = "Test",
            fechaEntregaString = "2025-01-15T10:00:00",
            pedido = pedido
        )

        // When
        val backendDelivery: BackendDelivery = simpleDelivery

        // Then
        assertEquals(6500, backendDelivery.totalPedido)
        assertEquals(5, backendDelivery.cantidadTotalProductos)
        assertEquals(2, backendDelivery.productos.size)
    }

    @Test
    fun `BackendDelivery should handle empty products list`() {
        // Given
        val cliente = ClienteAPI("Test", "3000000000", "Test", "")
        val pedido = PedidoAPI("test", cliente, emptyList())
        
        val simpleDelivery = SimpleDelivery(
            id = "test",
            direccion = "Test",
            fechaEntregaString = "2025-01-15T10:00:00",
            pedido = pedido
        )

        // When
        val backendDelivery: BackendDelivery = simpleDelivery

        // Then
        assertEquals(0, backendDelivery.totalPedido)
        assertEquals(0, backendDelivery.cantidadTotalProductos)
        assertTrue(backendDelivery.productos.isEmpty())
    }
}