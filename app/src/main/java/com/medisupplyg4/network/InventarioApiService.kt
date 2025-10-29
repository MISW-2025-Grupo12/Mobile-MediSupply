package com.medisupplyg4.network

import com.medisupplyg4.models.InventarioAPI
import com.medisupplyg4.models.PaginatedResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * API service for inventory operations
 */
interface InventarioApiService {
    
    @GET("inventario/")
    suspend fun getInventario(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<PaginatedResponse<InventarioAPI>>
    
    @GET("inventario/")
    suspend fun getInventarioArray(
        @Header("Authorization") token: String
    ): Response<List<InventarioAPI>>
}
