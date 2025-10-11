package com.medisupplyg4.repositories

import android.app.Application
import android.content.Context
import android.util.Log
import com.medisupplyg4.R
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
    
    suspend fun getDeliveriesForDay(date: LocalDate, driverId: String): List<SimpleDelivery> {
        return withContext(Dispatchers.IO) {
            try {
                val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val response = NetworkClient.deliveryApiService.getDeliveries(
                    driverId = driverId,
                    date = dateString,
                    period = application.getString(R.string.api_period_day)
                )
                
                if (response.isSuccessful) {
                    val deliveries = response.body() ?: emptyList()
                    Log.d(TAG, application.getString(R.string.log_data_received, deliveries.size))
                    deliveries
                } else {
                    Log.w(TAG, application.getString(R.string.error_backend, response.code()))
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, application.getString(R.string.error_network, e.message), e)
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
                    period = application.getString(R.string.api_period_week)
                )
                
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    Log.w(TAG, application.getString(R.string.error_backend, response.code()))
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, application.getString(R.string.error_network, e.message), e)
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
                    period = application.getString(R.string.api_period_month)
                )
                
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    Log.w(TAG, application.getString(R.string.error_backend, response.code()))
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, application.getString(R.string.error_network, e.message), e)
                emptyList()
            }
        }
    }

}
