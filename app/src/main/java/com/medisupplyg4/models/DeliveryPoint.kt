package com.medisupplyg4.models

import java.time.LocalDateTime

data class DeliveryPoint(
    val id: String,
    val customer: Customer,
    val orderId: String,
    val products: List<ProductDelivery>,
    val estimatedDeliveryTime: LocalDateTime,
    val priority: DeliveryPriority = DeliveryPriority.NORMAL,
    val requiresColdChain: Boolean = false,
    val specialInstructions: String? = null,
    val order: Int // Orden en la ruta
)

data class ProductDelivery(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unit: String,
    val requiresColdChain: Boolean = false
)

enum class DeliveryPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT
}
