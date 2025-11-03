package com.medisupplyg4.network

import com.medisupplyg4.models.PedidoClienteAPI
import com.medisupplyg4.models.PaginatedResponse
import com.medisupplyg4.models.PedidoCompletoRequest
import com.medisupplyg4.models.PedidoCompletoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * API service for order operations
 */
interface PedidosApiService {
    
    @POST("ventas/api/pedidos/completo")
    suspend fun crearPedidoCompleto(
        @Header("Authorization") token: String,
        @Body request: PedidoCompletoRequest
    ): Response<PedidoCompletoResponse>

    /** Lista de pedidos por cliente */
    @GET("ventas/api/pedidos/")
    suspend fun getPedidosCliente(
        @Header("Authorization") token: String,
        @Query("cliente_id") clienteId: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<PaginatedResponse<PedidoClienteAPI>>
}
