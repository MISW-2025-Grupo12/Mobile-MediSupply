package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

/**
 * Modelo simplificado para las entregas del backend
 */
data class SimpleDelivery(
    val id: String,
    val direccion: String,
    @SerializedName("fecha_entrega") val fechaEntregaString: String,
    val pedido: PedidoAPI
) {
    /**
     * Convierte la fecha string a LocalDateTime
     */
    val fechaEntrega: LocalDateTime
        get() = try {
            LocalDateTime.parse(fechaEntregaString)
        } catch (e: Exception) {
            LocalDateTime.now() // Fallback si hay error en el parsing
        }
    
    /**
     * Obtiene el nombre del cliente
     */
    val nombreCliente: String
        get() = pedido.cliente.nombre
    
    /**
     * Obtiene el teléfono del cliente
     */
    val telefonoCliente: String
        get() = pedido.cliente.telefono
    
    /**
     * Obtiene la dirección del cliente
     */
    val direccionCliente: String
        get() = pedido.cliente.direccion
    
    /**
     * Obtiene el avatar del cliente
     */
    val avatarCliente: String
        get() = pedido.cliente.avatar
    
    /**
     * Obtiene la lista de productos
     */
    val productos: List<ProductoAPI>
        get() = pedido.productos
    
    /**
     * Obtiene el total del pedido
     */
    val totalPedido: Int
        get() = pedido.productos.sumOf { it.subtotal }
    
    /**
     * Obtiene la cantidad total de productos
     */
    val cantidadTotalProductos: Int
        get() = pedido.productos.sumOf { it.cantidad }
}
