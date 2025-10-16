package com.medisupplyg4.models

import com.google.gson.Gson
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for ErrorResponse model
 */
class ErrorResponseTest {

    private val gson = Gson()

    @Test
    fun `test ErrorResponse parsing with detail`() {
        // Given
        val json = """
        {
            "success": false,
            "error": "❌ Error reservando inventario: Error reservando inventario: 400",
            "detalle": "Error inesperado durante la reserva de inventario",
            "items_pedido": [
                {
                    "producto_id": "b6a614a0-f20c-438d-baac-b52216ef56eb",
                    "nombre": "Guantes de nitrilo",
                    "cantidad": 1,
                    "precio_unitario": 25000.0,
                    "subtotal": 25000.0
                }
            ]
        }
        """.trimIndent()

        // When
        val errorResponse = gson.fromJson(json, ErrorResponse::class.java)

        // Then
        assertFalse(errorResponse.success)
        assertEquals("❌ Error reservando inventario: Error reservando inventario: 400", errorResponse.error)
        assertEquals("Error inesperado durante la reserva de inventario", errorResponse.detalle)
        assertNotNull(errorResponse.itemsPedido)
        assertEquals(1, errorResponse.itemsPedido?.size)
        
        val item = errorResponse.itemsPedido?.first()
        assertEquals("b6a614a0-f20c-438d-baac-b52216ef56eb", item?.productoId)
        assertEquals("Guantes de nitrilo", item?.nombre)
        assertEquals(1, item?.cantidad)
        assertEquals(25000.0, item?.precioUnitario ?: 0.0, 0.01)
        assertEquals(25000.0, item?.subtotal ?: 0.0, 0.01)
    }

    @Test
    fun `test ErrorResponse parsing without detail`() {
        // Given
        val json = """
        {
            "success": false,
            "error": "Error genérico del servidor"
        }
        """.trimIndent()

        // When
        val errorResponse = gson.fromJson(json, ErrorResponse::class.java)

        // Then
        assertFalse(errorResponse.success)
        assertEquals("Error genérico del servidor", errorResponse.error)
        assertNull(errorResponse.detalle)
        assertNull(errorResponse.itemsPedido)
    }

    @Test
    fun `test ErrorResponse parsing with null detail`() {
        // Given
        val json = """
        {
            "success": false,
            "error": "Error del servidor",
            "detalle": null
        }
        """.trimIndent()

        // When
        val errorResponse = gson.fromJson(json, ErrorResponse::class.java)

        // Then
        assertFalse(errorResponse.success)
        assertEquals("Error del servidor", errorResponse.error)
        assertNull(errorResponse.detalle)
    }

    @Test
    fun `test ErrorResponse parsing with empty items`() {
        // Given
        val json = """
        {
            "success": false,
            "error": "Error de validación",
            "detalle": "No hay productos en el pedido",
            "items_pedido": []
        }
        """.trimIndent()

        // When
        val errorResponse = gson.fromJson(json, ErrorResponse::class.java)

        // Then
        assertFalse(errorResponse.success)
        assertEquals("Error de validación", errorResponse.error)
        assertEquals("No hay productos en el pedido", errorResponse.detalle)
        assertNotNull(errorResponse.itemsPedido)
        assertTrue(errorResponse.itemsPedido?.isEmpty() == true)
    }
}
