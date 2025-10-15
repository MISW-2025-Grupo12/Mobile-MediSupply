package com.medisupplyg4.network

import com.medisupplyg4.models.VisitaAPI
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Servicio API para operaciones relacionadas con visitas
 */
interface VisitasApiService {
    
    /**
     * Obtiene las visitas programadas de un vendedor específico
     * @param vendedorId ID del vendedor
     * @param fechaInicio Fecha de inicio del rango (formato: YYYY-MM-DD)
     * @param fechaFin Fecha de fin del rango (formato: YYYY-MM-DD)
     * @param estado Estado de las visitas (ej: "pendiente")
     */
    @GET("ventas/api/visitas/vendedor/{vendedor_id}")
    suspend fun getVisitasVendedor(
        @Path("vendedor_id") vendedorId: String,
        @Query("fecha_inicio") fechaInicio: String,
        @Query("fecha_fin") fechaFin: String,
        @Query("estado") estado: String = "pendiente"
    ): Response<List<VisitaAPI>>
}
