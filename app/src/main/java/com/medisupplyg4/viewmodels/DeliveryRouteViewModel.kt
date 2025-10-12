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
import com.medisupplyg4.models.SimpleDelivery
import com.medisupplyg4.models.RoutePeriod
import com.medisupplyg4.repositories.DeliveryRouteRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class DeliveryRouteViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "DeliveryRouteViewModel"
    }

    private val deliveryRouteRepository = DeliveryRouteRepository(application)

    private val _deliveries = MutableLiveData<List<SimpleDelivery>>()
    private val _isLoading = MutableLiveData(false)
    private val _selectedPeriod = MutableLiveData(RoutePeriod.DAY)
    private val _selectedDate = MutableLiveData(LocalDate.now())

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

    fun loadRoutes() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val date = _selectedDate.value ?: LocalDate.now()
                val period = _selectedPeriod.value ?: RoutePeriod.DAY

                val fetchedDeliveries = deliveryRouteRepository.getDeliveries(date, period)

                // Ordenar entregas por fecha de entrega
                _deliveries.value = fetchedDeliveries.sortedBy { delivery -> delivery.fechaEntrega }
                _eventNetworkError.value = false
                _isLoading.value = false
            } catch (error: Exception) {
                Log.d(TAG, error.toString())
                _eventNetworkError.value = true
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
