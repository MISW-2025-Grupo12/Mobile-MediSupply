package com.medisupplyg4.network

import com.medisupplyg4.models.PedidoCompletoRequest
import com.medisupplyg4.models.PedidoCompletoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API service for order operations
 */
interface PedidosApiService {
    
    @POST("ventas/api/pedidos/completo")
    suspend fun crearPedidoCompleto(
        @Body request: PedidoCompletoRequest
    ): Response<PedidoCompletoResponse>
}
