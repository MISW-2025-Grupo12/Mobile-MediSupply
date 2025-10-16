package com.medisupplyg4.network

import com.medisupplyg4.models.InventarioAPI
import retrofit2.Response
import retrofit2.http.GET

/**
 * API service for inventory operations
 */
interface InventarioApiService {
    
    @GET("inventario/")
    suspend fun getInventario(): Response<List<InventarioAPI>>
}
