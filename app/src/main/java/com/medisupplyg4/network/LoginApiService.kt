package com.medisupplyg4.network

import com.medisupplyg4.models.LoginRequest
import com.medisupplyg4.models.LoginResponse
import com.medisupplyg4.models.VersionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @GET("auth/version")
    suspend fun getVersion(): Response<VersionResponse>
}
