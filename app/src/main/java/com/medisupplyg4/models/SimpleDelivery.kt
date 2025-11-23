package com.medisupplyg4.models

import android.content.Context
import com.google.gson.annotations.SerializedName
import com.medisupplyg4.R
import java.time.LocalDateTime

/**
 * Modelo simplificado para las entregas del backend
 */
data class SimpleDelivery(
    val id: String,
    val direccion: String,
    @SerializedName("fecha_entrega") val fechaEntregaString: String,
    val cliente: ClienteAPI?,
    val productos: List<ItemPedido>,
    // Información del pedido asociado
    val pedidoId: String? = null,
    val pedidoTotal: Double? = null,
    val pedidoEstado: String? = null,
    val pedidoFechaConfirmacion: String? = null,
    val pedidoVendedorId: String? = null
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
    fun getNombreCliente(context: Context): String {
        return cliente?.nombre ?: context.getString(R.string.client_not_available)
    }
    
    /**
     * Obtiene el teléfono del cliente
     */
    fun getTelefonoCliente(context: Context): String {
        return cliente?.telefono ?: context.getString(R.string.phone_not_available)
    }

    /**
     * Obtiene el avatar del cliente
     */
    val avatarCliente: String
        get() = cliente?.avatar ?: ""

}
