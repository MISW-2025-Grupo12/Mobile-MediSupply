package com.medisupplyg4.models

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class DeliveryRouteTest {

    @Test
    fun `DeliveryRoute should create instance with correct properties`() {
        // Given
        val id = "route-123"
        val driverId = "driver-456"
        val date = LocalDate.of(2025, 10, 15)
        val status = RouteStatus.PLANNED
        val deliveryPoints = listOf(
            DeliveryPoint(
                id = "point-1",
                customer = Customer(
                    id = "customer-1",
                    name = "Test Customer",
                    address = "Address 1",
                    phone = "123-456-7890"
                ),
                orderId = "order-1",
                products = listOf(
                    ProductDelivery(
                        productId = "product-1",
                        productName = "Test Product",
                        quantity = 1,
                        unit = "unit"
                    )
                ),
                estimatedDeliveryTime = java.time.LocalDateTime.now(),
                order = 1
            )
        )
        val totalDistance = 10.5
        val estimatedTotalTime = 120

        // When
        val deliveryRoute = DeliveryRoute(
            id = id,
            driverId = driverId,
            date = date,
            deliveryPoints = deliveryPoints,
            totalDistance = totalDistance,
            estimatedTotalTime = estimatedTotalTime,
            status = status
        )

        // Then
        assertEquals(id, deliveryRoute.id)
        assertEquals(driverId, deliveryRoute.driverId)
        assertEquals(date, deliveryRoute.date)
        assertEquals(status, deliveryRoute.status)
        assertEquals(1, deliveryRoute.deliveryPoints.size)
        assertEquals("point-1", deliveryRoute.deliveryPoints[0].id)
        assertEquals(totalDistance, deliveryRoute.totalDistance, 0.01)
        assertEquals(estimatedTotalTime, deliveryRoute.estimatedTotalTime)
    }
}
