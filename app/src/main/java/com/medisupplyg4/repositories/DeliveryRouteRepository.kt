package com.medisupplyg4.repositories

import android.app.Application
import com.medisupplyg4.models.*
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime

class DeliveryRouteRepository(application: Application) {
    
    // TODO: En el futuro esto se conectará con la API real
    // Por ahora usamos datos mock para desarrollo
    
    suspend fun getDeliveryRoutesForDay(date: LocalDate, driverId: String): List<DeliveryRoute> {
        // Simular delay de red
        delay(1000)
        
        return createMockRoutesForDay(date, driverId)
    }
    
    suspend fun getDeliveryRoutesForWeek(startDate: LocalDate, driverId: String): List<DeliveryRoute> {
        delay(1000)
        
        val routes = mutableListOf<DeliveryRoute>()
        for (i in 0..6) {
            val date = startDate.plusDays(i.toLong())
            routes.addAll(createMockRoutesForDay(date, driverId))
        }
        return routes
    }
    
    suspend fun getDeliveryRoutesForMonth(month: Int, year: Int, driverId: String): List<DeliveryRoute> {
        delay(1000)
        
        val routes = mutableListOf<DeliveryRoute>()
        val startDate = LocalDate.of(year, month, 1)
        val endDate = startDate.withDayOfMonth(startDate.lengthOfMonth())
        
        var currentDate = startDate
        while (!currentDate.isAfter(endDate)) {
            routes.addAll(createMockRoutesForDay(currentDate, driverId))
            currentDate = currentDate.plusDays(1)
        }
        return routes
    }
    
    private fun createMockRoutesForDay(date: LocalDate, driverId: String): List<DeliveryRoute> {
        // Crear datos mock para desarrollo
        val customers = listOf(
            Customer(
                id = "1",
                name = "Hospital San Rafael",
                address = "Calle 123 #45-67, Bogotá",
                phone = "+57 1 234-5678",
                email = "contacto@hospitalsanrafael.com",
                type = CustomerType.HOSPITAL
            ),
            Customer(
                id = "2", 
                name = "Clínica Santa María",
                address = "Avenida 7 #32-10, Bogotá",
                phone = "+57 1 345-6789",
                email = "info@clinicasantamaria.com",
                type = CustomerType.CLINIC
            ),
            Customer(
                id = "3",
                name = "Centro Médico Los Andes",
                address = "Carrera 15 #93-47, Bogotá", 
                phone = "+57 1 456-7890",
                email = "atencion@centromedicoandes.com",
                type = CustomerType.MEDICAL_CENTER
            )
        )
        
        val deliveryPoints = listOf(
            DeliveryPoint(
                id = "1",
                customer = customers[0],
                orderId = "34562",
                products = listOf(
                    ProductDelivery("P1", "Jeringas 10ml", 50, "unidades"),
                    ProductDelivery("P2", "Guantes Nitrilo", 100, "unidades")
                ),
                estimatedDeliveryTime = LocalDateTime.of(date.year, date.monthValue, date.dayOfMonth, 9, 0),
                priority = DeliveryPriority.HIGH,
                requiresColdChain = false,
                order = 1
            ),
            DeliveryPoint(
                id = "2",
                customer = customers[1],
                orderId = "34563", 
                products = listOf(
                    ProductDelivery("P3", "Vacunas COVID-19", 20, "dosis", true),
                    ProductDelivery("P4", "Tubos de Sangre", 30, "unidades")
                ),
                estimatedDeliveryTime = LocalDateTime.of(date.year, date.monthValue, date.dayOfMonth, 11, 30),
                priority = DeliveryPriority.URGENT,
                requiresColdChain = true,
                specialInstructions = "Mantener cadena de frío",
                order = 2
            ),
            DeliveryPoint(
                id = "3",
                customer = customers[2],
                orderId = "34564",
                products = listOf(
                    ProductDelivery("P5", "Mascarillas N95", 200, "unidades"),
                    ProductDelivery("P6", "Alcohol Gel", 50, "litros")
                ),
                estimatedDeliveryTime = LocalDateTime.of(date.year, date.monthValue, date.dayOfMonth, 14, 0),
                priority = DeliveryPriority.NORMAL,
                requiresColdChain = false,
                order = 3
            )
        )
        
        return listOf(
            DeliveryRoute(
                id = "route-${date}",
                driverId = driverId,
                date = date,
                deliveryPoints = deliveryPoints,
                totalDistance = 45.5,
                estimatedTotalTime = 180,
                status = RouteStatus.PLANNED
            )
        )
    }
}
