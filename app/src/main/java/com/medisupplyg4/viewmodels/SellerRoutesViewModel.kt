package com.medisupplyg4.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.medisupplyg4.models.SellerAPI
import com.medisupplyg4.models.VisitAPI
import com.medisupplyg4.repositories.SellerRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel to handle seller visit routes logic
 */
class SellerRoutesViewModel(application: Application) : AndroidViewModel(application) {
    
    companion object {
        private const val TAG = "SellerRoutesViewModel"
    }
    
    private val repository = SellerRepository()
    
    // Current seller state
    private val _currentSeller = MutableLiveData<SellerAPI?>()
    val currentSeller: LiveData<SellerAPI?> = _currentSeller
    
    // Visits state
    private val _visits = MutableLiveData<List<VisitAPI>>()
    val visits: LiveData<List<VisitAPI>> = _visits
    
    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Error state
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    // Selected dates for filter
    private val _startDate = MutableLiveData<LocalDate>()
    val startDate: LiveData<LocalDate> = _startDate
    
    private val _endDate = MutableLiveData<LocalDate>()
    val endDate: LiveData<LocalDate> = _endDate
    
    init {
        // Initialize with current date
        val today = LocalDate.now()
        _startDate.value = today
        _endDate.value = today
        
        // Load current seller
        loadCurrentSeller()
    }
    
    /**
     * Loads the current seller
     */
    private fun loadCurrentSeller() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val seller = repository.getCurrentSeller()
                _currentSeller.value = seller
                
                if (seller != null) {
                    Log.d(TAG, "Vendedor cargado: ${seller.nombre}")
                    // Load visits automatically when seller is obtained
                    loadVisits()
                } else {
                    _error.value = "No se pudo obtener el vendedor actual"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar vendedor", e)
                _error.value = "Error al cargar vendedor: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Loads visits for the current seller with selected dates
     */
    fun loadVisits() {
        val seller = _currentSeller.value
        val startDate = _startDate.value
        val endDate = _endDate.value
        
        if (seller == null || startDate == null || endDate == null) {
            Log.w(TAG, "No se pueden cargar visitas: vendedor o fechas nulas")
            return
        }
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val visits = repository.getSellerVisits(
                    vendedorId = seller.id,
                    fechaInicio = startDate,
                    fechaFin = endDate
                )
                
                _visits.value = visits
                Log.d(TAG, "Visitas cargadas: ${visits.size}")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar visitas", e)
                _error.value = "Error al cargar visitas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Sets a date range
     */
    fun setDateRange(startDate: LocalDate, endDate: LocalDate) {
        // Ensure start date is not after end date
        val finalStartDate = if (startDate.isAfter(endDate)) endDate else startDate
        val finalEndDate = if (startDate.isAfter(endDate)) startDate else endDate
        
        _startDate.value = finalStartDate
        _endDate.value = finalEndDate
        loadVisits()
    }
    
    /**
     * Clears the error
     */
    fun clearError() {
        _error.value = null
    }
}
