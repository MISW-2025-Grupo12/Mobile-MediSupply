package com.medisupplyg4.config

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApiConfigTest {

    @Test
    fun `BASE_URL should be defined`() {
        assertNotNull(ApiConfig.BASE_URL)
    }

    @Test
    fun `BASE_URL should be a valid URL`() {
        assert(ApiConfig.BASE_URL.startsWith("http"))
    }

    @Test
    fun `CONNECT_TIMEOUT should be defined`() {
        assertNotNull(ApiConfig.CONNECT_TIMEOUT)
    }

    @Test
    fun `CONNECT_TIMEOUT should be positive`() {
        assert(ApiConfig.CONNECT_TIMEOUT > 0)
    }

    @Test
    fun `READ_TIMEOUT should be defined`() {
        assertNotNull(ApiConfig.READ_TIMEOUT)
    }

    @Test
    fun `READ_TIMEOUT should be positive`() {
        assert(ApiConfig.READ_TIMEOUT > 0)
    }

    @Test
    fun `WRITE_TIMEOUT should be defined`() {
        assertNotNull(ApiConfig.WRITE_TIMEOUT)
    }

    @Test
    fun `WRITE_TIMEOUT should be positive`() {
        assert(ApiConfig.WRITE_TIMEOUT > 0)
    }

    @Test
    fun `timeouts should be reasonable values`() {
        // Connect timeout should be reasonable (not too high)
        assert(ApiConfig.CONNECT_TIMEOUT <= 60)
        
        // Read timeout should be reasonable (not too high)
        assert(ApiConfig.READ_TIMEOUT <= 60)
        
        // Write timeout should be reasonable (not too high)
        assert(ApiConfig.WRITE_TIMEOUT <= 60)
    }
}
