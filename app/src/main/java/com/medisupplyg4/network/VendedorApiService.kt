package com.medisupplyg4.network

import com.medisupplyg4.models.SellerAPI
import retrofit2.Response
import retrofit2.http.GET

/**
 * Servicio API para operaciones relacionadas con vendedores
 */
interface VendedorApiService {
    
    /**
     * Obtiene la lista de vendedores
     */
    @GET("usuarios/api/vendedores/")
    suspend fun getVendedores(): Response<List<SellerAPI>>
}
