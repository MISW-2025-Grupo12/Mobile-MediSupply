package com.medisupplyg4.network

import com.medisupplyg4.models.ProductoAPI
import retrofit2.Response
import retrofit2.http.GET

/**
 * API service for product operations
 */
interface ProductosApiService {
    
    @GET("productos/api/productos/")
    suspend fun getProductos(): Response<List<ProductoAPI>>
}
