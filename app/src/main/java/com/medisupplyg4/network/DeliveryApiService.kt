package com.medisupplyg4.network

import com.medisupplyg4.models.PaginatedResponse
import com.medisupplyg4.models.SimpleDelivery
import com.medisupplyg4.models.SimpleDeliveryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Servicio de API para comunicarse con el backend de entregas
 */
interface DeliveryApiService {
    
    @GET("entregas")
    suspend fun getDeliveries(
        @Header("Authorization") token: String,
        @Query("fecha_inicio") fechaInicio: String,
        @Query("fecha_fin") fechaFin: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<PaginatedResponse<SimpleDeliveryResponse>>
    
    @GET("entregas")
    suspend fun getDeliveriesByClienteId(
        @Header("Authorization") token: String,
        @Query("cliente_id") clienteId: String,
        @Query("fecha_inicio") fechaInicio: String? = null,
        @Query("fecha_fin") fechaFin: String? = null,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 100
    ): Response<PaginatedResponse<SimpleDeliveryResponse>>
}
