package com.medisupplyg4.repositories

import android.app.Application
import android.util.Log
import com.medisupplyg4.models.SimpleDelivery
import com.medisupplyg4.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DeliveryRouteRepository(application: Application) {
    
    companion object {
        private const val TAG = "DeliveryRouteRepository"
    }
    
    suspend fun getDeliveriesForDay(date: LocalDate, driverId: String): List<SimpleDelivery> {
        return withContext(Dispatchers.IO) {
            try {
                val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val response = NetworkClient.deliveryApiService.getDeliveries(
                    driverId = driverId,
                    date = dateString,
                    period = "day"
                )
                
                if (response.isSuccessful) {
                    val deliveries = response.body() ?: emptyList()
                    Log.d(TAG, "Datos recibidos del backend: ${deliveries.size} entregas")
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
    
    suspend fun getDeliveriesForWeek(startDate: LocalDate, driverId: String): List<SimpleDelivery> {
        return withContext(Dispatchers.IO) {
            try {
                val dateString = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val response = NetworkClient.deliveryApiService.getDeliveries(
                    driverId = driverId,
                    date = dateString,
                    period = "week"
                )
                
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
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
    
    suspend fun getDeliveriesForMonth(month: Int, year: Int, driverId: String): List<SimpleDelivery> {
        return withContext(Dispatchers.IO) {
            try {
                val startDate = LocalDate.of(year, month, 1)
                val dateString = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val response = NetworkClient.deliveryApiService.getDeliveries(
                    driverId = driverId,
                    date = dateString,
                    period = "month"
                )
                
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
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

}
