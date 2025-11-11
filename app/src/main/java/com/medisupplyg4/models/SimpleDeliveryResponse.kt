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
        val total: Double,
        val estado: String,
        @SerializedName("fecha_confirmacion") val fechaConfirmacion: String? = null,
        @SerializedName("vendedor_id") val vendedorId: String? = null,
        val cliente: ClienteAPI,
        val productos: List<ItemPedidoEntrega>
    )
    
    /**
     * Modelo para los productos en la respuesta de entregas
     */
    data class ItemPedidoEntrega(
        @SerializedName("producto_id") val productoId: String,
        val nombre: String,
        val cantidad: Int,
        @SerializedName("precio_unitario") val precioUnitario: Double,
        val subtotal: Double,
        val avatar: String? = null
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
                    identificacion = "", // Valor por defecto
                    telefono = getTelefonoCliente() ?: "",
                    direccion = direccion,
                    estado = "ACTIVO" // Valor por defecto
                )
            }
            else -> null
        }
        
        // Convertir productos de la respuesta a ItemPedido
        // Primero intentar usar productos del pedido (ItemPedidoEntrega)
        val productosFinales = when {
            pedido?.productos != null -> {
                // Convertir ItemPedidoEntrega a ItemPedido
                pedido.productos.map { itemEntrega ->
                    // Crear un ProductoAPI mínimo desde ItemPedidoEntrega
                    val productoAPI = ProductoAPI(
                        id = itemEntrega.productoId,
                        nombre = itemEntrega.nombre,
                        descripcion = "",
                        precio = itemEntrega.precioUnitario,
                        avatar = itemEntrega.avatar,
                        categoria = CategoriaAPI("", "", ""),
                        proveedor = ProveedorAPI("", "", "", ""),
                        inventarioDisponible = 0
                    )
                    ItemPedido(
                        producto = productoAPI,
                        cantidad = itemEntrega.cantidad
                    )
                }
            }
            productos != null -> productos
            else -> emptyList()
        }
        
        return SimpleDelivery(
            id = id,
            direccion = direccion,
            fechaEntregaString = fechaEntregaString,
            cliente = clienteAPI,
            productos = productosFinales,
            pedidoId = pedido?.id,
            pedidoTotal = pedido?.total,
            pedidoEstado = pedido?.estado,
            pedidoFechaConfirmacion = pedido?.fechaConfirmacion,
            pedidoVendedorId = pedido?.vendedorId
        )
    }
}
