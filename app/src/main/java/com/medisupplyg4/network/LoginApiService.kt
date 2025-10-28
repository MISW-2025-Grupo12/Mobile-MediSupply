package com.medisupplyg4.network

import com.medisupplyg4.models.LoginRequest
import com.medisupplyg4.models.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
