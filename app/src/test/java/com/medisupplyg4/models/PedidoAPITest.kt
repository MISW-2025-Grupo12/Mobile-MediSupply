package com.medisupplyg4.models

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PedidoAPITest {

    @Test
    fun `PedidoAPI should have correct properties`() {
        // Given
        val cliente = ClienteAPI(
            nombre = "Juan Pérez",
            telefono = "3001234567",
            direccion = "Calle 123 #45-67",
            avatar = "https://example.com/avatar.jpg"
        )
        
        val productos = listOf(
            ProductoAPI(
                nombre = "Paracetamol",
                cantidad = 10,
                precioUnitario = 5000,
                subtotal = 50000,
                avatar = "https://example.com/product1.jpg"
            ),
            ProductoAPI(
                nombre = "Ibuprofeno",
                cantidad = 5,
                precioUnitario = 8000,
                subtotal = 40000,
                avatar = "https://example.com/product2.jpg"
            )
        )
        
        val pedido = PedidoAPI(
            id = "pedido-123",
            cliente = cliente,
            productos = productos
        )

        // Then
        assertEquals("pedido-123", pedido.id)
        assertEquals(cliente, pedido.cliente)
        assertEquals(productos, pedido.productos)
        assertEquals(2, pedido.productos.size)
    }

    @Test
    fun `PedidoAPI should handle empty product list`() {
        // Given
        val cliente = ClienteAPI(
            nombre = "Cliente Test",
            telefono = "3000000000",
            direccion = "Dirección Test",
            avatar = ""
        )
        
        val pedido = PedidoAPI(
            id = "pedido-vacio",
            cliente = cliente,
            productos = emptyList()
        )

        // Then
        assertEquals("pedido-vacio", pedido.id)
        assertEquals(cliente, pedido.cliente)
        assertEquals(0, pedido.productos.size)
    }

    @Test
    fun `PedidoAPI should handle single product`() {
        // Given
        val cliente = ClienteAPI(
            nombre = "Cliente Único",
            telefono = "3001111111",
            direccion = "Dirección Única",
            avatar = "https://example.com/unique.jpg"
        )
        
        val producto = ProductoAPI(
            nombre = "Producto Único",
            cantidad = 1,
            precioUnitario = 10000,
            subtotal = 10000,
            avatar = "https://example.com/unique-product.jpg"
        )
        
        val pedido = PedidoAPI(
            id = "pedido-unico",
            cliente = cliente,
            productos = listOf(producto)
        )

        // Then
        assertEquals("pedido-unico", pedido.id)
        assertEquals(cliente, pedido.cliente)
        assertEquals(1, pedido.productos.size)
        assertEquals(producto, pedido.productos[0])
    }

    @Test
    fun `PedidoAPI should handle multiple products`() {
        // Given
        val cliente = ClienteAPI(
            nombre = "Cliente Múltiple",
            telefono = "3002222222",
            direccion = "Dirección Múltiple",
            avatar = "https://example.com/multiple.jpg"
        )
        
        val productos = (1..5).map { i ->
            ProductoAPI(
                nombre = "Producto $i",
                cantidad = i,
                precioUnitario = i * 1000,
                subtotal = i * i * 1000,
                avatar = "https://example.com/product$i.jpg"
            )
        }
        
        val pedido = PedidoAPI(
            id = "pedido-multiple",
            cliente = cliente,
            productos = productos
        )

        // Then
        assertEquals("pedido-multiple", pedido.id)
        assertEquals(cliente, pedido.cliente)
        assertEquals(5, pedido.productos.size)
        assertEquals("Producto 1", pedido.productos[0].nombre)
        assertEquals("Producto 5", pedido.productos[4].nombre)
    }
}