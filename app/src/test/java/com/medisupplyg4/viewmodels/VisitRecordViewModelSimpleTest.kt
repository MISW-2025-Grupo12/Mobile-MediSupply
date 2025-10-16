package com.medisupplyg4.viewmodels

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VisitRecordViewModelSimpleTest {

    @Test
    fun `getFechaErrorMessage should return error for empty date`() {
        // Given
        val emptyDate = ""
        
        // When
        val result = VisitRecordViewModel.getFechaErrorMessageStatic(emptyDate)
        
        // Then
        assertEquals(VisitRecordViewModel.ERROR_DATE_REQUIRED, result)
    }

    @Test
    fun `getFechaErrorMessage should return error for invalid date format`() {
        // Given
        val invalidDate = "2024-01-15" // Wrong format
        
        // When
        val result = VisitRecordViewModel.getFechaErrorMessageStatic(invalidDate)
        
        // Then
        assertEquals(VisitRecordViewModel.ERROR_DATE_FORMAT, result)
    }

    @Test
    fun `getFechaErrorMessage should return null for valid date`() {
        // Given
        val validDate = java.time.LocalDate.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        
        // When
        val result = VisitRecordViewModel.getFechaErrorMessageStatic(validDate)
        
        // Then
        assertNull(result)
    }

    @Test
    fun `getHoraErrorMessage should return error for empty time`() {
        // Given
        val emptyTime = ""
        
        // When
        val result = VisitRecordViewModel.getHoraErrorMessageStatic(emptyTime)
        
        // Then
        assertEquals(VisitRecordViewModel.ERROR_TIME_REQUIRED, result)
    }

    @Test
    fun `getHoraErrorMessage should return error for invalid time format`() {
        // Given
        val invalidTime = "2:30 PM" // Wrong format
        
        // When
        val result = VisitRecordViewModel.getHoraErrorMessageStatic(invalidTime)
        
        // Then
        assertEquals(VisitRecordViewModel.ERROR_TIME_FORMAT, result)
    }

    @Test
    fun `getHoraErrorMessage should return null for valid time`() {
        // Given
        val validTime = "14:30"
        
        // When
        val result = VisitRecordViewModel.getHoraErrorMessageStatic(validTime)
        
        // Then
        assertNull(result)
    }

    @Test
    fun `getNovedadesErrorMessage should return error for notes too long`() {
        // Given
        val longNotes = "a".repeat(501) // Exceeds 500 character limit
        
        // When
        val result = VisitRecordViewModel.getNovedadesErrorMessageStatic(longNotes)
        
        // Then
        assertEquals(VisitRecordViewModel.ERROR_NOTES_MAX_LENGTH, result)
    }

    @Test
    fun `getNovedadesErrorMessage should return null for valid notes`() {
        // Given
        val validNotes = "Valid notes"
        
        // When
        val result = VisitRecordViewModel.getNovedadesErrorMessageStatic(validNotes)
        
        // Then
        assertNull(result)
    }
}