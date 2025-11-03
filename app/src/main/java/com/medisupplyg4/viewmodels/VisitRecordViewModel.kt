package com.medisupplyg4.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.medisupplyg4.models.VisitRecordRequest
import com.medisupplyg4.repositories.SellerRepository
import com.medisupplyg4.utils.SessionManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * ViewModel to handle visit record logic
 */
class VisitRecordViewModel(application: Application) : AndroidViewModel(application) {
    
    companion object {
        private const val TAG = "VisitRecordViewModel"
        private const val DATE_FORMAT = "dd/MM/yyyy"
        private const val TIME_FORMAT = "HH:mm"
        private const val MAX_NOTES_LENGTH = 500
        
        // Error messages - these should be replaced with string resources in the UI layer
        const val ERROR_RECORDING_VISIT = "ERROR_RECORDING_VISIT"
        const val ERROR_NETWORK_CONNECTION = "ERROR_NETWORK_CONNECTION"
        const val ERROR_DATE_REQUIRED = "ERROR_DATE_REQUIRED"
        const val ERROR_TIME_REQUIRED = "ERROR_TIME_REQUIRED"
        const val ERROR_CLIENT_REQUIRED = "ERROR_CLIENT_REQUIRED"
        const val ERROR_DATE_FORMAT = "ERROR_DATE_FORMAT"
        const val ERROR_TIME_FORMAT = "ERROR_TIME_FORMAT"
        const val ERROR_FUTURE_DATE = "ERROR_FUTURE_DATE"
        const val ERROR_NOTES_MAX_LENGTH = "ERROR_NOTES_MAX_LENGTH"
        
        // Static methods for testing
        fun getFechaErrorMessageStatic(fecha: String): String? {
            if (fecha.isEmpty()) return ERROR_DATE_REQUIRED
            
            return try {
                val parsedDate = LocalDate.parse(fecha, DateTimeFormatter.ofPattern(DATE_FORMAT))
                val today = LocalDate.now()
                if (parsedDate.isAfter(today)) {
                    ERROR_FUTURE_DATE
                } else null
            } catch (e: DateTimeParseException) {
                ERROR_DATE_FORMAT
            }
        }
        
        fun getHoraErrorMessageStatic(hora: String): String? {
            if (hora.isEmpty()) return ERROR_TIME_REQUIRED
            
            return try {
                LocalTime.parse(hora, DateTimeFormatter.ofPattern(TIME_FORMAT))
                null
            } catch (e: DateTimeParseException) {
                ERROR_TIME_FORMAT
            }
        }
        
        fun getNovedadesErrorMessageStatic(novedades: String): String? {
            return if (novedades.length > MAX_NOTES_LENGTH) {
                ERROR_NOTES_MAX_LENGTH
            } else null
        }
    }
    
    private val repository = SellerRepository()
    
    // Form fields
    private val _fecha = MutableLiveData<String>("")
    val fecha: LiveData<String> = _fecha
    
    private val _hora = MutableLiveData<String>("")
    val hora: LiveData<String> = _hora
    
    private val _clienteId = MutableLiveData<String>("")
    val clienteId: LiveData<String> = _clienteId
    
    private val _clienteNombre = MutableLiveData<String>("")
    val clienteNombre: LiveData<String> = _clienteNombre
    
    private val _novedades = MutableLiveData<String>("")
    val novedades: LiveData<String> = _novedades
    
    private val _pedidoGenerado = MutableLiveData<Boolean>(false)
    val pedidoGenerado: LiveData<Boolean> = _pedidoGenerado
    
    // UI state
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _success = MutableLiveData<Boolean>(false)
    val success: LiveData<Boolean> = _success
    
    // Validation state
    private val _isFormValid = MutableLiveData<Boolean>(false)
    val isFormValid: LiveData<Boolean> = _isFormValid
    
    // Visit ID to register
    private var _visitaId: String = ""
    
    init {
        // Initialize with current date and time
        val today = LocalDate.now()
        val now = LocalTime.now()
        
        _fecha.value = today.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
        _hora.value = now.format(DateTimeFormatter.ofPattern(TIME_FORMAT))
        
        // Observe form changes to validate
        observeFormChanges()
    }
    
    /**
     * Sets the visit ID and client information
     */
    fun setVisitData(visitaId: String, clienteId: String, clienteNombre: String) {
        _visitaId = visitaId
        _clienteId.value = clienteId
        _clienteNombre.value = clienteNombre
        validateForm()
    }
    
    /**
     * Sets the date
     */
    fun setFecha(fecha: String) {
        _fecha.value = fecha
        validateForm()
    }
    
    /**
     * Sets the date from LocalDate
     */
    fun setFechaFromDate(date: LocalDate) {
        val formattedDate = date.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
        _fecha.value = formattedDate
        validateForm()
    }
    
    /**
     * Sets the time
     */
    fun setHora(hora: String) {
        _hora.value = hora
        validateForm()
    }
    
    /**
     * Sets the time from LocalTime
     */
    fun setHoraFromTime(time: LocalTime) {
        val formattedTime = time.format(DateTimeFormatter.ofPattern(TIME_FORMAT))
        _hora.value = formattedTime
        validateForm()
    }
    
    /**
     * Sets the notes
     */
    fun setNovedades(novedades: String) {
        if (novedades.length <= MAX_NOTES_LENGTH) {
            _novedades.value = novedades
        }
    }
    
    /**
     * Sets the order generated flag
     */
    fun setPedidoGenerado(pedidoGenerado: Boolean) {
        _pedidoGenerado.value = pedidoGenerado
    }
    
    /**
     * Uploads evidence (if provided) and then records the visit using viewModelScope
     */
    fun uploadEvidenceAndRecord(
        context: Context,
        visitaId: String,
        vendedorId: String,
        token: String,
        evidenceUri: Uri?,
        evidenceComments: String
    ) {
        if (!isFormValid.value!!) return
        viewModelScope.launch {
            try {
                _isLoading.value = true
                var ok = true
                if (evidenceUri != null || evidenceComments.isNotBlank()) {
                    ok = repository.uploadEvidence(
                        context = context,
                        token = token,
                        visitaId = visitaId,
                        vendedorId = vendedorId,
                        comentarios = evidenceComments,
                        fileUri = evidenceUri ?: Uri.EMPTY
                    )
                }
                if (ok) {
                    recordVisit()
                } else {
                    _error.value = ERROR_RECORDING_VISIT
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en uploadEvidenceAndRecord", e)
                _error.value = ERROR_NETWORK_CONNECTION
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Records the visit
     */
    fun recordVisit() {
        if (!isFormValid.value!!) {
            return
        }
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val request = VisitRecordRequest(
                    fechaRealizada = formatDateForAPI(_fecha.value!!),
                    horaRealizada = formatTimeForAPI(_hora.value!!),
                    clienteId = _clienteId.value!!,
                    novedades = _novedades.value ?: "",
                    pedidoGenerado = _pedidoGenerado.value!!
                )
                
                // Obtener token de autenticación
                val token = SessionManager.getToken(getApplication()) ?: ""
                val response = repository.recordVisit(token, _visitaId, request)
                
                if (response != null) {
                    _success.value = true
                    Log.d(TAG, "Visita registrada exitosamente: ${response.message}")
                } else {
                    _error.value = ERROR_RECORDING_VISIT
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error al registrar visita", e)
                _error.value = ERROR_NETWORK_CONNECTION
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Clears the form
     */
    fun clearForm() {
        _fecha.value = ""
        _hora.value = ""
        _novedades.value = ""
        _pedidoGenerado.value = false
        _success.value = false
        _error.value = null
    }
    
    /**
     * Clears the form and resets to default values
     */
    fun clearFormAndReset() {
        val today = LocalDate.now()
        val now = LocalTime.now()
        
        _fecha.value = today.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
        _hora.value = now.format(DateTimeFormatter.ofPattern(TIME_FORMAT))
        _novedades.value = ""
        _pedidoGenerado.value = false
        _success.value = false
        _error.value = null
    }
    
    /**
     * Clears the error
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Validates the form
     */
    private fun validateForm() {
        val fechaValid = validateFecha(_fecha.value ?: "")
        val horaValid = validateHora(_hora.value ?: "")
        val clienteValid = _clienteId.value?.isNotEmpty() == true
        
        _isFormValid.value = fechaValid && horaValid && clienteValid
    }
    
    /**
     * Validates date format and constraints
     */
    fun validateFecha(fecha: String): Boolean {
        if (fecha.isEmpty()) return false
        
        return try {
            val parsedDate = LocalDate.parse(fecha, DateTimeFormatter.ofPattern(DATE_FORMAT))
            val today = LocalDate.now()
            parsedDate.isBefore(today) || parsedDate.isEqual(today)
        } catch (e: DateTimeParseException) {
            false
        }
    }
    
    /**
     * Validates time format
     */
    fun validateHora(hora: String): Boolean {
        if (hora.isEmpty()) return false
        
        return try {
            LocalTime.parse(hora, DateTimeFormatter.ofPattern(TIME_FORMAT))
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }
    
    /**
     * Gets validation error message for date
     */
    fun getFechaErrorMessage(fecha: String): String? {
        return getFechaErrorMessageStatic(fecha)
    }
    
    
    /**
     * Gets validation error message for time
     */
    fun getHoraErrorMessage(hora: String): String? {
        if (hora.isEmpty()) return ERROR_TIME_REQUIRED
        
        return try {
            LocalTime.parse(hora, DateTimeFormatter.ofPattern(TIME_FORMAT))
            null
        } catch (e: DateTimeParseException) {
            ERROR_TIME_FORMAT
        }
    }
    
    /**
     * Gets validation error message for notes
     */
    fun getNovedadesErrorMessage(novedades: String): String? {
        return if (novedades.length > MAX_NOTES_LENGTH) {
            ERROR_NOTES_MAX_LENGTH
        } else null
    }
    
    /**
     * Observes form changes to validate
     */
    private fun observeFormChanges() {
        // This will be called whenever any form field changes
        // The validation is triggered in the setter methods
    }
    
    /**
     * Formats date for API (YYYY-MM-DD)
     */
    private fun formatDateForAPI(fecha: String): String {
        val parsedDate = LocalDate.parse(fecha, DateTimeFormatter.ofPattern(DATE_FORMAT))
        return parsedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }
    
    /**
     * Formats time for API (HH:mm:ss)
     */
    private fun formatTimeForAPI(hora: String): String {
        val parsedTime = LocalTime.parse(hora, DateTimeFormatter.ofPattern(TIME_FORMAT))
        return parsedTime.format(DateTimeFormatter.ISO_LOCAL_TIME)
    }
}
