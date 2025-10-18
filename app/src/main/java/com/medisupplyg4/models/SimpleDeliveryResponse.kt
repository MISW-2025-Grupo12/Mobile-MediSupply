package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo alternativo para manejar diferentes estructuras de respuesta de entregas
 */
data class SimpleDeliveryResponse(
    val id: String,
    val direccion: String,
    @SerializedName("fecha_entrega") val fechaEntregaString: String,
    
    // Campos directos del cliente (si vienen en la respuesta)
    @SerializedName("cliente_id") val clienteId: String? = null,
    @SerializedName("cliente_nombre") val clienteNombre: String? = null,
    @SerializedName("cliente_telefono") val clienteTelefono: String? = null,
    @SerializedName("cliente_email") val clienteEmail: String? = null,
    
    // Objeto cliente anidado (estructura original)
    val cliente: ClienteAPI? = null,
    
    // Estructura real de la API: pedido.cliente
    val pedido: PedidoResponse? = null,
    
    val productos: List<ItemPedido>? = null
) {
    
    /**
     * Modelo para la estructura de pedido en la respuesta
     */
    data class PedidoResponse(
        val id: String,
        val cliente: ClienteAPI,
        val productos: List<ItemPedido>
    )
    /**
     * Obtiene el nombre del cliente desde cualquier estructura
     */
    fun getNombreCliente(): String? {
        return clienteNombre ?: cliente?.nombre ?: pedido?.cliente?.nombre
    }
    
    /**
     * Obtiene el teléfono del cliente desde cualquier estructura
     */
    fun getTelefonoCliente(): String? {
        return clienteTelefono ?: cliente?.telefono ?: pedido?.cliente?.telefono
    }
    
    /**
     * Obtiene el email del cliente desde cualquier estructura
     */
    fun getEmailCliente(): String? {
        return clienteEmail ?: cliente?.email ?: pedido?.cliente?.email
    }
    
    /**
     * Convierte a SimpleDelivery estándar
     */
    fun toSimpleDelivery(): SimpleDelivery {
        val clienteAPI = when {
            // Estructura real de la API: pedido.cliente
            pedido?.cliente != null -> pedido!!.cliente
            // Estructura original: cliente directo
            cliente != null -> cliente
            // Campos directos
            clienteId != null && clienteNombre != null -> {
                ClienteAPI(
                    id = clienteId,
                    nombre = clienteNombre,
                    email = getEmailCliente() ?: "",
                    telefono = getTelefonoCliente() ?: "",
                    direccion = direccion
                )
            }
            else -> null
        }
        
        // Usar productos del pedido si están disponibles, sino los directos
        val productosFinales = pedido?.productos ?: productos ?: emptyList()
        
        return SimpleDelivery(
            id = id,
            direccion = direccion,
            fechaEntregaString = fechaEntregaString,
            cliente = clienteAPI,
            productos = productosFinales
        )
    }
}
