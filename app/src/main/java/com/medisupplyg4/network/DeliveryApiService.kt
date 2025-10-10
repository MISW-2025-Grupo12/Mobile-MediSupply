package com.medisupplyg4.network

import com.medisupplyg4.models.SimpleDelivery
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Servicio de API para comunicarse con el backend de entregas
 */
interface DeliveryApiService {
    
    @GET("entregas/")
    suspend fun getDeliveries(
        @Query("driver_id") driverId: String,
        @Query("date") date: String? = null,
        @Query("period") period: String? = null // "day", "week", "month"
    ): Response<List<SimpleDelivery>>
}
