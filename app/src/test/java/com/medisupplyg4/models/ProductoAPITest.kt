package com.medisupplyg4.models

import com.google.gson.Gson
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for ProductoAPI model with avatar support
 */
class ProductoAPITest {

    private val gson = Gson()

    @Test
    fun `test ProductoAPI parsing with avatar`() {
        // Given
        val json = """
        {
            "id": "ffd9a2c8-a2df-44d5-8eb8-7ad4371bb391",
            "nombre": "Paracetamol 500mg",
            "descripcion": "Analgésico y antipirético",
            "precio": 25000.0,
            "avatar": "https://example.com/paracetamol.jpg",
            "categoria": {
                "id": "38883ca2-3e44-431f-b39d-d32f2181d259",
                "nombre": "Medicamentos",
                "descripcion": "Productos farmacéuticos y medicamentos"
            },
            "proveedor": {
                "id": "dc7ec708-80f6-46d2-8544-8e623805945b",
                "nombre": "Bayer Colombia",
                "email": "contacto@bayer.com.co",
                "direccion": "Carrera 7 #32-16, Bogotá, Colombia"
            },
            "inventario_disponible": 100
        }
        """.trimIndent()

        // When
        val producto = gson.fromJson(json, ProductoAPI::class.java)

        // Then
        assertEquals("ffd9a2c8-a2df-44d5-8eb8-7ad4371bb391", producto.id)
        assertEquals("Paracetamol 500mg", producto.nombre)
        assertEquals("Analgésico y antipirético", producto.descripcion)
        assertEquals(25000.0, producto.precio, 0.01)
        assertEquals("https://example.com/paracetamol.jpg", producto.avatar)
        assertEquals(100, producto.inventarioDisponible)
        
        // Test categoria
        assertEquals("38883ca2-3e44-431f-b39d-d32f2181d259", producto.categoria.id)
        assertEquals("Medicamentos", producto.categoria.nombre)
        
        // Test proveedor
        assertEquals("dc7ec708-80f6-46d2-8544-8e623805945b", producto.proveedor.id)
        assertEquals("Bayer Colombia", producto.proveedor.nombre)
    }

    @Test
    fun `test ProductoAPI parsing without avatar`() {
        // Given
        val json = """
        {
            "id": "ffd9a2c8-a2df-44d5-8eb8-7ad4371bb391",
            "nombre": "Paracetamol 500mg",
            "descripcion": "Analgésico y antipirético",
            "precio": 25000.0,
            "categoria": {
                "id": "38883ca2-3e44-431f-b39d-d32f2181d259",
                "nombre": "Medicamentos",
                "descripcion": "Productos farmacéuticos y medicamentos"
            },
            "proveedor": {
                "id": "dc7ec708-80f6-46d2-8544-8e623805945b",
                "nombre": "Bayer Colombia",
                "email": "contacto@bayer.com.co",
                "direccion": "Carrera 7 #32-16, Bogotá, Colombia"
            }
        }
        """.trimIndent()

        // When
        val producto = gson.fromJson(json, ProductoAPI::class.java)

        // Then
        assertEquals("ffd9a2c8-a2df-44d5-8eb8-7ad4371bb391", producto.id)
        assertEquals("Paracetamol 500mg", producto.nombre)
        assertNull("Avatar should be null when not provided", producto.avatar)
        assertEquals(0, producto.inventarioDisponible) // Default value
    }

    @Test
    fun `test ProductoAPI parsing with null avatar`() {
        // Given
        val json = """
        {
            "id": "ffd9a2c8-a2df-44d5-8eb8-7ad4371bb391",
            "nombre": "Paracetamol 500mg",
            "descripcion": "Analgésico y antipirético",
            "precio": 25000.0,
            "avatar": null,
            "categoria": {
                "id": "38883ca2-3e44-431f-b39d-d32f2181d259",
                "nombre": "Medicamentos",
                "descripcion": "Productos farmacéuticos y medicamentos"
            },
            "proveedor": {
                "id": "dc7ec708-80f6-46d2-8544-8e623805945b",
                "nombre": "Bayer Colombia",
                "email": "contacto@bayer.com.co",
                "direccion": "Carrera 7 #32-16, Bogotá, Colombia"
            }
        }
        """.trimIndent()

        // When
        val producto = gson.fromJson(json, ProductoAPI::class.java)

        // Then
        assertEquals("ffd9a2c8-a2df-44d5-8eb8-7ad4371bb391", producto.id)
        assertEquals("Paracetamol 500mg", producto.nombre)
        assertNull("Avatar should be null when explicitly set to null", producto.avatar)
    }

    @Test
    fun `test ProductoAPI parsing with empty avatar`() {
        // Given
        val json = """
        {
            "id": "ffd9a2c8-a2df-44d5-8eb8-7ad4371bb391",
            "nombre": "Paracetamol 500mg",
            "descripcion": "Analgésico y antipirético",
            "precio": 25000.0,
            "avatar": "",
            "categoria": {
                "id": "38883ca2-3e44-431f-b39d-d32f2181d259",
                "nombre": "Medicamentos",
                "descripcion": "Productos farmacéuticos y medicamentos"
            },
            "proveedor": {
                "id": "dc7ec708-80f6-46d2-8544-8e623805945b",
                "nombre": "Bayer Colombia",
                "email": "contacto@bayer.com.co",
                "direccion": "Carrera 7 #32-16, Bogotá, Colombia"
            }
        }
        """.trimIndent()

        // When
        val producto = gson.fromJson(json, ProductoAPI::class.java)

        // Then
        assertEquals("ffd9a2c8-a2df-44d5-8eb8-7ad4371bb391", producto.id)
        assertEquals("Paracetamol 500mg", producto.nombre)
        assertEquals("", producto.avatar)
        assertTrue("Empty avatar should be considered as no avatar", producto.avatar.isNullOrEmpty())
    }
}