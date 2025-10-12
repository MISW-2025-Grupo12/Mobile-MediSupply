package com.medisupplyg4.repositories

import android.app.Application
import android.util.Log
import com.medisupplyg4.models.RoutePeriod
import com.medisupplyg4.models.SimpleDelivery
import com.medisupplyg4.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DeliveryRouteRepository(private val application: Application) {
    
    companion object {
        private const val TAG = "DeliveryRouteRepository"
    }
    
    /**
     * Obtiene entregas según el período seleccionado
     */
    suspend fun getDeliveries(selectedDate: LocalDate, selectedPeriod: RoutePeriod): List<SimpleDelivery> {
        return withContext(Dispatchers.IO) {
            try {
                val (fechaInicio, fechaFin) = calculateDateRange(selectedDate, selectedPeriod)
                
                Log.d(TAG, "Obteniendo entregas desde $fechaInicio hasta $fechaFin")
                
                val response = NetworkClient.deliveryApiService.getDeliveries(
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin
                )
                
                if (response.isSuccessful) {
                    val deliveries = response.body() ?: emptyList()
                    Log.d(TAG, "Datos recibidos: ${deliveries.size} entregas")
                    deliveries
                } else {
                    Log.w(TAG, "Error del backend: ${response.code()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error de red: ${e.message}", e)
                emptyList()
            }
        }
    }
    
    /**
     * Calcula el rango de fechas según el período seleccionado
     */
    private fun calculateDateRange(selectedDate: LocalDate, selectedPeriod: RoutePeriod): Pair<String, String> {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        
        return when (selectedPeriod) {
            RoutePeriod.DAY -> {
                // Para el día: fecha actual para ambos parámetros
                val fecha = selectedDate.format(formatter)
                Pair(fecha, fecha)
            }
            RoutePeriod.WEEK -> {
                // Para la semana: desde hoy hasta dentro de 7 días
                val fechaInicio = selectedDate.format(formatter)
                val fechaFin = selectedDate.plusDays(7).format(formatter)
                Pair(fechaInicio, fechaFin)
            }
            RoutePeriod.MONTH -> {
                // Para el mes: desde hoy hasta dentro de 30 días
                val fechaInicio = selectedDate.format(formatter)
                val fechaFin = selectedDate.plusDays(30).format(formatter)
                Pair(fechaInicio, fechaFin)
            }
        }
    }
}