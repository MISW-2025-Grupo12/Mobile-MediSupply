package com.medisupplyg4.network

import com.medisupplyg4.models.ClienteAPI
import retrofit2.Response
import retrofit2.http.GET

/**
 * API service for client operations
 */
interface ClientesApiService {
    
    @GET("usuarios/api/clientes/")
    suspend fun getClientes(): Response<List<ClienteAPI>>
}
