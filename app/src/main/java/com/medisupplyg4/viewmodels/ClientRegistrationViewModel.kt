package com.medisupplyg4.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.medisupplyg4.R
import com.medisupplyg4.models.ClientRegistrationRequest
import com.medisupplyg4.models.ClientRegistrationResponse
import com.medisupplyg4.models.UserRole
import com.medisupplyg4.repositories.ClientRegistrationRepository
import kotlinx.coroutines.launch

class ClientRegistrationViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "ClientRegistrationViewModel"
    }

    private val repository = ClientRegistrationRepository()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _registrationResult = MutableLiveData<Result<ClientRegistrationResponse>?>()
    val registrationResult: LiveData<Result<ClientRegistrationResponse>?> = _registrationResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun registerClient(
        name: String,
        email: String,
        identification: String,
        phone: String,
        address: String,
        password: String,
        confirmPassword: String
    ) {
        // Validaciones básicas
        if (name.isBlank()) {
            _error.value = getApplication<Application>().getString(R.string.error_name_required)
            return
        }
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _error.value = getApplication<Application>().getString(R.string.error_email_required)
            return
        }
        if (identification.isBlank()) {
            _error.value = getApplication<Application>().getString(R.string.error_identification_required)
            return
        }
        if (!identification.all { it.isDigit() }) {
            _error.value = getApplication<Application>().getString(R.string.error_identification_numbers_only)
            return
        }
        if (identification.length < 6 || identification.length > 15) {
            _error.value = getApplication<Application>().getString(R.string.error_identification_length)
            return
        }
        if (phone.isBlank()) {
            _error.value = getApplication<Application>().getString(R.string.error_phone_required)
            return
        }
        if (!phone.all { it.isDigit() }) {
            _error.value = getApplication<Application>().getString(R.string.error_phone_numbers_only)
            return
        }
        if (phone.length < 7 || phone.length > 15) {
            _error.value = getApplication<Application>().getString(R.string.error_phone_length)
            return
        }
        if (address.isBlank()) {
            _error.value = getApplication<Application>().getString(R.string.error_address_required)
            return
        }
        if (password.isBlank() || password.length < 6) {
            _error.value = getApplication<Application>().getString(R.string.error_password_length)
            return
        }
        if (confirmPassword.isBlank()) {
            _error.value = getApplication<Application>().getString(R.string.error_confirm_password_required)
            return
        }
        if (password != confirmPassword) {
            _error.value = getApplication<Application>().getString(R.string.error_passwords_no_match)
            return
        }

        val request = ClientRegistrationRequest(
            nombre = name.trim(),
            email = email.trim().lowercase(),
            identificacion = identification.trim(),
            telefono = phone.trim(),
            direccion = address.trim(),
            password = password
        )

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _registrationResult.value = null

                Log.d(TAG, "Iniciando registro de cliente")
                val result = repository.registerClient(request, getApplication())
                
                _registrationResult.value = result
                
                if (result.isSuccess) {
                    Log.d(TAG, "Registro exitoso: ${result.getOrNull()?.mensaje}")
                } else {
                    Log.e(TAG, "Error en registro: ${result.exceptionOrNull()?.message}")
                    _error.value = result.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.error_unknown)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción durante el registro", e)
                _error.value = getApplication<Application>().getString(R.string.error_connection_error, e.message ?: "")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearRegistrationResult() {
        _registrationResult.value = null
    }
}
