package com.medisupplyg4.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*

object LocaleHelper {
    
    fun setLocale(context: Context, language: String): Context {
        return updateResources(context, language)
    }
    
    private fun updateResources(context: Context, language: String): Context {
        val locale = when (language) {
            "es" -> Locale("es", "CO") // Español Colombia
            "en" -> Locale("en", "US") // Inglés USA
            else -> Locale(language)
        }
        Locale.setDefault(locale)
        
        val configuration = Configuration(context.resources.configuration)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
            return context.createConfigurationContext(configuration)
        } else {
            @Suppress("DEPRECATION")
            configuration.locale = locale
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
        }
        
        return context
    }
}
