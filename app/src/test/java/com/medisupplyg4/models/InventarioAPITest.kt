package com.medisupplyg4.models

import com.google.gson.Gson
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for InventarioAPI model
 */
class InventarioAPITest {

    private val gson = Gson()

    @Test
    fun `test InventarioAPI parsing`() {
        // Given
        val json = """
        {
            "producto_id": "bafe870b-d28e-44bb-a94b-ee2a38b25d7e",
            "total_disponible": 36,
            "total_reservado": 14,
            "lotes": [
                {
                    "fecha_vencimiento": "2025-12-31T00:00:00",
                    "cantidad_disponible": 36,
                    "cantidad_reservada": 14
                }
            ]
        }
        """.trimIndent()

        // When
        val inventario = gson.fromJson(json, InventarioAPI::class.java)

        // Then
        assertEquals("bafe870b-d28e-44bb-a94b-ee2a38b25d7e", inventario.productoId)
        assertEquals(36, inventario.totalDisponible)
        assertEquals(14, inventario.totalReservado)
        assertEquals(1, inventario.lotes.size)
        
        val lote = inventario.lotes.first()
        assertEquals("2025-12-31T00:00:00", lote.fechaVencimientoString)
        assertEquals(36, lote.cantidadDisponible)
        assertEquals(14, lote.cantidadReservada)
    }

    @Test
    fun `test InventarioAPI cantidadDisponible getter`() {
        // Given
        val inventario = InventarioAPI(
            productoId = "test-id",
            totalDisponible = 100,
            totalReservado = 20,
            lotes = emptyList()
        )

        // When
        val cantidadDisponible = inventario.cantidadDisponible

        // Then
        assertEquals(100, cantidadDisponible)
        // Should return totalDisponible directly, not subtracting totalReservado
        assertNotEquals(80, cantidadDisponible)
    }

    @Test
    fun `test InventarioAPI cantidadDisponible with zero available`() {
        // Given
        val inventario = InventarioAPI(
            productoId = "test-id",
            totalDisponible = 0,
            totalReservado = 0,
            lotes = emptyList()
        )

        // When
        val cantidadDisponible = inventario.cantidadDisponible

        // Then
        assertEquals(0, cantidadDisponible)
    }

    @Test
    fun `test InventarioAPI cantidadDisponible with reserved items`() {
        // Given
        val inventario = InventarioAPI(
            productoId = "test-id",
            totalDisponible = 50,
            totalReservado = 30,
            lotes = emptyList()
        )

        // When
        val cantidadDisponible = inventario.cantidadDisponible

        // Then
        assertEquals(50, cantidadDisponible)
        // Should return totalDisponible (50), not available minus reserved (20)
        assertNotEquals(20, cantidadDisponible)
    }

    @Test
    fun `test InventarioAPI with multiple lots`() {
        // Given
        val json = """
        {
            "producto_id": "test-id",
            "total_disponible": 100,
            "total_reservado": 25,
            "lotes": [
                {
                    "fecha_vencimiento": "2025-12-31T00:00:00",
                    "cantidad_disponible": 60,
                    "cantidad_reservada": 15
                },
                {
                    "fecha_vencimiento": "2026-01-31T00:00:00",
                    "cantidad_disponible": 40,
                    "cantidad_reservada": 10
                }
            ]
        }
        """.trimIndent()

        // When
        val inventario = gson.fromJson(json, InventarioAPI::class.java)

        // Then
        assertEquals("test-id", inventario.productoId)
        assertEquals(100, inventario.totalDisponible)
        assertEquals(25, inventario.totalReservado)
        assertEquals(2, inventario.lotes.size)
        assertEquals(100, inventario.cantidadDisponible)
    }

    @Test
    fun `test InventarioAPI with empty lots`() {
        // Given
        val inventario = InventarioAPI(
            productoId = "test-id",
            totalDisponible = 50,
            totalReservado = 10,
            lotes = emptyList()
        )

        // When
        val cantidadDisponible = inventario.cantidadDisponible

        // Then
        assertEquals(50, cantidadDisponible)
        assertTrue("Lotes should be empty", inventario.lotes.isEmpty())
    }
}
