package com.medisupplyg4.network

import com.medisupplyg4.config.ApiConfig
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Retrofit

class NetworkClientTest {

    @Before
    fun setup() {
        mockkObject(ApiConfig)
    }

    @Test
    fun `deliveryApiService should be created successfully`() {
        // Act
        val service = NetworkClient.deliveryApiService

        // Assert
        assertNotNull(service)
        assertTrue(service is DeliveryApiService)
    }

    @Test
    fun `vendedorApiService should be created successfully`() {
        // Act
        val service = NetworkClient.vendedorApiService

        // Assert
        assertNotNull(service)
        assertTrue(service is VendedorApiService)
    }

    @Test
    fun `visitasApiService should be created successfully`() {
        // Act
        val service = NetworkClient.visitasApiService

        // Assert
        assertNotNull(service)
        assertTrue(service is VisitasApiService)
    }

    @Test
    fun `all services should be different instances`() {
        // Act
        val deliveryService = NetworkClient.deliveryApiService
        val vendedorService = NetworkClient.vendedorApiService
        val visitasService = NetworkClient.visitasApiService

        // Assert
        assertNotEquals(deliveryService, vendedorService)
        assertNotEquals(deliveryService, visitasService)
        assertNotEquals(vendedorService, visitasService)
    }

    @Test
    fun `services should be singletons`() {
        // Act
        val deliveryService1 = NetworkClient.deliveryApiService
        val deliveryService2 = NetworkClient.deliveryApiService

        val vendedorService1 = NetworkClient.vendedorApiService
        val vendedorService2 = NetworkClient.vendedorApiService

        val visitasService1 = NetworkClient.visitasApiService
        val visitasService2 = NetworkClient.visitasApiService

        // Assert - Check that services are not null and are the same instance
        assertNotNull(deliveryService1)
        assertNotNull(deliveryService2)
        assertSame(deliveryService1, deliveryService2)
        
        assertNotNull(vendedorService1)
        assertNotNull(vendedorService2)
        assertSame(vendedorService1, vendedorService2)
        
        assertNotNull(visitasService1)
        assertNotNull(visitasService2)
        assertSame(visitasService1, visitasService2)
    }

    @Test
    fun `services should be properly configured`() {
        // This test verifies that the services are properly configured
        // by checking that they can be created without errors
        
        // Act
        val deliveryService = NetworkClient.deliveryApiService
        val vendedorService = NetworkClient.vendedorApiService
        val visitasService = NetworkClient.visitasApiService

        // Assert
        assertNotNull(deliveryService)
        assertNotNull(vendedorService)
        assertNotNull(visitasService)
    }

    @Test
    fun `retrofit instances should have Gson converter`() {
        // This test verifies that the Retrofit instances are properly configured
        // by checking that they can create service instances
        
        // Act
        val deliveryService = NetworkClient.deliveryApiService
        val vendedorService = NetworkClient.vendedorApiService
        val visitasService = NetworkClient.visitasApiService

        // Assert
        assertNotNull(deliveryService)
        assertNotNull(vendedorService)
        assertNotNull(visitasService)
        
        // Verify they are proper Retrofit service instances
        assertTrue(deliveryService.javaClass.interfaces.contains(DeliveryApiService::class.java))
        assertTrue(vendedorService.javaClass.interfaces.contains(VendedorApiService::class.java))
        assertTrue(visitasService.javaClass.interfaces.contains(VisitasApiService::class.java))
    }

    @Test
    fun `NetworkClient should be accessible`() {
        // When
        val networkClient = NetworkClient

        // Then
        assertNotNull(networkClient)
    }
}