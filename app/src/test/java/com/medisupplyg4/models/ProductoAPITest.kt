package com.medisupplyg4.models

import org.junit.Test
import kotlin.test.assertEquals

class ProductoAPITest {

    @Test
    fun `ProductoAPI should have correct properties`() {
        // Given
        val producto = ProductoAPI(
            nombre = "Paracetamol",
            cantidad = 10,
            precioUnitario = 5000,
            subtotal = 50000,
            avatar = "https://example.com/product.jpg"
        )

        // Then
        assertEquals("Paracetamol", producto.nombre)
        assertEquals(10, producto.cantidad)
        assertEquals(5000, producto.precioUnitario)
        assertEquals(50000, producto.subtotal)
        assertEquals("https://example.com/product.jpg", producto.avatar)
    }

    @Test
    fun `ProductoAPI should handle zero values`() {
        // Given
        val producto = ProductoAPI(
            nombre = "Producto Test",
            cantidad = 0,
            precioUnitario = 0,
            subtotal = 0,
            avatar = ""
        )

        // Then
        assertEquals("Producto Test", producto.nombre)
        assertEquals(0, producto.cantidad)
        assertEquals(0, producto.precioUnitario)
        assertEquals(0, producto.subtotal)
        assertEquals("", producto.avatar)
    }

    @Test
    fun `ProductoAPI should handle large numbers`() {
        // Given
        val producto = ProductoAPI(
            nombre = "Medicamento Caro",
            cantidad = 1000,
            precioUnitario = 1000000,
            subtotal = 1000000000,
            avatar = "https://example.com/expensive.jpg"
        )

        // Then
        assertEquals("Medicamento Caro", producto.nombre)
        assertEquals(1000, producto.cantidad)
        assertEquals(1000000, producto.precioUnitario)
        assertEquals(1000000000, producto.subtotal)
        assertEquals("https://example.com/expensive.jpg", producto.avatar)
    }

    @Test
    fun `ProductoAPI should handle special characters in name`() {
        // Given
        val producto = ProductoAPI(
            nombre = "Ibuprofeno 400mg - Tabletas",
            cantidad = 5,
            precioUnitario = 8000,
            subtotal = 40000,
            avatar = "https://example.com/ibuprofeno.jpg"
        )

        // Then
        assertEquals("Ibuprofeno 400mg - Tabletas", producto.nombre)
        assertEquals(5, producto.cantidad)
        assertEquals(8000, producto.precioUnitario)
        assertEquals(40000, producto.subtotal)
        assertEquals("https://example.com/ibuprofeno.jpg", producto.avatar)
    }
}