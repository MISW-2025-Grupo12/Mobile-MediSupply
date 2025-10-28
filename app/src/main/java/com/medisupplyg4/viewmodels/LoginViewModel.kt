package com.medisupplyg4.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.medisupplyg4.R
import com.medisupplyg4.models.LoginRequest
import com.medisupplyg4.models.LoginResponse
import com.medisupplyg4.models.UserRole
import com.medisupplyg4.repositories.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val repository = LoginRepository()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<Result<LoginResponse>?>()
    val loginResult: LiveData<Result<LoginResponse>?> = _loginResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun login(email: String, password: String) {
        // Validaciones básicas
        if (email.isBlank()) {
            _error.value = getApplication<Application>().getString(R.string.error_email_required)
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _error.value = getApplication<Application>().getString(R.string.error_email_required)
            return
        }
        if (password.isBlank()) {
            _error.value = getApplication<Application>().getString(R.string.error_password_length)
            return
        }

        val request = LoginRequest(
            email = email.trim().lowercase(),
            password = password
        )

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _loginResult.value = null

                Log.d(TAG, "Iniciando login")
                val result = repository.login(request, getApplication())
                
                _loginResult.value = result
                
                if (result.isSuccess) {
                    Log.d(TAG, "Login exitoso: ${result.getOrNull()?.user_info?.tipo_usuario}")
                } else {
                    Log.e(TAG, "Error en login: ${result.exceptionOrNull()?.message}")
                    _error.value = result.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.error_unknown)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción durante el login", e)
                _error.value = getApplication<Application>().getString(R.string.error_connection_error, e.message ?: "")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearLoginResult() {
        _loginResult.value = null
    }

    // Función para convertir tipo_usuario a UserRole
    fun getUserRoleFromTipoUsuario(tipoUsuario: String): UserRole {
        return when (tipoUsuario.uppercase()) {
            "CLIENTE" -> UserRole.CLIENT
            "VENDEDOR" -> UserRole.SELLER
            "REPARTIDOR" -> UserRole.DRIVER
            else -> UserRole.CLIENT // Default
        }
    }
}
