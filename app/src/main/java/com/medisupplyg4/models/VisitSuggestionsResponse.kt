package com.medisupplyg4.models

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de sugerencias de visita
 */
data class VisitSuggestionsResponse(
    val mensaje: String,
    @SerializedName("visita_id") val visitaId: String,
    val sugerencia: SugerenciaVisita
)

/**
 * Modelo de sugerencia de visita
 */
data class SugerenciaVisita(
    val id: String,
    @SerializedName("cliente_id") val clienteId: String,
    @SerializedName("evidencia_id") val evidenciaId: String? = null,
    @SerializedName("sugerencias_texto") val sugerenciasTexto: String,
    @SerializedName("modelo_usado") val modeloUsado: String,
    @SerializedName("created_at") val createdAt: String
)

