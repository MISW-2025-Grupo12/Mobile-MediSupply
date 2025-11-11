package com.medisupplyg4.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.medisupplyg4.R
import com.medisupplyg4.config.ApiConfig
import com.medisupplyg4.models.UserRole
import com.medisupplyg4.network.NetworkClient
import com.medisupplyg4.utils.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController = rememberNavController()
) {
    val context = LocalContext.current
    
    // Obtener tipo de usuario y ID
    val userRoleString = SessionManager.getUserRole(context) ?: ""
    val userId = SessionManager.getUserId(context)
    val token = SessionManager.getToken(context)
    
    // Obtener string de error de carga de perfil
    val errorLoadProfileString = stringResource(R.string.error_load_profile)
    val errorConnectionString = stringResource(R.string.error_connection_error)
    
    // Convertir el string del rol al enum UserRole
    val userRole = remember(userRoleString) {
        when (userRoleString.uppercase()) {
            "CLIENTE" -> UserRole.CLIENT
            "VENDEDOR" -> UserRole.SELLER
            "REPARTIDOR" -> UserRole.DRIVER
            else -> UserRole.CLIENT // Default
        }
    }
    
    // Estados para los datos del perfil
    var userName by remember { mutableStateOf(SessionManager.getUserName(context) ?: "") }
    var userEmail by remember { mutableStateOf(SessionManager.getUserEmail(context) ?: "") }
    var userAddress by remember { mutableStateOf(SessionManager.getUserAddress(context) ?: "") }
    var userPhone by remember { mutableStateOf(SessionManager.getUserPhone(context) ?: "") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Obtener datos del perfil desde el backend según el tipo de usuario
    LaunchedEffect(userRoleString, userId, token) {
        if (userId != null && token != null) {
            isLoading = true
            errorMessage = null
            
            try {
                val authToken = "Bearer $token"
                
                when (userRoleString.uppercase()) {
                    "VENDEDOR" -> {
                        Log.d("ProfileScreen", "Obteniendo perfil de vendedor: $userId")
                        val response = NetworkClient.vendedorApiService.getVendedorById(
                            token = authToken,
                            vendedorId = userId
                        )
                        if (response.isSuccessful) {
                            val seller = response.body()
                            seller?.let {
                                userName = it.nombre
                                userEmail = it.email
                                userAddress = it.direccion
                                userPhone = it.telefono
                                Log.d("ProfileScreen", "Perfil de vendedor cargado: ${it.nombre}")
                            }
                        } else {
                            Log.w("ProfileScreen", "Error al obtener perfil de vendedor: ${response.code()}")
                            errorMessage = errorLoadProfileString
                        }
                    }
                    "CLIENTE" -> {
                        Log.d("ProfileScreen", "Obteniendo perfil de cliente: $userId")
                        val response = NetworkClient.clientesApiService.getClienteById(
                            token = authToken,
                            clienteId = userId
                        )
                        if (response.isSuccessful) {
                            val cliente = response.body()
                            cliente?.let {
                                userName = it.nombre
                                userEmail = it.email
                                userAddress = it.direccion
                                userPhone = it.telefono
                                Log.d("ProfileScreen", "Perfil de cliente cargado: ${it.nombre}")
                            }
                        } else {
                            Log.w("ProfileScreen", "Error al obtener perfil de cliente: ${response.code()}")
                            errorMessage = errorLoadProfileString
                        }
                    }
                    "REPARTIDOR" -> {
                        // Para repartidor, usar los datos del login (ya están en SessionManager)
                        Log.d("ProfileScreen", "Usando datos del login para repartidor")
                        // Los valores ya están inicializados desde SessionManager
                    }
                    else -> {
                        Log.w("ProfileScreen", "Tipo de usuario desconocido: $userRole")
                        // Usar datos del login como fallback
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileScreen", "Excepción al obtener perfil", e)
                errorMessage = errorConnectionString.format(e.message ?: "")
                // En caso de error, mantener los datos del login
            } finally {
                isLoading = false
            }
        }
    }
    
    // App version
    val versionName = remember {
        try {
            @Suppress("DEPRECATION")
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "-.-.-"
        } catch (e: Exception) {
            "-.-.-" // Fallback version
        }
    }
    
    // Backend version - null = cargando, "NOT_AVAILABLE" = no disponible, otro valor = versión
    var backendVersion by remember { mutableStateOf<String?>(null) }
    
    // Obtener versión del backend al cargar la pantalla
    LaunchedEffect(Unit) {
        try {
            val currentEnvironment = ApiConfig.getCurrentEnvironment()
            Log.d("ProfileScreen", "Obteniendo versión del backend desde ambiente: ${currentEnvironment.name}...")
            Log.d("ProfileScreen", "URL base: ${ApiConfig.CLIENT_REGISTRATION_BASE_URL}")
            val response = NetworkClient.loginApiService.getVersion()
            Log.d("ProfileScreen", "Respuesta del backend - código: ${response.code()}, exitosa: ${response.isSuccessful}")
            if (response.isSuccessful) {
                val versionResponse = response.body()
                Log.d("ProfileScreen", "Versión del backend recibida: ${versionResponse?.version}")
                backendVersion = versionResponse?.version
            } else {
                Log.w("ProfileScreen", "Error al obtener versión del backend: código ${response.code()}, mensaje: ${response.message()}")
                val errorBody = response.errorBody()?.string()
                Log.w("ProfileScreen", "Cuerpo del error: $errorBody")
                backendVersion = "NOT_AVAILABLE"
            }
        } catch (e: Exception) {
            Log.e("ProfileScreen", "Excepción al obtener versión del backend", e)
            Log.e("ProfileScreen", "Mensaje de excepción: ${e.message}")
            backendVersion = "NOT_AVAILABLE"
        }
    }
    
    // Función para cerrar sesión
    val onLogout = {
        SessionManager.clearSession(context)
        // Navegar a login y limpiar el back stack
        navController.navigate("login") {
            popUpTo("login") { inclusive = true }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Icono de perfil grande y centrado
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(120.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(120.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Nombre centrado y grande
        Text(
            text = if (isLoading) {
                stringResource(R.string.loading)
            } else {
                userName.ifEmpty { stringResource(R.string.not_available) }
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Rol del usuario (debajo del nombre)
        if (!isLoading) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(userRole.titleResId),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Mostrar mensaje de error si existe
        if (errorMessage != null && !isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Información del usuario (solo mostrar si no está cargando)
        if (!isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Correo
                if (userEmail.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.email),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = userEmail,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                
                // Dirección (solo si está disponible)
                if (!userAddress.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.address),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = userAddress,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                
                // Teléfono (solo si está disponible)
                if (!userPhone.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.phone),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = userPhone,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Versiones (en la parte inferior)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_version, versionName),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = if (backendVersion == null) {
                    "" // Aún cargando, no mostrar nada
                } else if (backendVersion == "NOT_AVAILABLE") {
                    stringResource(R.string.backend_version_not_available)
                } else {
                    stringResource(R.string.backend_version, backendVersion!!)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Botón de cerrar sesión
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(
                text = stringResource(R.string.logout),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}