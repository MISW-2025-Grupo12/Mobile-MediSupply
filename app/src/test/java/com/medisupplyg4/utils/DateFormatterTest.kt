package com.medisupplyg4.utils

import android.content.Context
import android.content.res.Configuration
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DateFormatterTest {

    private lateinit var mockContext: Context

    @Before
    fun setup() {
        mockContext = mockk()
        // Mock configuration to return specific locales
        val mockConfiguration = mockk<Configuration>()
        every { mockContext.resources } returns RuntimeEnvironment.getApplication().resources
        every { mockContext.resources.configuration } returns mockConfiguration
        every { mockConfiguration.locales } returns android.os.LocaleList(Locale("es", "CO"))
    }

    @Test
    fun `formatLongDate should format date correctly for Spanish locale`() {
        val date = LocalDate.of(2023, 10, 26)
        val formattedDate = DateFormatter.formatLongDate(date, mockContext)
        assertNotNull(formattedDate)
        assertTrue(formattedDate.contains("26"))
        assertTrue(formattedDate.contains("octubre") || formattedDate.contains("October"))
    }

    @Test
    fun `formatShortDateTime should format date and time correctly for Spanish locale`() {
        val dateTime = LocalDateTime.of(2023, 10, 26, 14, 30)
        val formattedDateTime = DateFormatter.formatShortDateTime(dateTime, mockContext)
        assertNotNull(formattedDateTime)
        assertTrue(formattedDateTime.contains("26/10/2023"))
        assertTrue(formattedDateTime.contains("14:30"))
    }

    @Test
    fun `formatDayMonth should format day and month correctly for Spanish locale`() {
        val date = LocalDate.of(2023, 10, 26)
        val formattedDate = DateFormatter.formatDayMonth(date, mockContext)
        assertEquals("26/10", formattedDate)
    }

    @Test
    fun `formatFullDate should format full date correctly for Spanish locale`() {
        val date = LocalDate.of(2023, 10, 26)
        val formattedDate = DateFormatter.formatFullDate(date, mockContext)
        assertNotNull(formattedDate)
        assertTrue(formattedDate.contains("26"))
        assertTrue(formattedDate.contains("octubre") || formattedDate.contains("October"))
        assertTrue(formattedDate.contains("2023"))
    }

    @Test
    fun `formatShortDateTime should handle different locales`() {
        val dateTime = LocalDateTime.of(2023, 10, 26, 14, 30)
        
        // Test Spanish locale
        val mockContextEs = mockk<Context>()
        val mockConfigurationEs = mockk<Configuration>()
        every { mockContextEs.resources } returns RuntimeEnvironment.getApplication().resources
        every { mockContextEs.resources.configuration } returns mockConfigurationEs
        every { mockConfigurationEs.locales } returns android.os.LocaleList(Locale("es", "CO"))
        
        val formattedDateTimeEs = DateFormatter.formatShortDateTime(dateTime, mockContextEs)
        assertNotNull(formattedDateTimeEs)
        assertTrue(formattedDateTimeEs.contains("26/10/2023"))
    }

    @Test
    fun `formatLongDate should handle English locale`() {
        val date = LocalDate.of(2023, 10, 26)
        
        val mockContextEn = mockk<Context>()
        val mockConfigurationEn = mockk<Configuration>()
        every { mockContextEn.resources } returns RuntimeEnvironment.getApplication().resources
        every { mockContextEn.resources.configuration } returns mockConfigurationEn
        every { mockConfigurationEn.locales } returns android.os.LocaleList(Locale("en", "US"))
        
        val formattedDate = DateFormatter.formatLongDate(date, mockContextEn)
        assertNotNull(formattedDate)
        assertTrue(formattedDate.contains("26"))
        assertTrue(formattedDate.contains("October") || formattedDate.contains("octubre"))
    }

    @Test
    fun `formatDayMonth should handle English locale`() {
        val date = LocalDate.of(2023, 10, 26)
        
        val mockContextEn = mockk<Context>()
        val mockConfigurationEn = mockk<Configuration>()
        every { mockContextEn.resources } returns RuntimeEnvironment.getApplication().resources
        every { mockContextEn.resources.configuration } returns mockConfigurationEn
        every { mockConfigurationEn.locales } returns android.os.LocaleList(Locale("en", "US"))
        
        val formattedDate = DateFormatter.formatDayMonth(date, mockContextEn)
        assertEquals("10/26", formattedDate)
    }

    @Test
    fun `formatShortDateTime should handle English locale`() {
        val dateTime = LocalDateTime.of(2023, 10, 26, 14, 30)
        
        val mockContextEn = mockk<Context>()
        val mockConfigurationEn = mockk<Configuration>()
        every { mockContextEn.resources } returns RuntimeEnvironment.getApplication().resources
        every { mockContextEn.resources.configuration } returns mockConfigurationEn
        every { mockConfigurationEn.locales } returns android.os.LocaleList(Locale("en", "US"))
        
        val formattedDateTime = DateFormatter.formatShortDateTime(dateTime, mockContextEn)
        assertNotNull(formattedDateTime)
        assertTrue(formattedDateTime.contains("10/26/2023"))
    }

    @Test
    fun `formatFullDate should handle English locale`() {
        val date = LocalDate.of(2023, 10, 26)
        
        val mockContextEn = mockk<Context>()
        val mockConfigurationEn = mockk<Configuration>()
        every { mockContextEn.resources } returns RuntimeEnvironment.getApplication().resources
        every { mockContextEn.resources.configuration } returns mockConfigurationEn
        every { mockConfigurationEn.locales } returns android.os.LocaleList(Locale("en", "US"))
        
        val formattedDate = DateFormatter.formatFullDate(date, mockContextEn)
        assertNotNull(formattedDate)
        assertTrue(formattedDate.contains("26"))
        assertTrue(formattedDate.contains("October") || formattedDate.contains("octubre"))
        assertTrue(formattedDate.contains("2023"))
    }
}
