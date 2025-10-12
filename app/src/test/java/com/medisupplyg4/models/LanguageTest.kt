package com.medisupplyg4.models

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class LanguageTest {

    @Test
    fun `Language enum should have correct values`() {
        // When & Then
        assertEquals("es", Language.SPANISH.code)
        assertEquals("en", Language.ENGLISH.code)
    }

    @Test
    fun `Language enum should have correct locales`() {
        // When & Then
        assertEquals("es_CO", Language.SPANISH.locale.toString())
        assertEquals("en_US", Language.ENGLISH.locale.toString())
    }

    @Test
    fun `getByCode should return correct language for Spanish`() {
        // When
        val result = Language.getByCode("es")

        // Then
        assertEquals(Language.SPANISH, result)
    }

    @Test
    fun `getByCode should return correct language for English`() {
        // When
        val result = Language.getByCode("en")

        // Then
        assertEquals(Language.ENGLISH, result)
    }

    @Test
    fun `getByCode should return null for invalid code`() {
        // When
        val result = Language.getByCode("invalid")

        // Then
        assertNull(result)
    }

    @Test
    fun `getByCode should return null for empty code`() {
        // When
        val result = Language.getByCode("")

        // Then
        assertNull(result)
    }
}
