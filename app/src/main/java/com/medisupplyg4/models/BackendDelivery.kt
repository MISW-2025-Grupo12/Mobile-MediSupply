package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos que representa una entrega tal como viene del backend
 */
data class BackendDelivery(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("direccion")
    val direccion: String,
    
    @SerializedName("fecha_entrega")
    val fechaEntrega: String, // ISO 8601 string
    
    @SerializedName("producto_id")
    val productoId: String,
    
    @SerializedName("cliente_id")
    val clienteId: String
)

/**
 * Modelo para la respuesta del backend (array de entregas)
 */
data class BackendDeliveryResponse(
    val deliveries: List<BackendDelivery>
)


