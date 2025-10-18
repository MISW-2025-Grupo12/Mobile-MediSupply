package com.medisupplyg4.repositories

import android.util.Log
import com.medisupplyg4.config.ApiConfig
import com.medisupplyg4.models.SellerAPI
import com.medisupplyg4.models.VisitAPI
import com.medisupplyg4.models.VisitRecordRequest
import com.medisupplyg4.models.VisitRecordResponse
import com.medisupplyg4.network.NetworkClient
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
     * Gets the list of sellers
     * TODO: Change this when authentication is implemented
     * For now always returns the first seller from the list
     */
    suspend fun getCurrentSeller(): SellerAPI? {
        return try {
            Log.d(TAG, "Obteniendo lista de vendedores...")
            Log.d(TAG, "URL base del servicio de vendedores: ${ApiConfig.USUARIOS_BASE_URL}")
            Log.d(TAG, "URL completa esperada: ${ApiConfig.USUARIOS_BASE_URL}usuarios/api/vendedores/")
            val response = vendedorApiService.getVendedores()
            
            if (response.isSuccessful) {
                val vendedores = response.body()
                Log.d(TAG, "Vendedores recibidos: ${vendedores?.size ?: 0}")
                
                // TODO: Cambiar esto cuando se implemente la autenticación
                // Por ahora siempre retornamos el primer vendedor
                vendedores?.firstOrNull()
            } else {
                Log.e(TAG, "Error al obtener vendedores: ${response.code()} - ${response.message()}")
                Log.e(TAG, "Response body: ${response.errorBody()?.string()}")
                Log.e(TAG, "Headers: ${response.headers()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener vendedores", e)
            null
        }
    }
    
    /**
     * Gets the scheduled visits of a seller for a date range
     */
    suspend fun getSellerVisits(
        vendedorId: String,
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ): List<VisitAPI> {
        return try {
            val fechaInicioStr = fechaInicio.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val fechaFinStr = fechaFin.format(DateTimeFormatter.ISO_LOCAL_DATE)
            
            Log.d(TAG, "Obteniendo visitas para vendedor $vendedorId desde $fechaInicioStr hasta $fechaFinStr")
            
            val response = visitasApiService.getVisitasVendedor(
                vendedorId = vendedorId,
                fechaInicio = fechaInicioStr,
                fechaFin = fechaFinStr,
                estado = "pendiente"
            )
            
            if (response.isSuccessful) {
                val visitas = response.body() ?: emptyList()
                Log.d(TAG, "Visitas recibidas: ${visitas.size}")
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
     * Records a completed visit
     */
    suspend fun recordVisit(
        visitaId: String,
        request: VisitRecordRequest
    ): VisitRecordResponse? {
        return try {
            Log.d(TAG, "Registrando visita $visitaId")
            
            val response = visitasApiService.recordVisit(visitaId, request)
            
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
