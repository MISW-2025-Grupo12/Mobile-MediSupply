package com.medisupplyg4.network

import com.medisupplyg4.models.PaginatedResponse
import com.medisupplyg4.models.SellerAPI
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Servicio API para operaciones relacionadas con vendedores
 */
interface VendedorApiService {
    
    /**
     * Obtiene la lista de vendedores
     */
    @GET("usuarios/api/vendedores/")
    suspend fun getVendedores(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<PaginatedResponse<SellerAPI>>
    
    /**
     * Obtiene el detalle de un vendedor por ID
     */
    @GET("usuarios/api/vendedores/{vendedor_id}")
    suspend fun getVendedorById(
        @Header("Authorization") token: String,
        @Path("vendedor_id") vendedorId: String
    ): Response<SellerAPI>
}
