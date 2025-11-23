package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Modelo para el detalle completo de una ruta con todas sus entregas
 */
data class RouteDetail(
    val id: String,
    @SerializedName("fecha_ruta") val fechaRuta: String,
    @SerializedName("repartidor_id") val repartidorId: String,
    @SerializedName("bodega_id") val bodegaId: String,
    val estado: String,
    val entregas: List<EntregaDetail>,
    val bodega: BodegaDetail
) {
    val fechaRutaLocalDate: LocalDate
        get() = try {
            LocalDate.parse(fechaRuta, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            LocalDate.now()
        }
}

/**
 * Modelo para una entrega dentro de una ruta
 */
data class EntregaDetail(
    val id: String,
    val direccion: String,
    @SerializedName("fecha_entrega") val fechaEntrega: String,
    val pedido: PedidoDetail
) {
    val fechaEntregaLocalDateTime: LocalDateTime
        get() = try {
            LocalDateTime.parse(fechaEntrega, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: Exception) {
            LocalDateTime.now()
        }
}

/**
 * Modelo para el pedido dentro de una entrega
 */
data class PedidoDetail(
    val id: String,
    val total: Double,
    val estado: String,
    @SerializedName("fecha_confirmacion") val fechaConfirmacion: String,
    @SerializedName("vendedor_id") val vendedorId: String,
    val cliente: ClienteAPI,
    val productos: List<ProductoPedidoDetail>
) {
    val fechaConfirmacionLocalDateTime: LocalDateTime
        get() = try {
            LocalDateTime.parse(fechaConfirmacion, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: Exception) {
            LocalDateTime.now()
        }
}

/**
 * Modelo para un producto dentro de un pedido
 */
data class ProductoPedidoDetail(
    @SerializedName("producto_id") val productoId: String,
    val nombre: String,
    val cantidad: Int,
    @SerializedName("precio_unitario") val precioUnitario: Double,
    val subtotal: Double,
    val avatar: String? = null
)

/**
 * Modelo para la bodega de una ruta
 */
data class BodegaDetail(
    val id: String,
    val nombre: String,
    val direccion: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

