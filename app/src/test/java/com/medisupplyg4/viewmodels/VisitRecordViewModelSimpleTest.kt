package com.medisupplyg4.viewmodels

import android.app.Application
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class VisitRecordViewModelSimpleTest {

    @Test
    fun `getFechaErrorMessage should return error for empty date`() {
        // Given
        val application = RuntimeEnvironment.getApplication()
        val viewModel = VisitRecordViewModel(application)
        val emptyDate = ""
        
        // When
        val result = viewModel.getFechaErrorMessage(emptyDate)
        
        // Then
        assertEquals(VisitRecordViewModel.ERROR_DATE_REQUIRED, result)
    }

    @Test
    fun `getFechaErrorMessage should return error for invalid date format`() {
        // Given
        val application = RuntimeEnvironment.getApplication()
        val viewModel = VisitRecordViewModel(application)
        val invalidDate = "2024-01-15" // Wrong format
        
        // When
        val result = viewModel.getFechaErrorMessage(invalidDate)
        
        // Then
        assertEquals(VisitRecordViewModel.ERROR_DATE_FORMAT, result)
    }

    @Test
    fun `getFechaErrorMessage should return null for valid date`() {
        // Given
        val application = RuntimeEnvironment.getApplication()
        val viewModel = VisitRecordViewModel(application)
        val validDate = java.time.LocalDate.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        
        // When
        val result = viewModel.getFechaErrorMessage(validDate)
        
        // Then
        assertNull(result)
    }

    @Test
    fun `getHoraErrorMessage should return error for empty time`() {
        // Given
        val application = RuntimeEnvironment.getApplication()
        val viewModel = VisitRecordViewModel(application)
        val emptyTime = ""
        
        // When
        val result = viewModel.getHoraErrorMessage(emptyTime)
        
        // Then
        assertEquals(VisitRecordViewModel.ERROR_TIME_REQUIRED, result)
    }

    @Test
    fun `getHoraErrorMessage should return error for invalid time format`() {
        // Given
        val application = RuntimeEnvironment.getApplication()
        val viewModel = VisitRecordViewModel(application)
        val invalidTime = "2:30 PM" // Wrong format
        
        // When
        val result = viewModel.getHoraErrorMessage(invalidTime)
        
        // Then
        assertEquals(VisitRecordViewModel.ERROR_TIME_FORMAT, result)
    }

    @Test
    fun `getHoraErrorMessage should return null for valid time`() {
        // Given
        val application = RuntimeEnvironment.getApplication()
        val viewModel = VisitRecordViewModel(application)
        val validTime = "14:30"
        
        // When
        val result = viewModel.getHoraErrorMessage(validTime)
        
        // Then
        assertNull(result)
    }

    @Test
    fun `getNovedadesErrorMessage should return error for notes too long`() {
        // Given
        val application = RuntimeEnvironment.getApplication()
        val viewModel = VisitRecordViewModel(application)
        val longNotes = "a".repeat(501) // Exceeds 500 character limit
        
        // When
        val result = viewModel.getNovedadesErrorMessage(longNotes)
        
        // Then
        assertEquals(VisitRecordViewModel.ERROR_NOTES_MAX_LENGTH, result)
    }

    @Test
    fun `getNovedadesErrorMessage should return null for valid notes`() {
        // Given
        val application = RuntimeEnvironment.getApplication()
        val viewModel = VisitRecordViewModel(application)
        val validNotes = "Valid notes"
        
        // When
        val result = viewModel.getNovedadesErrorMessage(validNotes)
        
        // Then
        assertNull(result)
    }
}
