package com.medisupplyg4.models

import org.junit.Assert.*
import org.junit.Test

class UserRoleTest {

    @Test
    fun `UserRole enum should have all expected values`() {
        // When
        val roles = UserRole.values()

        // Then
        assertEquals(3, roles.size)
        assertTrue(roles.contains(UserRole.DRIVER))
        assertTrue(roles.contains(UserRole.CLIENT))
        assertTrue(roles.contains(UserRole.SELLER))
    }

    @Test
    fun `UserRole should have correct title resource IDs`() {
        // When & Then
        assertNotNull(UserRole.DRIVER.titleResId)
        assertNotNull(UserRole.CLIENT.titleResId)
        assertNotNull(UserRole.SELLER.titleResId)
    }

    @Test
    fun `UserRole values should be unique`() {
        // When
        val roles = UserRole.values()
        val uniqueRoles = roles.distinct()

        // Then
        assertEquals(roles.size, uniqueRoles.size)
    }
}

