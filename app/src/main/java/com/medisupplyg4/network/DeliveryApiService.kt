package com.medisupplyg4.network

import com.medisupplyg4.models.SimpleDelivery
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Servicio de API para comunicarse con el backend de entregas
 */
interface DeliveryApiService {
    
    @GET("entregas")
    suspend fun getDeliveries(
        @Query("fecha_inicio") fechaInicio: String,
        @Query("fecha_fin") fechaFin: String
    ): Response<List<SimpleDelivery>>
}
