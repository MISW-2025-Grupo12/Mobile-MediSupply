package com.medisupplyg4.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.medisupplyg4.models.VendedorAPI
import com.medisupplyg4.models.VisitaAPI
import com.medisupplyg4.repositories.VendedorRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel para manejar la lógica de rutas de visitas del vendedor
 */
class VendedorRoutesViewModel(application: Application) : AndroidViewModel(application) {
    
    companion object {
        private const val TAG = "VendedorRoutesViewModel"
    }
    
    private val repository = VendedorRepository()
    
    // Estado del vendedor actual
    private val _vendedorActual = MutableLiveData<VendedorAPI?>()
    val vendedorActual: LiveData<VendedorAPI?> = _vendedorActual
    
    // Estado de las visitas
    private val _visitas = MutableLiveData<List<VisitaAPI>>()
    val visitas: LiveData<List<VisitaAPI>> = _visitas
    
    // Estado de carga
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Estado de error
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    // Fechas seleccionadas para el filtro
    private val _fechaInicio = MutableLiveData<LocalDate>()
    val fechaInicio: LiveData<LocalDate> = _fechaInicio
    
    private val _fechaFin = MutableLiveData<LocalDate>()
    val fechaFin: LiveData<LocalDate> = _fechaFin
    
    init {
        // Inicializar con la fecha actual
        val hoy = LocalDate.now()
        _fechaInicio.value = hoy
        _fechaFin.value = hoy
        
        // Cargar el vendedor actual
        loadVendedorActual()
    }
    
    /**
     * Carga el vendedor actual
     */
    private fun loadVendedorActual() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val vendedor = repository.getVendedorActual()
                _vendedorActual.value = vendedor
                
                if (vendedor != null) {
                    Log.d(TAG, "Vendedor cargado: ${vendedor.nombre}")
                    // Cargar visitas automáticamente cuando se obtiene el vendedor
                    loadVisitas()
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
     * Carga las visitas del vendedor actual con las fechas seleccionadas
     */
    fun loadVisitas() {
        val vendedor = _vendedorActual.value
        val fechaInicio = _fechaInicio.value
        val fechaFin = _fechaFin.value
        
        if (vendedor == null || fechaInicio == null || fechaFin == null) {
            Log.w(TAG, "No se pueden cargar visitas: vendedor o fechas nulas")
            return
        }
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val visitas = repository.getVisitasVendedor(
                    vendedorId = vendedor.id,
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin
                )
                
                _visitas.value = visitas
                Log.d(TAG, "Visitas cargadas: ${visitas.size}")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar visitas", e)
                _error.value = "Error al cargar visitas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Establece un rango de fechas
     */
    fun setRangoFechas(fechaInicio: LocalDate, fechaFin: LocalDate) {
        // Asegurar que la fecha de inicio no sea posterior a la fecha de fin
        val fechaInicioFinal = if (fechaInicio.isAfter(fechaFin)) fechaFin else fechaInicio
        val fechaFinFinal = if (fechaInicio.isAfter(fechaFin)) fechaInicio else fechaFin
        
        _fechaInicio.value = fechaInicioFinal
        _fechaFin.value = fechaFinFinal
        loadVisitas()
    }
    
    /**
     * Limpia el error
     */
    fun clearError() {
        _error.value = null
    }
}
