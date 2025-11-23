package com.medisupplyg4.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.medisupplyg4.R
import com.medisupplyg4.models.RouteDetail
import com.medisupplyg4.models.RoutePeriod
import com.medisupplyg4.models.SimpleDelivery
import com.medisupplyg4.repositories.DeliveryRouteRepository
import com.medisupplyg4.utils.SessionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate

class DeliveryRouteViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "DeliveryRouteViewModel"
    }

    private val deliveryRouteRepository = DeliveryRouteRepository()

    private val _routes = MutableLiveData<List<RouteDetail>>()
    private val _deliveries = MutableLiveData<List<SimpleDelivery>>()
    private val _isLoading = MutableLiveData(false)
    private val _selectedPeriod = MutableLiveData(RoutePeriod.DAY)
    private val _selectedDate = MutableLiveData(LocalDate.now())

    // Job para cancelar llamadas anteriores
    private var loadRoutesJob: Job? = null

    val routes: LiveData<List<RouteDetail>>
        get() = _routes

    val deliveries: LiveData<List<SimpleDelivery>>
        get() = _deliveries

    val isLoading: LiveData<Boolean>
        get() = _isLoading

    val selectedPeriod: LiveData<RoutePeriod>
        get() = _selectedPeriod

    val selectedDate: LiveData<LocalDate>
        get() = _selectedDate

    private val _eventNetworkError = MutableLiveData(false)

    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    init {
        loadRoutes()
    }

    fun loadRoutes(page: Int = 1, pageSize: Int = 20) {
        // Cancelar llamada anterior si existe
        loadRoutesJob?.cancel()
        
        loadRoutesJob = viewModelScope.launch {
            try {
                _isLoading.value = true
                val date = _selectedDate.value ?: LocalDate.now()
                val period = _selectedPeriod.value ?: RoutePeriod.DAY
                
                // Obtener token de autenticación y repartidor_id
                val token = SessionManager.getToken(getApplication()) ?: ""
                val repartidorId = SessionManager.getUserId(getApplication()) ?: ""
                
                if (repartidorId.isEmpty()) {
                    Log.w(TAG, "No se encontró repartidor_id en la sesión")
                    _routes.value = emptyList()
                    _isLoading.value = false
                    return@launch
                }

                // Obtener rutas del repartidor
                val fetchedRoutes = deliveryRouteRepository.getRoutes(token, repartidorId, date, period)

                // Ordenar rutas por fecha de ruta
                _routes.value = fetchedRoutes.sortedBy { route -> route.fechaRutaLocalDate }
                _eventNetworkError.value = false
                _isLoading.value = false
            } catch (error: Exception) {
                // Solo logear errores reales, no cancelaciones
                if (error !is kotlinx.coroutines.CancellationException) {
                    Log.e(TAG, "Error cargando rutas", error)
                    _eventNetworkError.value = true
                }
                _isLoading.value = false
            }
        }
    }

    fun setSelectedPeriod(period: RoutePeriod) {
        _selectedPeriod.value = period
        loadRoutes()
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        loadRoutes()
    }

    fun onNetworkErrorShown() {
        _eventNetworkError.value = false
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DeliveryRouteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DeliveryRouteViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
