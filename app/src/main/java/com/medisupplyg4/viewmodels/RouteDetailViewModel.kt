package com.medisupplyg4.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.medisupplyg4.R
import com.medisupplyg4.models.RouteDetail
import com.medisupplyg4.repositories.RouteDetailRepository
import com.medisupplyg4.utils.SessionManager
import kotlinx.coroutines.launch

class RouteDetailViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "RouteDetailViewModel"
    }

    private val repository = RouteDetailRepository()

    private val _routeDetail = MutableLiveData<RouteDetail?>()
    val routeDetail: LiveData<RouteDetail?> = _routeDetail

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadRouteDetail(rutaId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val token = SessionManager.getToken(getApplication()) ?: ""
                val result = repository.getRouteDetail(token, rutaId, getApplication())
                
                if (result.isSuccess) {
                    _routeDetail.value = result.getOrNull()
                } else {
                    val exception = result.exceptionOrNull()
                    Log.e(TAG, "Error cargando detalle de ruta: ${exception?.message}")
                    _error.value = exception?.message ?: getApplication<Application>().getString(R.string.error_unknown)
                    _routeDetail.value = null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción al cargar detalle de ruta", e)
                _error.value = e.message ?: getApplication<Application>().getString(R.string.error_unknown)
                _routeDetail.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadRouteByDate(fecha: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val token = SessionManager.getToken(getApplication()) ?: ""
                val result = repository.getRouteByDate(token, fecha, getApplication())
                
                if (result.isSuccess) {
                    val route = result.getOrNull()
                    if (route != null) {
                        _routeDetail.value = route
                    } else {
                        _error.value = getApplication<Application>().getString(R.string.route_not_found)
                        _routeDetail.value = null
                    }
                } else {
                    val exception = result.exceptionOrNull()
                    Log.e(TAG, "Error cargando ruta por fecha: ${exception?.message}")
                    _error.value = exception?.message ?: getApplication<Application>().getString(R.string.error_unknown)
                    _routeDetail.value = null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción al cargar ruta por fecha", e)
                _error.value = e.message ?: getApplication<Application>().getString(R.string.error_unknown)
                _routeDetail.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    /**
     * Establece el detalle de la ruta directamente sin hacer petición
     */
    fun setRouteDetail(routeDetail: RouteDetail) {
        _routeDetail.value = routeDetail
        _isLoading.value = false
        _error.value = null
    }
}

