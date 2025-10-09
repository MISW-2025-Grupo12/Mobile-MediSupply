package com.medisupplyg4.models

import java.time.LocalDate
import java.time.LocalDateTime

data class DeliveryRoute(
    val id: String,
    val driverId: String,
    val date: LocalDate,
    val deliveryPoints: List<DeliveryPoint>,
    val totalDistance: Double, // en kilómetros
    val estimatedTotalTime: Int, // en minutos
    val status: RouteStatus = RouteStatus.PLANNED,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class RouteStatus {
    PLANNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

enum class RoutePeriod {
    DAY,
    WEEK,
    MONTH
}
