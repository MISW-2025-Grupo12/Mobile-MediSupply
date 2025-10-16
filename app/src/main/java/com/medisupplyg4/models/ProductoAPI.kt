package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName

/**
 * Model to represent a product from the API
 */
data class ProductoAPI(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val avatar: String? = null, // URL del avatar del producto
    val categoria: CategoriaAPI,
    val proveedor: ProveedorAPI,
    @SerializedName("inventario_disponible") val inventarioDisponible: Int = 0
)

/**
 * Model to represent a product category
 */
data class CategoriaAPI(
    val id: String,
    val nombre: String,
    val descripcion: String
)

/**
 * Model to represent a product supplier
 */
data class ProveedorAPI(
    val id: String,
    val nombre: String,
    val email: String,
    val direccion: String
)