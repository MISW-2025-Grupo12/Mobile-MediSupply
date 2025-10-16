package com.medisupplyg4.models

/**
 * Model to represent a product with its inventory information
 */
data class ProductoConInventario(
    val producto: ProductoAPI,
    val inventario: InventarioAPI
) {
    /**
     * Gets the available quantity for this product
     */
    val cantidadDisponible: Int
        get() = inventario.cantidadDisponible
    
    /**
     * Gets the product ID
     */
    val id: String
        get() = producto.id
    
    /**
     * Gets the product name
     */
    val nombre: String
        get() = producto.nombre
    
    /**
     * Gets the product description
     */
    val descripcion: String
        get() = producto.descripcion
    
    /**
     * Gets the product price
     */
    val precio: Double
        get() = producto.precio
    
    /**
     * Gets the product category
     */
    val categoria: CategoriaAPI
        get() = producto.categoria

    /**
     * Gets the product avatar URL
     */
    val avatar: String?
        get() = producto.avatar
}
