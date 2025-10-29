package com.medisupplyg4.repositories

import android.app.Application
import android.util.Log
import com.medisupplyg4.models.ClienteAPI
import com.medisupplyg4.models.RoutePeriod
import com.medisupplyg4.models.SimpleDelivery
import com.medisupplyg4.network.NetworkClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DeliveryRouteRepository() {
    
    companion object {
        private const val TAG = "DeliveryRouteRepository"
    }
    
    /**
     * Obtiene entregas según el período seleccionado
     */
    suspend fun getDeliveries(token: String, selectedDate: LocalDate, selectedPeriod: RoutePeriod, page: Int = 1, pageSize: Int = 20): List<SimpleDelivery> {
        return withContext(Dispatchers.IO) {
            try {
                val (fechaInicio, fechaFin) = calculateDateRange(selectedDate, selectedPeriod)
                
                Log.d(TAG, "Obteniendo entregas desde $fechaInicio hasta $fechaFin (página $page, tamaño $pageSize)")
                
                val response = NetworkClient.deliveryApiService.getDeliveries(
                    token = "Bearer $token",
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin,
                    page = page,
                    pageSize = pageSize
                )
                
                if (response.isSuccessful) {
                    val paginatedResponse = response.body()
                    val deliveryResponses = paginatedResponse?.items ?: emptyList()
                    Log.d(TAG, "Datos recibidos: ${deliveryResponses.size} entregas de ${paginatedResponse?.pagination?.totalItems ?: 0}")
                    
                    // Convertir a SimpleDelivery estándar
                    val deliveries = deliveryResponses.map { it.toSimpleDelivery() }
                    
                    // Si no hay información de cliente, intentar obtenerla
                    val deliveriesWithClientInfo = if (deliveries.any { it.cliente == null }) {
                        enrichDeliveriesWithClientInfo(token, deliveries)
                    } else {
                        deliveries
                    }
                    
                    deliveriesWithClientInfo
                } else {
                    Log.w(TAG, "Error del backend: ${response.code()}")
                    Log.w(TAG, "Response body: ${response.errorBody()?.string()}")
                    emptyList()
                }
            } catch (e: Exception) {
                // Solo logear errores reales, no cancelaciones
                if (e !is kotlinx.coroutines.CancellationException) {
                    Log.e(TAG, "Error de red: ${e.message}", e)
                }
                emptyList()
            }
        }
    }
    
    /**
     * Enriquece las entregas con información de cliente obtenida de la API de usuarios
     */
    private suspend fun enrichDeliveriesWithClientInfo(token: String, deliveries: List<SimpleDelivery>): List<SimpleDelivery> {
        return try {
            // Obtener todos los clientes de la API de usuarios
            val clientesResponse = NetworkClient.clientesApiService.getClientes("Bearer $token", 1, 20)
            
            if (clientesResponse.isSuccessful) {
                val paginatedResponse = clientesResponse.body()
                val clientes = paginatedResponse?.items ?: emptyList()
                
                // Crear un mapa de clientes por ID para búsqueda rápida
                val clientesMap = clientes.associateBy { it.id }
                
                // Enriquecer las entregas con información de cliente
                deliveries.map { delivery ->
                    if (delivery.cliente == null) {
                        // Intentar encontrar el cliente por ID en la dirección o usar un cliente genérico
                        val clienteEncontrado = findClienteByAddress(delivery.direccion, clientesMap)
                        
                        delivery.copy(cliente = clienteEncontrado)
                    } else {
                        delivery
                    }
                }
            } else {
                Log.w(TAG, "Error al obtener clientes: ${clientesResponse.code()}")
                deliveries
            }
        } catch (e: Exception) {
            // Solo logear errores reales, no cancelaciones
            if (e !is CancellationException) {
                Log.e(TAG, "Error al enriquecer entregas con información de cliente", e)
            }
            deliveries
        }
    }
    
    /**
     * Busca un cliente por dirección (método de fallback)
     */
    private fun findClienteByAddress(direccion: String, clientesMap: Map<String, ClienteAPI>): ClienteAPI? {
        // Buscar cliente que tenga una dirección similar
        val clienteEncontrado = clientesMap.values.find { cliente: ClienteAPI ->
            cliente.direccion.contains(direccion.substringBefore(" #"), ignoreCase = true) ||
            direccion.contains(cliente.direccion.substringBefore(" #"), ignoreCase = true)
        }
        
        // Cliente encontrado o no encontrado
        
        return clienteEncontrado
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