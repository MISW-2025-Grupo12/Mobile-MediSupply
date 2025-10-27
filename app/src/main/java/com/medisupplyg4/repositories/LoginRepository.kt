package com.medisupplyg4.repositories

import android.content.Context
import android.util.Log
import com.medisupplyg4.R
import com.medisupplyg4.models.LoginRequest
import com.medisupplyg4.models.LoginResponse
import com.medisupplyg4.network.NetworkClient

class LoginRepository {
    companion object {
        private const val TAG = "LoginRepository"
    }

    private val loginApiService = NetworkClient.loginApiService

    suspend fun login(request: LoginRequest, context: Context): Result<LoginResponse> {
        return try {
            Log.d(TAG, "Iniciando login para: ${request.email}")
            
            val response = loginApiService.login(request)
            
            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse != null) {
                    Log.d(TAG, "Login exitoso para usuario: ${loginResponse.user_info.tipo_usuario}")
                    Result.success(loginResponse)
                } else {
                    Log.e(TAG, "Respuesta vacía del servidor")
                    Result.failure(Exception(context.getString(R.string.error_server_empty_response)))
                }
            } else {
                val errorMessage = context.getString(R.string.error_server_error, response.code(), response.message())
                Log.e(TAG, errorMessage)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error durante el login", e)
            Result.failure(e)
        }
    }
}
