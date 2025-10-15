package com.medisupplyg4.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.medisupplyg4.base.BaseActivity
import com.medisupplyg4.models.Language
import com.medisupplyg4.models.UserRole

class StartupViewModel(application: Application) : AndroidViewModel(application) {
    
    companion object {
        private const val TAG = "StartupViewModel"
        private const val PREFS_NAME = "MediSupplyPrefs"
        private const val KEY_SELECTED_LANGUAGE = "selected_language"
        private const val KEY_SELECTED_ROLE = "selected_role"
        private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
    }
    
    private val prefs: SharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val _selectedLanguage = MutableLiveData<Language>()
    val selectedLanguage: LiveData<Language> = _selectedLanguage
    
    private val _selectedRole = MutableLiveData<UserRole?>()
    val selectedRole: LiveData<UserRole?> = _selectedRole

    private val _isFirstLaunch = MutableLiveData<Boolean>()
    
    init {
        loadSavedLanguage()
        loadSavedRole()
        checkFirstLaunch()
    }
    
    private fun loadSavedLanguage() {
        val savedLanguageCode = prefs.getString(KEY_SELECTED_LANGUAGE, Language.SPANISH.code)
        val language = Language.getByCode(savedLanguageCode ?: Language.SPANISH.code) ?: Language.SPANISH
        Log.d(TAG, "Loading saved language: $savedLanguageCode -> $language")
        _selectedLanguage.value = language
    }
    
    private fun checkFirstLaunch() {
        val isFirst = prefs.getBoolean(KEY_IS_FIRST_LAUNCH, true)
        _isFirstLaunch.value = isFirst
    }
    
    fun selectLanguage(language: Language) {
        Log.d(TAG, "Selecting language: $language")
        _selectedLanguage.value = language
        saveLanguage(language)
    }
    
    private fun saveLanguage(language: Language) {
        prefs.edit()
            .putString(KEY_SELECTED_LANGUAGE, language.code)
            .apply()
    }
    
    fun applyLanguageChange(activity: BaseActivity, language: Language) {
        Log.d(TAG, "Applying language change: ${language.code}")
        activity.changeLanguage(language.code)
    }
    
    fun markAsCompleted() {
        prefs.edit()
            .putBoolean(KEY_IS_FIRST_LAUNCH, false)
            .apply()
        _isFirstLaunch.value = false
    }

    
    fun selectRole(role: UserRole) {
        Log.d(TAG, "Role selected: ${role.name}")
        _selectedRole.value = role
        prefs.edit()
            .putString(KEY_SELECTED_ROLE, role.name)
            .apply()
    }
    
    fun loadSavedRole() {
        val roleName = prefs.getString(KEY_SELECTED_ROLE, null)
        if (roleName != null) {
            try {
                val role = UserRole.valueOf(roleName)
                _selectedRole.value = role
                Log.d(TAG, "Loaded saved role: $roleName")
            } catch (e: IllegalArgumentException) {
                Log.w(TAG, "Invalid role saved: $roleName")
                _selectedRole.value = null
            }
        } else {
            _selectedRole.value = null
        }
    }
}
