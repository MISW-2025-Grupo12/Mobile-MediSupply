package com.medisupplyg4.models

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for ProductoConInventario model
 */
class ProductoConInventarioTest {

    @Test
    fun `test ProductoConInventario avatar access`() {
        // Given
        val producto = ProductoAPI(
            id = "test-id",
            nombre = "Test Product",
            descripcion = "Test Description",
            precio = 100.0,
            avatar = "https://example.com/test.jpg",
            categoria = CategoriaAPI("cat-id", "Category", "Category Description"),
            proveedor = ProveedorAPI("prov-id", "Provider", "provider@test.com", "Test Address"),
            inventarioDisponible = 0
        )
        
        val inventario = InventarioAPI(
            productoId = "test-id",
            totalDisponible = 50,
            totalReservado = 10,
            lotes = emptyList()
        )
        
        val productoConInventario = ProductoConInventario(producto, inventario)

        // When & Then
        assertEquals("https://example.com/test.jpg", productoConInventario.avatar)
        assertEquals("test-id", productoConInventario.id)
        assertEquals("Test Product", productoConInventario.nombre)
        assertEquals(50, productoConInventario.cantidadDisponible)
    }

    @Test
    fun `test ProductoConInventario with null avatar`() {
        // Given
        val producto = ProductoAPI(
            id = "test-id",
            nombre = "Test Product",
            descripcion = "Test Description",
            precio = 100.0,
            avatar = null,
            categoria = CategoriaAPI("cat-id", "Category", "Category Description"),
            proveedor = ProveedorAPI("prov-id", "Provider", "provider@test.com", "Test Address"),
            inventarioDisponible = 0
        )
        
        val inventario = InventarioAPI(
            productoId = "test-id",
            totalDisponible = 50,
            totalReservado = 10,
            lotes = emptyList()
        )
        
        val productoConInventario = ProductoConInventario(producto, inventario)

        // When & Then
        assertNull("Avatar should be null", productoConInventario.avatar)
        assertEquals("test-id", productoConInventario.id)
        assertEquals("Test Product", productoConInventario.nombre)
        assertEquals(50, productoConInventario.cantidadDisponible)
    }

    @Test
    fun `test ProductoConInventario with empty avatar`() {
        // Given
        val producto = ProductoAPI(
            id = "test-id",
            nombre = "Test Product",
            descripcion = "Test Description",
            precio = 100.0,
            avatar = "",
            categoria = CategoriaAPI("cat-id", "Category", "Category Description"),
            proveedor = ProveedorAPI("prov-id", "Provider", "provider@test.com", "Test Address"),
            inventarioDisponible = 0
        )
        
        val inventario = InventarioAPI(
            productoId = "test-id",
            totalDisponible = 50,
            totalReservado = 10,
            lotes = emptyList()
        )
        
        val productoConInventario = ProductoConInventario(producto, inventario)

        // When & Then
        assertEquals("", productoConInventario.avatar)
        assertTrue("Empty avatar should be considered as no avatar", productoConInventario.avatar.isNullOrEmpty())
        assertEquals("test-id", productoConInventario.id)
        assertEquals("Test Product", productoConInventario.nombre)
        assertEquals(50, productoConInventario.cantidadDisponible)
    }

    @Test
    fun `test ProductoConInventario cantidadDisponible calculation`() {
        // Given
        val producto = ProductoAPI(
            id = "test-id",
            nombre = "Test Product",
            descripcion = "Test Description",
            precio = 100.0,
            avatar = "https://example.com/test.jpg",
            categoria = CategoriaAPI("cat-id", "Category", "Category Description"),
            proveedor = ProveedorAPI("prov-id", "Provider", "provider@test.com", "Test Address"),
            inventarioDisponible = 0
        )
        
        val inventario = InventarioAPI(
            productoId = "test-id",
            totalDisponible = 100,
            totalReservado = 20,
            lotes = emptyList()
        )
        
        val productoConInventario = ProductoConInventario(producto, inventario)

        // When & Then
        assertEquals(100, productoConInventario.cantidadDisponible)
        // Should return totalDisponible directly, not subtracting totalReservado
        assertNotEquals(80, productoConInventario.cantidadDisponible)
    }

    @Test
    fun `test ProductoConInventario categoria access`() {
        // Given
        val categoria = CategoriaAPI("cat-id", "Medicamentos", "Productos farmacéuticos")
        val producto = ProductoAPI(
            id = "test-id",
            nombre = "Test Product",
            descripcion = "Test Description",
            precio = 100.0,
            avatar = "https://example.com/test.jpg",
            categoria = categoria,
            proveedor = ProveedorAPI("prov-id", "Provider", "provider@test.com", "Test Address"),
            inventarioDisponible = 0
        )
        
        val inventario = InventarioAPI(
            productoId = "test-id",
            totalDisponible = 50,
            totalReservado = 10,
            lotes = emptyList()
        )
        
        val productoConInventario = ProductoConInventario(producto, inventario)

        // When & Then
        assertEquals(categoria, productoConInventario.categoria)
        assertEquals("cat-id", productoConInventario.categoria.id)
        assertEquals("Medicamentos", productoConInventario.categoria.nombre)
    }
}
