package com.medisupplyg4.models

import org.junit.Assert.*
import org.junit.Test

class ProductoAPITest {

    @Test
    fun `ProductoAPI should create instance with correct properties`() {
        // Given
        val nombre = "Paracetamol"
        val cantidad = 2
        val precioUnitario = 8000
        val subtotal = 16000
        val avatar = "https://example.com/product.jpg"

        // When
        val producto = ProductoAPI(
            nombre = nombre,
            cantidad = cantidad,
            precioUnitario = precioUnitario,
            subtotal = subtotal,
            avatar = avatar
        )

        // Then
        assertEquals(nombre, producto.nombre)
        assertEquals(cantidad, producto.cantidad)
        assertEquals(precioUnitario, producto.precioUnitario)
        assertEquals(subtotal, producto.subtotal)
        assertEquals(avatar, producto.avatar)
    }

    @Test
    fun `ProductoAPI should handle different product types`() {
        // Given
        val medicamento = ProductoAPI(
            nombre = "Ibuprofeno",
            cantidad = 1,
            precioUnitario = 10000,
            subtotal = 10000,
            avatar = "https://example.com/ibuprofeno.jpg"
        )

        // When & Then
        assertEquals("Ibuprofeno", medicamento.nombre)
        assertEquals(1, medicamento.cantidad)
        assertEquals(10000, medicamento.precioUnitario)
        assertEquals(10000, medicamento.subtotal)
    }
}
