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
import com.medisupplyg4.utils.SessionManager
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
    
    // Pagination state
    private val _currentPage = MutableLiveData(1)
    val currentPage: LiveData<Int> = _currentPage

    private val _hasMorePages = MutableLiveData(true)
    val hasMorePages: LiveData<Boolean> = _hasMorePages

    private val _isLoadingMore = MutableLiveData(false)
    val isLoadingMore: LiveData<Boolean> = _isLoadingMore
    
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
        // Initialize with current month (first day to last day)
        val today = LocalDate.now()
        val firstDayOfMonth = today.withDayOfMonth(1)
        val lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth())
        _startDate.value = firstDayOfMonth
        _endDate.value = lastDayOfMonth
        
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
                
                val token = SessionManager.getToken(getApplication()) ?: ""
                val seller = repository.getCurrentSeller(token, getApplication())
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
     * Loads visits for the current seller with selected dates (resets pagination)
     */
    fun loadVisits() {
        val seller = _currentSeller.value
        val startDate = _startDate.value
        val endDate = _endDate.value
        
        if (seller == null || startDate == null || endDate == null) {
            Log.w(TAG, "No se pueden cargar visitas: vendedor o fechas nulas")
            return
        }
        
        // Reset pagination
        _currentPage.value = 1
        _hasMorePages.value = true
        _visits.value = emptyList()
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val token = SessionManager.getToken(getApplication()) ?: ""
                val result = repository.getSellerVisitsPaginated(
                    token = token,
                    vendedorId = seller.id,
                    fechaInicio = startDate,
                    fechaFin = endDate,
                    page = 1,
                    pageSize = 10,
                    context = getApplication()
                )
                
                if (result.isSuccess) {
                    val paginatedResponse = result.getOrNull()
                    val visits = paginatedResponse?.items ?: emptyList()
                    val pagination = paginatedResponse?.pagination
                    
                    _visits.value = visits
                    _hasMorePages.value = pagination?.hasNext ?: false
                    _currentPage.value = 1
                    
                    Log.d(TAG, "Visitas cargadas: ${visits.size} de ${pagination?.totalItems ?: 0}")
                } else {
                    _error.value = "Error al cargar visitas: ${result.exceptionOrNull()?.message}"
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar visitas", e)
                _error.value = "Error al cargar visitas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Loads more visits (next page) for infinite scroll
     */
    fun loadMoreVisits() {
        val seller = _currentSeller.value
        val startDate = _startDate.value
        val endDate = _endDate.value
        val currentPage = _currentPage.value ?: 1
        val hasMore = _hasMorePages.value ?: false
        
        if (seller == null || startDate == null || endDate == null || !hasMore) {
            Log.d(TAG, "No se pueden cargar más visitas: vendedor nulo, fechas nulas o no hay más páginas")
            return
        }
        
        viewModelScope.launch {
            try {
                _isLoadingMore.value = true
                
                val token = SessionManager.getToken(getApplication()) ?: ""
                val nextPage = currentPage + 1
                
                val result = repository.getSellerVisitsPaginated(
                    token = token,
                    vendedorId = seller.id,
                    fechaInicio = startDate,
                    fechaFin = endDate,
                    page = nextPage,
                    pageSize = 10,
                    context = getApplication()
                )
                
                if (result.isSuccess) {
                    val paginatedResponse = result.getOrNull()
                    val newVisits = paginatedResponse?.items ?: emptyList()
                    val pagination = paginatedResponse?.pagination
                    
                    // Append new visits to existing list
                    val currentVisits = _visits.value ?: emptyList()
                    _visits.value = currentVisits + newVisits
                    _hasMorePages.value = pagination?.hasNext ?: false
                    _currentPage.value = nextPage
                    
                    Log.d(TAG, "Más visitas cargadas: ${newVisits.size} (página $nextPage)")
                } else {
                    Log.e(TAG, "Error al cargar más visitas: ${result.exceptionOrNull()?.message}")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar más visitas", e)
            } finally {
                _isLoadingMore.value = false
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
