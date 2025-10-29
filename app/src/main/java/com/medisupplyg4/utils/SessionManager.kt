package com.medisupplyg4.utils

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREFS_NAME = "MediSupplySession"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_PHONE = "user_phone"
    private const val KEY_USER_ADDRESS = "user_address"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(context: Context, token: String) {
        getSharedPreferences(context).edit()
            .putString(KEY_ACCESS_TOKEN, token)
            .apply()
    }

    fun getToken(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_ACCESS_TOKEN, null)
    }

    fun saveUserRole(context: Context, role: String) {
        getSharedPreferences(context).edit()
            .putString(KEY_USER_ROLE, role)
            .apply()
    }

    fun getUserRole(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_USER_ROLE, null)
    }

    fun saveUserEmail(context: Context, email: String) {
        getSharedPreferences(context).edit()
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    fun getUserEmail(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_USER_EMAIL, null)
    }

    fun saveUserId(context: Context, userId: String) {
        getSharedPreferences(context).edit()
            .putString(KEY_USER_ID, userId)
            .apply()
    }

    fun getUserId(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_USER_ID, null)
    }

    fun saveUserName(context: Context, name: String) {
        getSharedPreferences(context).edit()
            .putString(KEY_USER_NAME, name)
            .apply()
    }

    fun getUserName(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_USER_NAME, null)
    }

    fun saveUserPhone(context: Context, phone: String) {
        getSharedPreferences(context).edit()
            .putString(KEY_USER_PHONE, phone)
            .apply()
    }

    fun getUserPhone(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_USER_PHONE, null)
    }

    fun saveUserAddress(context: Context, address: String) {
        getSharedPreferences(context).edit()
            .putString(KEY_USER_ADDRESS, address)
            .apply()
    }

    fun getUserAddress(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_USER_ADDRESS, null)
    }

    fun clearSession(context: Context) {
        getSharedPreferences(context).edit().clear().apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        return getToken(context) != null
    }
}
