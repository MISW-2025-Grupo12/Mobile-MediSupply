package com.medisupplyg4.base

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.medisupplyg4.utils.LocaleHelper

abstract class BaseActivity : ComponentActivity() {
    
    companion object {
        private const val PREFS_NAME = "MediSupplyPrefs"
        private const val KEY_SELECTED_LANGUAGE = "selected_language"
    }
    
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val language = prefs.getString(KEY_SELECTED_LANGUAGE, "es") ?: "es"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, language))
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    
    fun changeLanguage(language: String) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_SELECTED_LANGUAGE, language).apply()
        
        // Recreate activity to apply language change
        recreate()
    }
}
