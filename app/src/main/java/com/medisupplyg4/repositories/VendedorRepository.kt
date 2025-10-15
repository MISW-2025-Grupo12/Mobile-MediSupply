package com.medisupplyg4.repositories

import android.util.Log
import com.medisupplyg4.models.VendedorAPI
import com.medisupplyg4.models.VisitaAPI
import com.medisupplyg4.network.NetworkClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Repositorio para manejar datos de vendedores y visitas
 */
class VendedorRepository {
    
    companion object {
        private const val TAG = "VendedorRepository"
    }
    
    private val vendedorApiService = NetworkClient.vendedorApiService
    private val visitasApiService = NetworkClient.visitasApiService
    
    /**
     * Obtiene la lista de vendedores
     * TODO: Cambiar esto cuando se implemente la autenticación
     * Por ahora siempre retorna el primer vendedor de la lista
     */
    suspend fun getVendedorActual(): VendedorAPI? {
        return try {
            Log.d(TAG, "Obteniendo lista de vendedores...")
            val response = vendedorApiService.getVendedores()
            
            if (response.isSuccessful) {
                val vendedores = response.body()
                Log.d(TAG, "Vendedores recibidos: ${vendedores?.size ?: 0}")
                
                // TODO: Cambiar esto cuando se implemente la autenticación
                // Por ahora siempre retornamos el primer vendedor
                vendedores?.firstOrNull()
            } else {
                Log.e(TAG, "Error al obtener vendedores: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener vendedores", e)
            null
        }
    }
    
    /**
     * Obtiene las visitas programadas de un vendedor para un rango de fechas
     */
    suspend fun getVisitasVendedor(
        vendedorId: String,
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ): List<VisitaAPI> {
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
     * Obtiene las visitas programadas de un vendedor para una fecha específica
     */
    suspend fun getVisitasVendedorPorFecha(
        vendedorId: String,
        fecha: LocalDate
    ): List<VisitaAPI> {
        return getVisitasVendedor(vendedorId, fecha, fecha)
    }
}
