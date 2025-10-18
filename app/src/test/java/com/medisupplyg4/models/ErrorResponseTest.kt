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
            "error": "❌ Problemas con productos del pedido: 1 no existen",
            "detalle": "No se puede crear el pedido debido a problemas con los productos",
            "items_con_problemas": [
                {
                    "producto_id": "b6a614a0-f20c-438d-baac-b52216ef56eb",
                    "nombre": "Guantes de nitrilo",
                    "cantidad_solicitada": 1,
                    "cantidad_disponible": 0,
                    "problema": "no_existe_inventario",
                    "mensaje": "El producto no existe en el inventario"
                }
            ],
            "items_validos": [],
            "resumen": {
                "total_items_solicitados": 1,
                "items_validos": 0,
                "items_con_problemas": 1,
                "productos_no_existen": 1,
                "productos_stock_insuficiente": 0,
                "productos_precio_invalido": 0
            }
        }
        """.trimIndent()

        // When
        val errorResponse = gson.fromJson(json, ErrorResponse::class.java)

        // Then
        assertFalse(errorResponse.success)
        assertEquals("❌ Problemas con productos del pedido: 1 no existen", errorResponse.error)
        assertEquals("No se puede crear el pedido debido a problemas con los productos", errorResponse.detalle)
        assertNotNull(errorResponse.itemsConProblemas)
        assertEquals(1, errorResponse.itemsConProblemas?.size)
        
        val item = errorResponse.itemsConProblemas?.first()
        assertEquals("b6a614a0-f20c-438d-baac-b52216ef56eb", item?.productoId)
        assertEquals("Guantes de nitrilo", item?.nombre)
        assertEquals(1, item?.cantidadSolicitada)
        assertEquals(0, item?.cantidadDisponible)
        assertEquals("no_existe_inventario", item?.problema)
        assertEquals("El producto no existe en el inventario", item?.mensaje)
        
        // Test resumen
        assertNotNull(errorResponse.resumen)
        assertEquals(1, errorResponse.resumen?.totalItemsSolicitados)
        assertEquals(0, errorResponse.resumen?.itemsValidos)
        assertEquals(1, errorResponse.resumen?.itemsConProblemas)
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
        assertNull(errorResponse.itemsConProblemas)
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
            "items_con_problemas": [],
            "items_validos": []
        }
        """.trimIndent()

        // When
        val errorResponse = gson.fromJson(json, ErrorResponse::class.java)

        // Then
        assertFalse(errorResponse.success)
        assertEquals("Error de validación", errorResponse.error)
        assertEquals("No hay productos en el pedido", errorResponse.detalle)
        assertNotNull(errorResponse.itemsConProblemas)
        assertTrue(errorResponse.itemsConProblemas?.isEmpty() == true)
        assertNotNull(errorResponse.itemsValidos)
        assertTrue(errorResponse.itemsValidos?.isEmpty() == true)
    }
}
