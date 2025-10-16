package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName

/**
 * Model to represent a visit record request
 */
data class VisitRecordRequest(
    @SerializedName("fecha_realizada") val fechaRealizada: String,
    @SerializedName("hora_realizada") val horaRealizada: String,
    @SerializedName("cliente_id") val clienteId: String,
    val novedades: String,
    @SerializedName("pedido_generado") val pedidoGenerado: Boolean
)

/**
 * Model to represent a visit record response
 */
data class VisitRecordResponse(
    val message: String,
    @SerializedName("visita_id") val visitaId: String,
    val estado: String
)
