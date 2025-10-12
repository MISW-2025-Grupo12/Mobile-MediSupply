package com.medisupplyg4.models

import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SimpleDeliveryTest {

    @Test
    fun `SimpleDelivery should have correct properties`() {
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
            )
        )
        
        val pedido = PedidoAPI(
            id = "pedido-123",
            cliente = cliente,
            productos = productos
        )
        
        val delivery = SimpleDelivery(
            id = "delivery-123",
            direccion = "Calle 123 #45-67",
            fechaEntregaString = "2025-01-15T10:30:00",
            pedido = pedido
        )

        // Then
        assertEquals("delivery-123", delivery.id)
        assertEquals("Calle 123 #45-67", delivery.direccion)
        assertEquals("2025-01-15T10:30:00", delivery.fechaEntregaString)
        assertEquals(pedido, delivery.pedido)
    }

    @Test
    fun `fechaEntrega should parse date string correctly`() {
        // Given
        val cliente = ClienteAPI("Test", "3000000000", "Test", "")
        val pedido = PedidoAPI("test", cliente, emptyList())
        
        val delivery = SimpleDelivery(
            id = "test",
            direccion = "Test",
            fechaEntregaString = "2025-01-15T10:30:00",
            pedido = pedido
        )

        // When
        val fechaEntrega = delivery.fechaEntrega

        // Then
        assertNotNull(fechaEntrega)
        assertEquals(2025, fechaEntrega.year)
        assertEquals(1, fechaEntrega.monthValue)
        assertEquals(15, fechaEntrega.dayOfMonth)
        assertEquals(10, fechaEntrega.hour)
        assertEquals(30, fechaEntrega.minute)
    }

    @Test
    fun `nombreCliente should return client name`() {
        // Given
        val cliente = ClienteAPI(
            nombre = "María García",
            telefono = "3001111111",
            direccion = "Calle 456 #78-90",
            avatar = "https://example.com/maria.jpg"
        )
        val pedido = PedidoAPI("test", cliente, emptyList())
        
        val delivery = SimpleDelivery(
            id = "test",
            direccion = "Test",
            fechaEntregaString = "2025-01-15T10:30:00",
            pedido = pedido
        )

        // When
        val nombreCliente = delivery.nombreCliente

        // Then
        assertEquals("María García", nombreCliente)
    }

    @Test
    fun `telefonoCliente should return client phone`() {
        // Given
        val cliente = ClienteAPI(
            nombre = "Carlos López",
            telefono = "3002222222",
            direccion = "Calle 789 #12-34",
            avatar = "https://example.com/carlos.jpg"
        )
        val pedido = PedidoAPI("test", cliente, emptyList())
        
        val delivery = SimpleDelivery(
            id = "test",
            direccion = "Test",
            fechaEntregaString = "2025-01-15T10:30:00",
            pedido = pedido
        )

        // When
        val telefonoCliente = delivery.telefonoCliente

        // Then
        assertEquals("3002222222", telefonoCliente)
    }

    @Test
    fun `direccionCliente should return client address`() {
        // Given
        val cliente = ClienteAPI(
            nombre = "Ana Rodríguez",
            telefono = "3003333333",
            direccion = "Calle 101 #56-78",
            avatar = "https://example.com/ana.jpg"
        )
        val pedido = PedidoAPI("test", cliente, emptyList())
        
        val delivery = SimpleDelivery(
            id = "test",
            direccion = "Test",
            fechaEntregaString = "2025-01-15T10:30:00",
            pedido = pedido
        )

        // When
        val direccionCliente = delivery.direccionCliente

        // Then
        assertEquals("Calle 101 #56-78", direccionCliente)
    }

    @Test
    fun `avatarCliente should return client avatar`() {
        // Given
        val cliente = ClienteAPI(
            nombre = "Pedro Martínez",
            telefono = "3004444444",
            direccion = "Calle 202 #90-12",
            avatar = "https://example.com/pedro.jpg"
        )
        val pedido = PedidoAPI("test", cliente, emptyList())
        
        val delivery = SimpleDelivery(
            id = "test",
            direccion = "Test",
            fechaEntregaString = "2025-01-15T10:30:00",
            pedido = pedido
        )

        // When
        val avatarCliente = delivery.avatarCliente

        // Then
        assertEquals("https://example.com/pedro.jpg", avatarCliente)
    }

    @Test
    fun `productos should return products list`() {
        // Given
        val cliente = ClienteAPI("Test", "3000000000", "Test", "")
        val productos = listOf(
            ProductoAPI("Producto 1", 5, 1000, 5000, ""),
            ProductoAPI("Producto 2", 3, 2000, 6000, "")
        )
        val pedido = PedidoAPI("test", cliente, productos)
        
        val delivery = SimpleDelivery(
            id = "test",
            direccion = "Test",
            fechaEntregaString = "2025-01-15T10:30:00",
            pedido = pedido
        )

        // When
        val productosList = delivery.productos

        // Then
        assertEquals(2, productosList.size)
        assertEquals("Producto 1", productosList[0].nombre)
        assertEquals("Producto 2", productosList[1].nombre)
    }

    @Test
    fun `totalPedido should calculate total correctly`() {
        // Given
        val cliente = ClienteAPI("Test", "3000000000", "Test", "")
        val productos = listOf(
            ProductoAPI("Producto 1", 5, 1000, 5000, ""),
            ProductoAPI("Producto 2", 3, 2000, 6000, ""),
            ProductoAPI("Producto 3", 2, 1500, 3000, "")
        )
        val pedido = PedidoAPI("test", cliente, productos)
        
        val delivery = SimpleDelivery(
            id = "test",
            direccion = "Test",
            fechaEntregaString = "2025-01-15T10:30:00",
            pedido = pedido
        )

        // When
        val totalPedido = delivery.totalPedido

        // Then
        assertEquals(14000, totalPedido) // 5000 + 6000 + 3000
    }

    @Test
    fun `cantidadTotalProductos should calculate total quantity correctly`() {
        // Given
        val cliente = ClienteAPI("Test", "3000000000", "Test", "")
        val productos = listOf(
            ProductoAPI("Producto 1", 5, 1000, 5000, ""),
            ProductoAPI("Producto 2", 3, 2000, 6000, ""),
            ProductoAPI("Producto 3", 2, 1500, 3000, "")
        )
        val pedido = PedidoAPI("test", cliente, productos)
        
        val delivery = SimpleDelivery(
            id = "test",
            direccion = "Test",
            fechaEntregaString = "2025-01-15T10:30:00",
            pedido = pedido
        )

        // When
        val cantidadTotal = delivery.cantidadTotalProductos

        // Then
        assertEquals(10, cantidadTotal) // 5 + 3 + 2
    }

    @Test
    fun `fechaEntrega should handle invalid date string`() {
        // Given
        val cliente = ClienteAPI("Test", "3000000000", "Test", "")
        val pedido = PedidoAPI("test", cliente, emptyList())
        
        val delivery = SimpleDelivery(
            id = "test",
            direccion = "Test",
            fechaEntregaString = "invalid-date",
            pedido = pedido
        )

        // When
        val fechaEntrega = delivery.fechaEntrega

        // Then
        // Should return current date/time as fallback
        assertNotNull(fechaEntrega)
    }
}