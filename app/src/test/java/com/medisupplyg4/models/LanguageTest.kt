package com.medisupplyg4.models

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class LanguageTest {

    @Test
    fun `SPANISH should have correct code`() {
        assertEquals("es", Language.SPANISH.code)
    }

    @Test
    fun `ENGLISH should have correct code`() {
        assertEquals("en", Language.ENGLISH.code)
    }

    @Test
    fun `SPANISH should have correct locale`() {
        assertEquals("es_CO", Language.SPANISH.locale)
    }

    @Test
    fun `ENGLISH should have correct locale`() {
        assertEquals("en_US", Language.ENGLISH.locale)
    }

    @Test
    fun `SPANISH should have name resource ID`() {
        assertNotNull(Language.SPANISH.nameResId)
    }

    @Test
    fun `ENGLISH should have name resource ID`() {
        assertNotNull(Language.ENGLISH.nameResId)
    }

    @Test
    fun `SPANISH should have country resource ID`() {
        assertNotNull(Language.SPANISH.countryResId)
    }

    @Test
    fun `ENGLISH should have country resource ID`() {
        assertNotNull(Language.ENGLISH.countryResId)
    }

    @Test
    fun `SPANISH should have flag resource ID`() {
        assertNotNull(Language.SPANISH.flagResId)
    }

    @Test
    fun `ENGLISH should have flag resource ID`() {
        assertNotNull(Language.ENGLISH.flagResId)
    }

    @Test
    fun `AVAILABLE_LANGUAGES should contain both languages`() {
        assertEquals(2, Language.AVAILABLE_LANGUAGES.size)
        assert(Language.AVAILABLE_LANGUAGES.contains(Language.SPANISH))
        assert(Language.AVAILABLE_LANGUAGES.contains(Language.ENGLISH))
    }

    @Test
    fun `getByCode should return SPANISH for es code`() {
        val result = Language.getByCode("es")
        assertEquals(Language.SPANISH, result)
    }

    @Test
    fun `getByCode should return ENGLISH for en code`() {
        val result = Language.getByCode("en")
        assertEquals(Language.ENGLISH, result)
    }

    @Test
    fun `getByCode should return null for unknown code`() {
        val result = Language.getByCode("fr")
        assertNull(result)
    }

    @Test
    fun `getByCode should return null for empty code`() {
        val result = Language.getByCode("")
        assertNull(result)
    }
}