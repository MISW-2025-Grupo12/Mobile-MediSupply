package com.medisupplyg4.repositories

import android.content.Context
import android.util.Log
import com.medisupplyg4.R
import com.medisupplyg4.models.ClienteAPI
import com.medisupplyg4.network.NetworkClient

class ClientesRepository {
    companion object {
        private const val TAG = "ClientesRepository"
    }

    private val clientesApiService = NetworkClient.clientesApiService

    suspend fun getClientes(token: String, context: Context): Result<List<ClienteAPI>> {
        return try {
            Log.d(TAG, "Obteniendo lista de clientes")
            
            val response = clientesApiService.getClientes("Bearer $token")
            
            if (response.isSuccessful) {
                val clientes = response.body()
                if (clientes != null) {
                    Log.d(TAG, "Clientes obtenidos exitosamente: ${clientes.size} clientes")
                    Result.success(clientes)
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
            Log.e(TAG, "Error al obtener clientes", e)
            Result.failure(e)
        }
    }
}
