package com.medisupplyg4.models

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class DeliveryPointTest {

    @Test
    fun `DeliveryPoint should create instance with correct properties`() {
        // Given
        val id = "point-123"
        val customer = Customer(
            id = "customer-456",
            name = "Test Customer",
            address = "Test Address 123",
            phone = "123-456-7890"
        )
        val orderId = "order-789"
        val products = listOf(
            ProductDelivery(
                productId = "product-1",
                productName = "Test Product",
                quantity = 5,
                unit = "units"
            )
        )
        val estimatedDeliveryTime = LocalDateTime.of(2025, 10, 15, 14, 30)
        val priority = DeliveryPriority.NORMAL
        val requiresColdChain = false
        val specialInstructions = "Handle with care"
        val order = 1

        // When
        val deliveryPoint = DeliveryPoint(
            id = id,
            customer = customer,
            orderId = orderId,
            products = products,
            estimatedDeliveryTime = estimatedDeliveryTime,
            priority = priority,
            requiresColdChain = requiresColdChain,
            specialInstructions = specialInstructions,
            order = order
        )

        // Then
        assertEquals(id, deliveryPoint.id)
        assertEquals(customer, deliveryPoint.customer)
        assertEquals(orderId, deliveryPoint.orderId)
        assertEquals(products, deliveryPoint.products)
        assertEquals(estimatedDeliveryTime, deliveryPoint.estimatedDeliveryTime)
        assertEquals(priority, deliveryPoint.priority)
        assertEquals(requiresColdChain, deliveryPoint.requiresColdChain)
        assertEquals(specialInstructions, deliveryPoint.specialInstructions)
        assertEquals(order, deliveryPoint.order)
    }
}
