package com.medisupplyg4.network

import org.junit.Test
import kotlin.test.assertNotNull

class DeliveryApiServiceTest {

    @Test
    fun `DeliveryApiService should be accessible`() {
        // When
        val apiService = NetworkClient.deliveryApiService

        // Then
        assertNotNull(apiService)
    }

    @Test
    fun `DeliveryApiService should be of correct type`() {
        // When
        val apiService = NetworkClient.deliveryApiService

        // Then
        assert(apiService is DeliveryApiService)
    }
}
