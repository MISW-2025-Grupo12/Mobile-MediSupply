package com.medisupplyg4.network

import org.junit.Test
import kotlin.test.assertNotNull

class NetworkClientTest {

    @Test
    fun `deliveryApiService should return DeliveryApiService instance`() {
        // When
        val apiService = NetworkClient.deliveryApiService

        // Then
        assertNotNull(apiService)
    }

    @Test
    fun `deliveryApiService should return same instance on multiple calls`() {
        // When
        val apiService1 = NetworkClient.deliveryApiService
        val apiService2 = NetworkClient.deliveryApiService

        // Then
        assert(apiService1 === apiService2)
    }

    @Test
    fun `NetworkClient should be accessible`() {
        // When
        val networkClient = NetworkClient

        // Then
        assertNotNull(networkClient)
    }
}
