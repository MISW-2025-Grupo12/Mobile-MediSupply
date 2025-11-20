package com.medisupplyg4.repositories

import android.content.Context
import android.util.Log
import com.medisupplyg4.R
import com.medisupplyg4.models.RouteDetail
import com.medisupplyg4.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RouteDetailRepository {

    companion object {
        private const val TAG = "RouteDetailRepository"
    }

    /**
     * Obtiene la ruta por fecha (asume que hay una ruta por fecha para el repartidor)
     */
    suspend fun getRouteByDate(token: String, fecha: String, context: Context): Result<RouteDetail?> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Obteniendo ruta por fecha: $fecha")
                val response = NetworkClient.deliveryApiService.getRoutesByDate(
                    token = "Bearer $token",
                    fechaRuta = fecha,
                    page = 1,
                    pageSize = 1
                )
                
                if (response.isSuccessful) {
                    val routes = response.body() ?: emptyList()
                    if (routes.isNotEmpty()) {
                        Log.d(TAG, "Ruta encontrada: ${routes.first().id}")
                        Result.success(routes.first())
                    } else {
                        Log.d(TAG, "No se encontró ruta para la fecha: $fecha")
                        Result.success(null)
                    }
                } else {
                    Log.w(TAG, "Error del backend: ${response.code()}")
                    val errorMessage = context.getString(R.string.error_http_code, response.code())
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al obtener ruta por fecha", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Obtiene el detalle de una ruta por ID
     */
    suspend fun getRouteDetail(token: String, rutaId: String, context: Context): Result<RouteDetail> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Obteniendo detalle de ruta: $rutaId")
                val response = NetworkClient.deliveryApiService.getRouteDetail(
                    token = "Bearer $token",
                    rutaId = rutaId
                )
                
                if (response.isSuccessful) {
                    val routeDetail = response.body()
                    if (routeDetail != null) {
                        Log.d(TAG, "Ruta obtenida exitosamente: ${routeDetail.id} con ${routeDetail.entregas.size} entregas")
                        Result.success(routeDetail)
                    } else {
                        Log.w(TAG, "Respuesta vacía del servidor")
                        val errorMessage = context.getString(R.string.error_server_empty_response)
                        Result.failure(Exception(errorMessage))
                    }
                } else {
                    Log.w(TAG, "Error del backend: ${response.code()}")
                    val errorMessage = context.getString(R.string.error_http_code, response.code())
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al obtener detalle de ruta", e)
                Result.failure(e)
            }
        }
    }
}

