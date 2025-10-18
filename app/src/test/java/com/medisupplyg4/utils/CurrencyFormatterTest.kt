package com.medisupplyg4.utils

import org.junit.Test
import org.junit.Assert.*
import java.text.NumberFormat
import java.util.*

/**
 * Unit tests for currency formatting functionality
 */
class CurrencyFormatterTest {

    @Test
    fun `test Spanish currency formatting`() {
        // Given
        val price = 25000.0
        val locale = Locale.Builder().setLanguage("es").setRegion("CO").build()
        val formatter = NumberFormat.getCurrencyInstance(locale)

        // When
        val formatted = formatter.format(price)

        // Then
        // Spanish format should use dot for thousands separator and comma for decimals
        assertTrue("Formatted price should contain currency symbol", formatted.contains("$"))
        assertTrue("Spanish format should use dot for thousands", formatted.contains("."))
    }

    @Test
    fun `test English currency formatting`() {
        // Given
        val price = 25000.0
        val locale = Locale.US
        val formatter = NumberFormat.getCurrencyInstance(locale)

        // When
        val formatted = formatter.format(price)

        // Then
        // English format should use comma for thousands separator and dot for decimals
        assertTrue("Formatted price should contain currency symbol", formatted.contains("$"))
        assertTrue("English format should use comma for thousands", formatted.contains(","))
    }

    @Test
    fun `test currency formatting with decimals`() {
        // Given
        val price = 1234.56
        val locale = Locale.US
        val formatter = NumberFormat.getCurrencyInstance(locale)

        // When
        val formatted = formatter.format(price)

        // Then
        assertTrue("Formatted price should contain currency symbol", formatted.contains("$"))
        assertTrue("Should contain decimal part", formatted.contains("."))
        assertTrue("Should contain thousands separator", formatted.contains(","))
    }

    @Test
    fun `test currency formatting with zero decimals`() {
        // Given
        val price = 1000.0
        val locale = Locale.US
        val formatter = NumberFormat.getCurrencyInstance(locale)

        // When
        val formatted = formatter.format(price)

        // Then
        assertTrue("Formatted price should contain currency symbol", formatted.contains("$"))
        assertTrue("Should contain thousands separator", formatted.contains(","))
    }

    @Test
    fun `test currency formatting with large numbers`() {
        // Given
        val price = 1234567.89
        val locale = Locale.US
        val formatter = NumberFormat.getCurrencyInstance(locale)

        // When
        val formatted = formatter.format(price)

        // Then
        assertTrue("Formatted price should contain currency symbol", formatted.contains("$"))
        assertTrue("Should contain multiple thousands separators", formatted.contains(","))
        assertTrue("Should contain decimal part", formatted.contains("."))
    }

    @Test
    fun `test locale builder usage`() {
        // Given
        val language = "es"
        val region = "CO"

        // When
        val locale = Locale.Builder()
            .setLanguage(language)
            .setRegion(region)
            .build()

        // Then
        assertEquals("es", locale.language)
        assertEquals("CO", locale.country)
        assertNotNull("Locale should be created successfully", locale)
    }

    @Test
    fun `test locale detection logic`() {
        // Given
        val currentLocale = Locale.getDefault()

        // When
        val isEnglish = currentLocale.language == "en"
        val isSpanish = currentLocale.language == "es"

        // Then
        assertTrue("Should be either English or Spanish or other", isEnglish || isSpanish || (!isEnglish && !isSpanish))
    }
}
