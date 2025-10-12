package com.medisupplyg4.models

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserRoleTest {

    @Test
    fun `DRIVER enum should have correct name`() {
        assertEquals("DRIVER", UserRole.DRIVER.name)
    }

    @Test
    fun `CLIENT enum should have correct name`() {
        assertEquals("CLIENT", UserRole.CLIENT.name)
    }

    @Test
    fun `SELLER enum should have correct name`() {
        assertEquals("SELLER", UserRole.SELLER.name)
    }

    @Test
    fun `DRIVER enum should have correct ordinal`() {
        assertEquals(0, UserRole.DRIVER.ordinal)
    }

    @Test
    fun `CLIENT enum should have correct ordinal`() {
        assertEquals(1, UserRole.CLIENT.ordinal)
    }

    @Test
    fun `SELLER enum should have correct ordinal`() {
        assertEquals(2, UserRole.SELLER.ordinal)
    }

    @Test
    fun `values should return all enum values`() {
        val values = UserRole.values()
        assertEquals(3, values.size)
        assertEquals(UserRole.DRIVER, values[0])
        assertEquals(UserRole.CLIENT, values[1])
        assertEquals(UserRole.SELLER, values[2])
    }

    @Test
    fun `valueOf should return correct enum for DRIVER`() {
        val result = UserRole.valueOf("DRIVER")
        assertEquals(UserRole.DRIVER, result)
    }

    @Test
    fun `valueOf should return correct enum for CLIENT`() {
        val result = UserRole.valueOf("CLIENT")
        assertEquals(UserRole.CLIENT, result)
    }

    @Test
    fun `valueOf should return correct enum for SELLER`() {
        val result = UserRole.valueOf("SELLER")
        assertEquals(UserRole.SELLER, result)
    }

    @Test
    fun `DRIVER should have title resource ID`() {
        assertNotNull(UserRole.DRIVER.titleResId)
    }

    @Test
    fun `CLIENT should have title resource ID`() {
        assertNotNull(UserRole.CLIENT.titleResId)
    }

    @Test
    fun `SELLER should have title resource ID`() {
        assertNotNull(UserRole.SELLER.titleResId)
    }
}