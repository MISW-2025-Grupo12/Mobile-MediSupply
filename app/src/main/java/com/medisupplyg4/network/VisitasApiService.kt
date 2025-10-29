package com.medisupplyg4.network

import com.medisupplyg4.models.PaginatedResponse
import com.medisupplyg4.models.VisitAPI
import com.medisupplyg4.models.VisitRecordRequest
import com.medisupplyg4.models.VisitRecordResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Servicio API para operaciones relacionadas con visitas
 */
interface VisitasApiService {
    
    /**
     * Obtiene las visitas programadas de un vendedor específico
     * @param token Token de autenticación JWT
     * @param vendedorId ID del vendedor
     * @param fechaInicio Fecha de inicio del rango (formato: YYYY-MM-DD)
     * @param fechaFin Fecha de fin del rango (formato: YYYY-MM-DD)
     * @param estado Estado de las visitas (ej: "pendiente")
     * @param page Número de página (por defecto: 1)
     * @param pageSize Tamaño de página (por defecto: 20)
     */
    @GET("ventas/api/visitas/vendedor/{vendedor_id}")
    suspend fun getVisitasVendedor(
        @Header("Authorization") token: String,
        @Path("vendedor_id") vendedorId: String,
        @Query("fecha_inicio") fechaInicio: String,
        @Query("fecha_fin") fechaFin: String,
        @Query("estado") estado: String = "pendiente",
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<PaginatedResponse<VisitAPI>>
    
    /**
     * Registra una visita completada
     * @param token Token de autenticación JWT
     * @param visitaId ID de la visita a registrar
     * @param request Datos del registro de la visita
     */
    @PUT("ventas/api/visitas/{visita_id}")
    suspend fun recordVisit(
        @Header("Authorization") token: String,
        @Path("visita_id") visitaId: String,
        @Body request: VisitRecordRequest
    ): Response<VisitRecordResponse>
}
