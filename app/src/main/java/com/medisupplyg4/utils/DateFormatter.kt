package com.medisupplyg4.utils

import android.content.Context
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object DateFormatter {
    
    /**
     * Obtiene el locale actual del contexto
     */
    private fun getCurrentLocale(context: Context): Locale {
        return context.resources.configuration.locales[0]
    }
    
    /**
     * Formatea una fecha en formato largo (ej: "lunes, 15 de enero de 2024")
     */
    fun formatLongDate(date: LocalDate, context: Context): String {
        val locale = getCurrentLocale(context)
        val formatter = when (locale.language) {
            "es" -> DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", locale)
            "en" -> DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", locale)
            else -> DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", locale)
        }
        val formattedDate = formatter.format(date)
        return formattedDate.substring(0, 1).uppercase(locale) + formattedDate.substring(1)
    }
    
    /**
     * Formatea una fecha en formato corto (ej: "15/01/2024 14:30" o "01/15/2024 2:30 PM")
     */
    fun formatShortDateTime(dateTime: LocalDateTime, context: Context): String {
        val locale = getCurrentLocale(context)
        val formatter = when (locale.language) {
            "es" -> DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", locale)
            "en" -> DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", locale)
            else -> DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", locale)
        }
        return formatter.format(dateTime)
    }
    
    /**
     * Formatea una fecha en formato día/mes (ej: "15/1" o "1/15")
     */
    fun formatDayMonth(date: LocalDate, context: Context): String {
        val locale = getCurrentLocale(context)
        return when (locale.language) {
            "es" -> "${date.dayOfMonth}/${date.monthValue}"
            "en" -> "${date.monthValue}/${date.dayOfMonth}"
            else -> "${date.dayOfMonth}/${date.monthValue}"
        }
    }

    /**
     * Formatea una fecha en formato de fecha completa (ej: "15 de enero de 2024" o "January 15, 2024")
     */
    fun formatFullDate(date: LocalDate, context: Context): String {
        val locale = getCurrentLocale(context)
        val formatter = when (locale.language) {
            "es" -> DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", locale)
            "en" -> DateTimeFormatter.ofPattern("MMMM d, yyyy", locale)
            else -> DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", locale)
        }
        return formatter.format(date)
    }
}

