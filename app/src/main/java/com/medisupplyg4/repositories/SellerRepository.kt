package com.medisupplyg4.repositories

import android.content.Context
import android.util.Log
import com.medisupplyg4.config.ApiConfig
import com.medisupplyg4.models.PaginatedResponse
import com.medisupplyg4.models.SellerAPI
import com.medisupplyg4.models.VisitAPI
import com.medisupplyg4.models.VisitRecordRequest
import com.medisupplyg4.models.VisitRecordResponse
import com.medisupplyg4.network.NetworkClient
import com.medisupplyg4.utils.SessionManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Repository to handle seller and visit data
 */
class SellerRepository {
    
    companion object {
        private const val TAG = "SellerRepository"
    }
    
    private val vendedorApiService = NetworkClient.vendedorApiService
    private val visitasApiService = NetworkClient.visitasApiService
    
    /**
     * Gets the current seller based on the logged-in user information
     * Creates SellerAPI directly from SessionManager data without API call
     */
    suspend fun getCurrentSeller(token: String, context: Context): SellerAPI? {
        return try {
            Log.d(TAG, "Obteniendo vendedor actual desde información de sesión...")
            
            // Obtener información del usuario desde SessionManager
            val userId = SessionManager.getUserId(context)
            val userName = SessionManager.getUserName(context)
            val userEmail = SessionManager.getUserEmail(context)
            val userPhone = SessionManager.getUserPhone(context)
            val userAddress = SessionManager.getUserAddress(context)
            
            if (userId != null && userName != null && userEmail != null) {
                Log.d(TAG, "Información del usuario encontrada:")
                Log.d(TAG, "  ID: $userId")
                Log.d(TAG, "  Nombre: $userName")
                Log.d(TAG, "  Email: $userEmail")
                Log.d(TAG, "  Teléfono: $userPhone")
                Log.d(TAG, "  Dirección: $userAddress")
                
                // Crear SellerAPI directamente desde la información de sesión
                val seller = SellerAPI(
                    id = userId,
                    nombre = userName,
                    email = userEmail,
                    telefono = userPhone ?: "",
                    direccion = userAddress ?: ""
                )
                
                Log.d(TAG, "Vendedor creado desde información de sesión: ${seller.nombre}")
                seller
            } else {
                Log.w(TAG, "Información incompleta del usuario en SessionManager")
                Log.w(TAG, "userId: $userId, userName: $userName, userEmail: $userEmail")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener vendedor desde sesión", e)
            null
        }
    }
    
    /**
     * Gets the scheduled visits of a seller for a date range
     */
    suspend fun getSellerVisits(
        token: String,
        vendedorId: String,
        fechaInicio: LocalDate,
        fechaFin: LocalDate,
        page: Int = 1,
        pageSize: Int = 20
    ): List<VisitAPI> {
        return try {
            val fechaInicioStr = fechaInicio.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val fechaFinStr = fechaFin.format(DateTimeFormatter.ISO_LOCAL_DATE)
            
            Log.d(TAG, "Obteniendo visitas para vendedor $vendedorId desde $fechaInicioStr hasta $fechaFinStr (página $page, tamaño $pageSize)")
            
            val response = visitasApiService.getVisitasVendedor(
                token = "Bearer $token",
                vendedorId = vendedorId,
                fechaInicio = fechaInicioStr,
                fechaFin = fechaFinStr,
                estado = "pendiente",
                page = page,
                pageSize = pageSize
            )
            
            if (response.isSuccessful) {
                val paginatedResponse = response.body()
                val visitas = paginatedResponse?.items ?: emptyList()
                Log.d(TAG, "Visitas recibidas: ${visitas.size} de ${paginatedResponse?.pagination?.totalItems ?: 0}")
                visitas
            } else {
                Log.e(TAG, "Error al obtener visitas: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener visitas", e)
            emptyList()
        }
    }
    
    /**
     * Gets the scheduled visits of a seller for a date range with pagination
     */
    suspend fun getSellerVisitsPaginated(
        token: String,
        vendedorId: String,
        fechaInicio: LocalDate,
        fechaFin: LocalDate,
        page: Int = 1,
        pageSize: Int = 10
    ): Result<PaginatedResponse<VisitAPI>> {
        return try {
            val fechaInicioStr = fechaInicio.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val fechaFinStr = fechaFin.format(DateTimeFormatter.ISO_LOCAL_DATE)
            
            Log.d(TAG, "Obteniendo visitas paginadas para vendedor $vendedorId desde $fechaInicioStr hasta $fechaFinStr (página $page, tamaño $pageSize)")
            
            val response = visitasApiService.getVisitasVendedor(
                token = "Bearer $token",
                vendedorId = vendedorId,
                fechaInicio = fechaInicioStr,
                fechaFin = fechaFinStr,
                estado = "pendiente",
                page = page,
                pageSize = pageSize
            )
            
            if (response.isSuccessful) {
                val paginatedResponse = response.body()
                if (paginatedResponse != null) {
                    Log.d(TAG, "Visitas paginadas recibidas: ${paginatedResponse.items.size} de ${paginatedResponse.pagination.totalItems}")
                    Result.success(paginatedResponse)
                } else {
                    Log.e(TAG, "Respuesta vacía al obtener visitas paginadas")
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                Log.e(TAG, "Error al obtener visitas paginadas: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Error del servidor: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener visitas paginadas", e)
            Result.failure(e)
        }
    }

    /**
     * Records a completed visit
     */
    suspend fun recordVisit(
        token: String,
        visitaId: String,
        request: VisitRecordRequest
    ): VisitRecordResponse? {
        return try {
            Log.d(TAG, "Registrando visita $visitaId")
            
            val response = visitasApiService.recordVisit("Bearer $token", visitaId, request)
            
            if (response.isSuccessful) {
                val result = response.body()
                Log.d(TAG, "Visita registrada exitosamente: ${result?.message}")
                result
            } else {
                Log.e(TAG, "Error al registrar visita: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al registrar visita", e)
            null
        }
    }
}
