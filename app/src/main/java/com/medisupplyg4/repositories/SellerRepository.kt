package com.medisupplyg4.repositories

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.medisupplyg4.R
import com.medisupplyg4.config.ApiConfig
import com.medisupplyg4.models.PaginatedResponse
import com.medisupplyg4.models.SellerAPI
import com.medisupplyg4.models.VisitAPI
import com.medisupplyg4.models.VisitRecordRequest
import com.medisupplyg4.models.VisitRecordResponse
import com.medisupplyg4.models.VisitSuggestionsResponse
import com.medisupplyg4.network.NetworkClient
import com.medisupplyg4.utils.SessionManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.webkit.MimeTypeMap

/**
 * Repository to handle seller and visit data
 */
class SellerRepository {
    
    companion object {
        private const val TAG = "SellerRepository"
        private const val MAX_FILE_SIZE_BYTES = 100L * 1024L * 1024L // 100 MB
    }
    
    private val vendedorApiService = NetworkClient.vendedorApiService
    private val visitasApiService = NetworkClient.visitasApiService
    
    /**
     * Gets the current seller based on the logged-in user information
     * Creates SellerAPI directly from SessionManager data without API call
     */
    suspend fun getCurrentSeller(token: String, context: Context): SellerAPI? {
        return try {
            Log.d(TAG, "Obteniendo vendedor actual desde información de sesión...")
            
            // Obtener información del usuario desde SessionManager
            val userId = SessionManager.getUserId(context)
            val userName = SessionManager.getUserName(context)
            val userEmail = SessionManager.getUserEmail(context)
            val userPhone = SessionManager.getUserPhone(context)
            val userAddress = SessionManager.getUserAddress(context)
            
            if (userId != null && userName != null && userEmail != null) {
                Log.d(TAG, "Información del usuario encontrada:")
                Log.d(TAG, "  ID: $userId")
                Log.d(TAG, "  Nombre: $userName")
                Log.d(TAG, "  Email: $userEmail")
                Log.d(TAG, "  Teléfono: $userPhone")
                Log.d(TAG, "  Dirección: $userAddress")
                
                // Crear SellerAPI directamente desde la información de sesión
                val seller = SellerAPI(
                    id = userId,
                    nombre = userName,
                    email = userEmail,
                    telefono = userPhone ?: "",
                    direccion = userAddress ?: ""
                )
                
                Log.d(TAG, "Vendedor creado desde información de sesión: ${seller.nombre}")
                seller
            } else {
                Log.w(TAG, "Información incompleta del usuario en SessionManager")
                Log.w(TAG, "userId: $userId, userName: $userName, userEmail: $userEmail")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener vendedor desde sesión", e)
            null
        }
    }
    
    /**
     * Gets the scheduled visits of a seller for a date range
     */
    suspend fun getSellerVisits(
        token: String,
        vendedorId: String,
        fechaInicio: LocalDate,
        fechaFin: LocalDate,
        page: Int = 1,
        pageSize: Int = 20
    ): List<VisitAPI> {
        return try {
            val fechaInicioStr = fechaInicio.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val fechaFinStr = fechaFin.format(DateTimeFormatter.ISO_LOCAL_DATE)
            
            Log.d(TAG, "Obteniendo visitas para vendedor $vendedorId desde $fechaInicioStr hasta $fechaFinStr (página $page, tamaño $pageSize)")
            
            val response = visitasApiService.getVisitasVendedor(
                token = "Bearer $token",
                vendedorId = vendedorId,
                fechaInicio = fechaInicioStr,
                fechaFin = fechaFinStr,
                estado = "pendiente",
                page = page,
                pageSize = pageSize
            )
            
            if (response.isSuccessful) {
                val paginatedResponse = response.body()
                val visitas = paginatedResponse?.items ?: emptyList()
                Log.d(TAG, "Visitas recibidas: ${visitas.size} de ${paginatedResponse?.pagination?.totalItems ?: 0}")
                visitas
            } else {
                Log.e(TAG, "Error al obtener visitas: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener visitas", e)
            emptyList()
        }
    }
    
    /**
     * Gets the scheduled visits of a seller for a date range with pagination
     */
    suspend fun getSellerVisitsPaginated(
        token: String,
        vendedorId: String,
        fechaInicio: LocalDate,
        fechaFin: LocalDate,
        page: Int = 1,
        pageSize: Int = 10,
        context: Context
    ): Result<PaginatedResponse<VisitAPI>> {
        return try {
            val fechaInicioStr = fechaInicio.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val fechaFinStr = fechaFin.format(DateTimeFormatter.ISO_LOCAL_DATE)
            
            Log.d(TAG, "Obteniendo visitas paginadas para vendedor $vendedorId desde $fechaInicioStr hasta $fechaFinStr (página $page, tamaño $pageSize)")
            
            val response = visitasApiService.getVisitasVendedor(
                token = "Bearer $token",
                vendedorId = vendedorId,
                fechaInicio = fechaInicioStr,
                fechaFin = fechaFinStr,
                estado = "pendiente",
                page = page,
                pageSize = pageSize
            )
            
            if (response.isSuccessful) {
                val paginatedResponse = response.body()
                if (paginatedResponse != null) {
                    Log.d(TAG, "Visitas paginadas recibidas: ${paginatedResponse.items.size} de ${paginatedResponse.pagination.totalItems}")
                    Result.success(paginatedResponse)
                } else {
                    Log.e(TAG, "Respuesta vacía al obtener visitas paginadas")
                    val errorMessage = context.getString(R.string.error_server_empty_response)
                    Result.failure(Exception(errorMessage))
                }
            } else {
                Log.e(TAG, "Error al obtener visitas paginadas: ${response.code()} - ${response.message()}")
                val errorMessage = context.getString(R.string.error_server_error, response.code(), response.message() ?: "")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener visitas paginadas", e)
            Result.failure(e)
        }
    }

    /**
     * Records a completed visit
     */
    suspend fun recordVisit(
        token: String,
        visitaId: String,
        request: VisitRecordRequest
    ): VisitRecordResponse? {
        return try {
            Log.d(TAG, "Registrando visita $visitaId")
            
            val response = visitasApiService.recordVisit("Bearer $token", visitaId, request)
            
            if (response.isSuccessful) {
                val result = response.body()
                Log.d(TAG, "Visita registrada exitosamente: ${result?.message}")
                result
            } else {
                Log.e(TAG, "Error al registrar visita: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al registrar visita", e)
            null
        }
    }

    /**
     * Sube evidencia de visita
     */
    suspend fun uploadEvidence(
        context: Context,
        token: String,
        visitaId: String,
        vendedorId: String,
        comentarios: String,
        fileUri: Uri
    ): Boolean {
        return try {
            val resolver = context.contentResolver
            val type = resolver.getType(fileUri) ?: "application/octet-stream"
            // Validate allowed types
            val allowed = type.startsWith("image/") || type.startsWith("video/")
            if (!allowed) {
                Log.w(TAG, "Tipo de archivo no permitido: $type")
                return false
            }
            // Validate size
            resolver.openFileDescriptor(fileUri, "r")?.use { pfd ->
                if (pfd.statSize > 0 && pfd.statSize > MAX_FILE_SIZE_BYTES) {
                    Log.w(TAG, "Archivo excede tamaño permitido: ${pfd.statSize}")
                    return false
                }
            }

            // Resolve original display name (with extension) if available
            var displayName: String? = null
            resolver.query(fileUri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx >= 0 && cursor.moveToFirst()) {
                    displayName = cursor.getString(idx)
                }
            }
            // Fallback: build a name with proper extension derived from MIME
            val extFromMime = MimeTypeMap.getSingleton().getExtensionFromMimeType(type)
            val safeFileName = when {
                !displayName.isNullOrBlank() -> displayName!!
                !extFromMime.isNullOrBlank() -> "evidencia.$extFromMime"
                else -> "evidencia.bin"
            }

            // Copy to temp file while preserving extension
            val suffix = if (safeFileName.contains('.')) safeFileName.substringAfterLast('.', missingDelimiterValue = "") else ""
            val tempFile = if (suffix.isNotBlank()) File.createTempFile("evidencia_", ".${suffix}", context.cacheDir) else File.createTempFile("evidencia_", null, context.cacheDir)
            resolver.openInputStream(fileUri)?.use { input ->
                FileOutputStream(tempFile).use { out -> input.copyTo(out) }
            } ?: run {
                Log.e(TAG, "No se pudo abrir InputStream del Uri")
                return false
            }

            val requestFile: RequestBody = tempFile.asRequestBody(type.toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("archivo", safeFileName, requestFile)
            val comentariosBody = comentarios.toRequestBody("text/plain".toMediaTypeOrNull())
            val vendedorBody = vendedorId.toRequestBody("text/plain".toMediaTypeOrNull())

            val resp = visitasApiService.uploadEvidencia(
                token = "Bearer $token",
                visitaId = visitaId,
                archivo = filePart,
                comentarios = comentariosBody,
                vendedorId = vendedorBody
            )
            val ok = resp.isSuccessful
            if (!ok) {
                Log.e(TAG, "Error subiendo evidencia: ${resp.code()} - ${resp.message()}")
            }
            // Cleanup temp file
            tempFile.delete()
            ok
        } catch (e: Exception) {
            Log.e(TAG, "Excepción subiendo evidencia", e)
            false
        }
    }
    
    /**
     * Obtiene las sugerencias de una visita
     */
    suspend fun getVisitSuggestions(token: String, visitaId: String): VisitSuggestionsResponse? {
        return try {
            val response = visitasApiService.getVisitSuggestions(
                token = "Bearer $token",
                visitaId = visitaId
            )
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e(TAG, "Error obteniendo sugerencias: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción obteniendo sugerencias", e)
            null
        }
    }
}
