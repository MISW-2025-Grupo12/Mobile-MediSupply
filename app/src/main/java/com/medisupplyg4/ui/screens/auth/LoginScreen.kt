package com.medisupplyg4.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.medisupplyg4.R
import com.medisupplyg4.models.Language
import com.medisupplyg4.models.UserRole
import com.medisupplyg4.ui.components.CompactLanguageSelector
import com.medisupplyg4.utils.SessionManager
import com.medisupplyg4.viewmodels.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController = rememberNavController(),
    onLoginSuccess: (UserRole) -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    selectedLanguage: Language = Language.SPANISH,
    onLanguageSelected: (Language) -> Unit = {},
    viewModel: LoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Observar el estado del ViewModel
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()
    val loginResult by viewModel.loginResult.observeAsState()
    val context = LocalContext.current

    // Manejar resultado del login
    LaunchedEffect(loginResult) {
        loginResult?.let { result ->
            if (result.isSuccess) {
                val loginResponse = result.getOrNull()
                val userRole = viewModel.getUserRoleFromTipoUsuario(loginResponse?.user_info?.tipo_usuario ?: "CLIENTE")
                
                // Guardar token y datos del usuario
                loginResponse?.let { response ->
                    SessionManager.saveToken(context, response.access_token)
                    SessionManager.saveUserRole(context, response.user_info.tipo_usuario)
                    SessionManager.saveUserEmail(context, response.user_info.email)
                }
                
                onLoginSuccess(userRole)
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar errores en Snackbar
    LaunchedEffect(error) {
        error?.let { errorMessage ->
            snackbarHostState.showSnackbar(errorMessage)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        // Language selector (top right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            CompactLanguageSelector(
                selectedLanguage = selectedLanguage,
                onLanguageSelected = onLanguageSelected
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Welcome message
        Text(
            text = stringResource(R.string.welcome_message),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            placeholder = { Text(stringResource(R.string.email_hint)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            placeholder = { Text(stringResource(R.string.password_hint)) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Login button
        Button(
            onClick = {
                viewModel.login(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = stringResource(R.string.login),
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Register link
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.no_account),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(
                onClick = onNavigateToRegister
            ) {
                Text(
                    text = stringResource(R.string.register_link),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
}
