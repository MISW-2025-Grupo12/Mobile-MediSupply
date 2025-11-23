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
import com.medisupplyg4.models.PaginatedResponse
import com.medisupplyg4.models.SimpleDelivery
import com.medisupplyg4.models.SimpleDeliveryResponse
import com.medisupplyg4.network.NetworkClient
import com.medisupplyg4.utils.SessionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ClientDeliveriesViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "ClientDeliveriesViewModel"
    }

    private val _deliveries = MutableLiveData<List<SimpleDelivery>>()
    private val _isLoading = MutableLiveData(false)
    private val _errorMessage = MutableLiveData<String?>(null)
    private val _selectedStartDate = MutableLiveData<LocalDate?>(null)
    private val _selectedEndDate = MutableLiveData<LocalDate?>(null)

    // Job para cancelar llamadas anteriores
    private var loadDeliveriesJob: Job? = null

    val deliveries: LiveData<List<SimpleDelivery>>
        get() = _deliveries

    val isLoading: LiveData<Boolean>
        get() = _isLoading

    val errorMessage: LiveData<String?>
        get() = _errorMessage

    val selectedStartDate: LiveData<LocalDate?>
        get() = _selectedStartDate

    val selectedEndDate: LiveData<LocalDate?>
        get() = _selectedEndDate

    fun loadDeliveries(clienteId: String, page: Int = 1, pageSize: Int = 100) {
        // Cancelar llamada anterior si existe
        loadDeliveriesJob?.cancel()

        loadDeliveriesJob = viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                // Obtener token de autenticación
                val token = SessionManager.getToken(getApplication()) ?: ""
                
                if (token.isEmpty()) {
                    _errorMessage.value = getApplication<Application>().getString(R.string.error_token_not_found)
                    _isLoading.value = false
                    return@launch
                }

                // Formatear fechas si están disponibles
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE
                val fechaInicio = _selectedStartDate.value?.format(formatter)
                val fechaFin = _selectedEndDate.value?.format(formatter)

                Log.d(TAG, "Obteniendo entregas para cliente: $clienteId, desde: $fechaInicio, hasta: $fechaFin")

                val response: Response<PaginatedResponse<SimpleDeliveryResponse>> = 
                    NetworkClient.deliveryApiService.getDeliveriesByClienteId(
                        token = "Bearer $token",
                        clienteId = clienteId,
                        fechaInicio = fechaInicio,
                        fechaFin = fechaFin,
                        page = page,
                        pageSize = pageSize
                    )

                if (response.isSuccessful) {
                    val paginatedResponse = response.body()
                    val deliveryResponses = paginatedResponse?.items ?: emptyList()
                    Log.d(TAG, "Datos recibidos: ${deliveryResponses.size} entregas de ${paginatedResponse?.pagination?.totalItems ?: 0}")

                    // Convertir a SimpleDelivery estándar
                    val deliveries = deliveryResponses.map { it.toSimpleDelivery() }

                    // Ordenar entregas por fecha de entrega (ascendente - más antiguas primero)
                    _deliveries.value = deliveries.sortedBy { delivery -> delivery.fechaEntrega }
                    _errorMessage.value = null
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.w(TAG, "Error del backend: ${response.code()}, body: $errorBody")
                    _errorMessage.value = getApplication<Application>().getString(R.string.error_load_deliveries, response.code())
                }
                _isLoading.value = false
            } catch (error: Exception) {
                // Solo logear errores reales, no cancelaciones
                if (error !is kotlinx.coroutines.CancellationException) {
                    Log.e(TAG, "Error al cargar entregas", error)
                    val errorMessage = error.message ?: ""
                    _errorMessage.value = getApplication<Application>().getString(R.string.error_connection_error, errorMessage)
                }
                _isLoading.value = false
            }
        }
    }

    fun setDateRange(startDate: LocalDate?, endDate: LocalDate?) {
        _selectedStartDate.value = startDate
        _selectedEndDate.value = endDate
    }

    fun clearError() {
        _errorMessage.value = null
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClientDeliveriesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ClientDeliveriesViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}

