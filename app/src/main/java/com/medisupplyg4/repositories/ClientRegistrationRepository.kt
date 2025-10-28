package com.medisupplyg4.repositories

import android.content.Context
import android.util.Log
import com.medisupplyg4.R
import com.medisupplyg4.models.ClientRegistrationRequest
import com.medisupplyg4.models.ClientRegistrationResponse
import com.medisupplyg4.network.NetworkClient

class ClientRegistrationRepository {
    companion object {
        private const val TAG = "ClientRegistrationRepository"
    }

    private val clientRegistrationApiService = NetworkClient.clientRegistrationApiService

    suspend fun registerClient(request: ClientRegistrationRequest, context: Context): Result<ClientRegistrationResponse> {
        return try {
            Log.d(TAG, "Iniciando registro de cliente: ${request.email}")
            
            val response = clientRegistrationApiService.registerClient(request)
            
            if (response.isSuccessful) {
                val registrationResponse = response.body()
                if (registrationResponse != null) {
                    Log.d(TAG, "Registro exitoso: ${registrationResponse.mensaje}")
                    Result.success(registrationResponse)
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
            Log.e(TAG, "Error durante el registro", e)
            Result.failure(e)
        }
    }
}
