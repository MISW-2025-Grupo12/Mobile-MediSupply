package com.medisupplyg4.network

import com.medisupplyg4.models.ClienteAPI
import com.medisupplyg4.models.PaginatedResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API service for client operations
 */
interface ClientesApiService {
    
    @GET("usuarios/api/clientes/")
    suspend fun getClientes(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<PaginatedResponse<ClienteAPI>>
    
    /**
     * Obtiene el detalle de un cliente por ID
     */
    @GET("usuarios/api/clientes/{cliente_id}")
    suspend fun getClienteById(
        @Header("Authorization") token: String,
        @Path("cliente_id") clienteId: String
    ): Response<ClienteAPI>
}
