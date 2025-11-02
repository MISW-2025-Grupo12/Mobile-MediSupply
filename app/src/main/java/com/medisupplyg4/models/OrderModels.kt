package com.medisupplyg4.models

import java.time.LocalDate

data class OrderItemUI(
    val id: String,
    val name: String,
    val quantity: Int,
    val unitPrice: Double
) {
    val subtotal: Double get() = quantity * unitPrice
}

data class OrderUI(
    val id: String,
    val number: String,
    val createdAt: LocalDate,
    val status: OrderStatus,
    val items: List<OrderItemUI>,
    val estimatedDelivery: LocalDate? = null,
    val deliveredAt: LocalDate? = null
) {
    val productsSummary: String
        get() = items.joinToString(limit = 3, truncated = "…") { it.name }
    val total: Double get() = items.sumOf { it.subtotal }
}
