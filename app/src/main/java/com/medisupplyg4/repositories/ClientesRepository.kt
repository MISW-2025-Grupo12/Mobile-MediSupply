package com.medisupplyg4.repositories

import android.content.Context
import android.util.Log
import com.medisupplyg4.R
import com.medisupplyg4.models.ClienteAPI
import com.medisupplyg4.models.PaginatedResponse
import com.medisupplyg4.network.NetworkClient

class ClientesRepository {
    companion object {
        private const val TAG = "ClientesRepository"
    }

    private val clientesApiService = NetworkClient.clientesApiService

    suspend fun getClientes(token: String, context: Context, page: Int = 1, pageSize: Int = 20): Result<List<ClienteAPI>> {
        return try {
            Log.d(TAG, "Obteniendo lista de clientes (página $page, tamaño $pageSize)")
            
            val response = clientesApiService.getClientes("Bearer $token", page, pageSize)
            
            if (response.isSuccessful) {
                val paginatedResponse = response.body()
                if (paginatedResponse != null) {
                    val clientes = paginatedResponse.items
                    Log.d(TAG, "Clientes obtenidos exitosamente: ${clientes.size} de ${paginatedResponse.pagination.totalItems} clientes")
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

    suspend fun getClientesPaginated(token: String, context: Context, page: Int = 1, pageSize: Int = 10): Result<PaginatedResponse<ClienteAPI>> {
        return try {
            Log.d(TAG, "Obteniendo clientes paginados (página $page, tamaño $pageSize)")
            
            val response = clientesApiService.getClientes("Bearer $token", page, pageSize)
            
            if (response.isSuccessful) {
                val paginatedResponse = response.body()
                if (paginatedResponse != null) {
                    Log.d(TAG, "Clientes paginados obtenidos exitosamente: ${paginatedResponse.items.size} de ${paginatedResponse.pagination.totalItems} clientes")
                    Result.success(paginatedResponse)
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
            Log.e(TAG, "Error al obtener clientes paginados", e)
            Result.failure(e)
        }
    }
}
