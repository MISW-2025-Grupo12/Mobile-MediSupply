package com.medisupplyg4.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.medisupplyg4.R
import com.medisupplyg4.models.UserRole
import com.medisupplyg4.viewmodels.ClientRegistrationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController = rememberNavController(),
    onRegisterSuccess: (UserRole) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: ClientRegistrationViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var identification by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf(UserRole.CLIENT) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var userTypeExpanded by remember { mutableStateOf(false) }
    var showNotImplementedMessage by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }

    // Observar el estado del ViewModel
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()
    val registrationResult by viewModel.registrationResult.observeAsState()

    val userTypes = listOf(UserRole.CLIENT, UserRole.SELLER, UserRole.DRIVER)

    // Strings para validación
    val emailErrorText = stringResource(R.string.error_email_required)
    val roleNotImplementedText = stringResource(R.string.role_not_implemented)

    // Manejar resultado del registro
    LaunchedEffect(registrationResult) {
        registrationResult?.let { result ->
            if (result.isSuccess) {
                onRegisterSuccess(userType)
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

    // Mostrar mensaje de no implementado
    LaunchedEffect(showNotImplementedMessage) {
        if (showNotImplementedMessage) {
            snackbarHostState.showSnackbar(roleNotImplementedText)
            showNotImplementedMessage = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
        // Top bar with back button and title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(R.string.register),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Form fields
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.name)) },
                placeholder = { Text(stringResource(R.string.name_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { newValue ->
                    email = newValue
                    // Validar email cuando el usuario termine de escribir
                    if (newValue.isNotBlank()) {
                        emailError = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newValue).matches()) {
                            emailErrorText
                        } else {
                            ""
                        }
                    } else {
                        emailError = ""
                    }
                },
                label = { Text(stringResource(R.string.email)) },
                placeholder = { Text(stringResource(R.string.email_hint)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = emailError.isNotEmpty(),
                supportingText = if (emailError.isNotEmpty()) {
                    { Text(emailError, color = MaterialTheme.colorScheme.error) }
                } else null
            )

            // Identification field
            OutlinedTextField(
                value = identification,
                onValueChange = { newValue ->
                    // Solo permitir números y limitar longitud
                    if (newValue.all { it.isDigit() } && newValue.length <= 15) {
                        identification = newValue
                    }
                },
                label = { Text(stringResource(R.string.identification)) },
                placeholder = { Text(stringResource(R.string.identification_hint)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = identification.isNotBlank() && (!identification.all { it.isDigit() } || identification.length < 6)
            )

            // Phone field
            OutlinedTextField(
                value = phone,
                onValueChange = { newValue ->
                    // Solo permitir números y limitar longitud
                    if (newValue.all { it.isDigit() } && newValue.length <= 15) {
                        phone = newValue
                    }
                },
                label = { Text(stringResource(R.string.phone)) },
                placeholder = { Text(stringResource(R.string.phone_hint)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = phone.isNotBlank() && (!phone.all { it.isDigit() } || phone.length < 7)
            )

            // Address field - only show for CLIENT and SELLER
            if (userType == UserRole.CLIENT || userType == UserRole.SELLER) {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(stringResource(R.string.address)) },
                    placeholder = { Text(stringResource(R.string.address_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // User type dropdown
            ExposedDropdownMenuBox(
                expanded = userTypeExpanded,
                onExpandedChange = { userTypeExpanded = !userTypeExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = when (userType) {
                        UserRole.CLIENT -> stringResource(R.string.client)
                        UserRole.SELLER -> stringResource(R.string.seller)
                        UserRole.DRIVER -> stringResource(R.string.driver)
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.user_type)) },
                    placeholder = { Text(stringResource(R.string.user_type_hint)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = userTypeExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = userTypeExpanded,
                    onDismissRequest = { userTypeExpanded = false }
                ) {
                    userTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { 
                                Text(when (type) {
                                    UserRole.CLIENT -> stringResource(R.string.client)
                                    UserRole.SELLER -> stringResource(R.string.seller)
                                    UserRole.DRIVER -> stringResource(R.string.driver)
                                })
                            },
                            onClick = {
                                userType = type
                                userTypeExpanded = false
                                // Clear address when switching to driver
                                if (type == UserRole.DRIVER) {
                                    address = ""
                                }
                            }
                        )
                    }
                }
            }

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

            // Confirm password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text(stringResource(R.string.confirm_password)) },
                placeholder = { Text(stringResource(R.string.confirm_password_hint)) },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = confirmPassword.isNotBlank() && password != confirmPassword,
                supportingText = if (confirmPassword.isNotBlank() && password != confirmPassword) {
                    { Text(stringResource(R.string.error_passwords_no_match), color = MaterialTheme.colorScheme.error) }
                } else null
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Register button
        Button(
            onClick = {
                if (userType == UserRole.CLIENT) {
                    // Solo registrar clientes por ahora
                    viewModel.registerClient(
                        name = name,
                        email = email,
                        identification = identification,
                        phone = phone,
                        address = address,
                        password = password,
                        confirmPassword = confirmPassword
                    )
                } else {
                    // Para otros tipos de usuario, mostrar mensaje de no implementado
                    showNotImplementedMessage = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !isLoading && 
                     name.isNotBlank() && 
                     email.isNotBlank() && 
                     emailError.isEmpty() &&
                     identification.isNotBlank() && 
                     identification.all { it.isDigit() } &&
                     identification.length >= 6 &&
                     phone.isNotBlank() && 
                     phone.all { it.isDigit() } &&
                     phone.length >= 7 &&
                     password.isNotBlank() && 
                     password.length >= 6 &&
                     confirmPassword.isNotBlank() &&
                     password == confirmPassword &&
                     (userType == UserRole.DRIVER || address.isNotBlank())
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = stringResource(R.string.register_button),
                    fontSize = 16.sp
                )
            }
        }
        }
    }
}
