package com.medisupplyg4.models

import com.medisupplyg4.R

/**
 * Modelo para representar un idioma disponible en la aplicación
 */
data class Language(
    val code: String,
    val nameResId: Int,
    val countryResId: Int,
    val flagResId: Int,
    val locale: String
) {
    companion object {
        val SPANISH = Language(
            code = "es",
            nameResId = R.string.language_spanish,
            countryResId = R.string.country_colombia,
            flagResId = R.drawable.colombia_icon_flag,
            locale = "es_CO"
        )
        
        val ENGLISH = Language(
            code = "en",
            nameResId = R.string.language_english,
            countryResId = R.string.country_usa,
            flagResId = R.drawable.usa_icon_flag,
            locale = "en_US"
        )
        
        val AVAILABLE_LANGUAGES = listOf(SPANISH, ENGLISH)
        
        fun getByCode(code: String): Language? {
            return AVAILABLE_LANGUAGES.find { it.code == code }
        }
    }
}

