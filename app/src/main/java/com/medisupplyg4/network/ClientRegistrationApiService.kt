package com.medisupplyg4.network

import com.medisupplyg4.models.ClientRegistrationRequest
import com.medisupplyg4.models.ClientRegistrationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ClientRegistrationApiService {
    @POST("usuarios/api/auth/registro-cliente")
    suspend fun registerClient(
        @Body request: ClientRegistrationRequest
    ): Response<ClientRegistrationResponse>
}
