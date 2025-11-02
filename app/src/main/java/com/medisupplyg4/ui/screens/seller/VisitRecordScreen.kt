package com.medisupplyg4.ui.screens.seller

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.medisupplyg4.R
import com.medisupplyg4.utils.SessionManager
import com.medisupplyg4.viewmodels.VisitRecordViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitRecordScreen(
    navController: NavController,
    visitaId: String,
    clienteId: String,
    clienteNombre: String,
    onVisitRecorded: (() -> Unit)? = null,
    viewModel: VisitRecordViewModel = viewModel()
) {
    val context = LocalContext.current

    // UI State
    val fecha by viewModel.fecha.observeAsState("")
    val hora by viewModel.hora.observeAsState("")
    val clienteNombreState by viewModel.clienteNombre.observeAsState("")
    val novedades by viewModel.novedades.observeAsState("")
    val pedidoGenerado by viewModel.pedidoGenerado.observeAsState(false)
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()
    val success by viewModel.success.observeAsState(false)
    val isFormValid by viewModel.isFormValid.observeAsState(false)
    
    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    // Initialize visit data
    LaunchedEffect(visitaId, clienteId, clienteNombre) {
        viewModel.setVisitData(visitaId, clienteId, clienteNombre)
    }
    
    // Handle success
    LaunchedEffect(success) {
        if (success) {
            onVisitRecorded?.invoke()
        }
    }
    
    // Date picker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = { 
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                            viewModel.setFechaFromDate(selectedDate)
                        }
                        showDatePicker = false 
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(16.dp)
            )
        }
    }
    
    // Time picker
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = { 
                        val selectedTime = java.time.LocalTime.of(timePickerState.hour, timePickerState.minute)
                        viewModel.setHoraFromTime(selectedTime)
                        showTimePicker = false 
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            TimePicker(
                state = timePickerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp)
            )
        }
    }
    
    // Success Modal
    if (success) {
        AlertDialog(
            onDismissRequest = { 
                // No se puede cerrar haciendo click fuera
            },
            title = {
                Text(
                    text = stringResource(R.string.visit_record_success),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.visit_record_success_message),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearFormAndReset()
                        navController.popBackStack()
                    }
                ) {
                    Text(stringResource(R.string.continue_button))
                }
            },
            dismissButton = null
        )
    }
    
    // Listen for evidence result from UploadEvidenceScreen
    val evidenceUriFlow = navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<String?>("evidence_uri", null)
    val evidenceCommentsFlow = navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<String?>("evidence_comments", null)
    val evidenceUriResult by (evidenceUriFlow?.collectAsState(initial = null) ?: mutableStateOf<String?>(null))
    val evidenceCommentsResult by (evidenceCommentsFlow?.collectAsState(initial = null) ?: mutableStateOf<String?>(null))

    LaunchedEffect(evidenceUriResult, evidenceCommentsResult) {
        val uriStr = evidenceUriResult
        val commentsStr = evidenceCommentsResult
        if (uriStr != null || (commentsStr != null && commentsStr.isNotBlank())) {
            val token = SessionManager.getToken(context) ?: ""
            val vendedorId = SessionManager.getUserId(context) ?: ""
            val uri = uriStr?.let { Uri.parse(it) }
            viewModel.uploadEvidenceAndRecord(
                context = context,
                visitaId = visitaId,
                vendedorId = vendedorId,
                token = token,
                evidenceUri = uri,
                evidenceComments = commentsStr ?: ""
            )
            // Clear keys to avoid re-trigger
            navController.currentBackStackEntry?.savedStateHandle?.set("evidence_uri", null)
            navController.currentBackStackEntry?.savedStateHandle?.set("evidence_comments", null)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = stringResource(R.string.visit_record_back)
                )
            }
            Text(
                text = stringResource(R.string.visit_record_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        // Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Date and Time Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Date Field
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDatePicker = true },
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.visit_record_date),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (fecha.isEmpty()) stringResource(R.string.visit_record_select_date) else fecha,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (fecha.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = stringResource(R.string.select_date_description),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        
                        // Date validation error
                        val fechaError = viewModel.getFechaErrorMessage(fecha)
                        if (fechaError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (fechaError) {
                                    VisitRecordViewModel.ERROR_DATE_REQUIRED -> stringResource(R.string.validation_date_required)
                                    VisitRecordViewModel.ERROR_DATE_FORMAT -> stringResource(R.string.validation_date_format)
                                    VisitRecordViewModel.ERROR_FUTURE_DATE -> stringResource(R.string.validation_future_date)
                                    else -> fechaError
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                    
                    // Time Field
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showTimePicker = true },
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.visit_record_time),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (hora.isEmpty()) stringResource(R.string.visit_record_select_time) else hora,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (hora.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(
                                        Icons.Default.Schedule,
                                        contentDescription = stringResource(R.string.select_time_description),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        
                        // Time validation error
                        val horaError = viewModel.getHoraErrorMessage(hora)
                        if (horaError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (horaError) {
                                    VisitRecordViewModel.ERROR_TIME_REQUIRED -> stringResource(R.string.validation_time_required)
                                    VisitRecordViewModel.ERROR_TIME_FORMAT -> stringResource(R.string.validation_time_format)
                                    else -> horaError
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
                
                // Client Field (Read-only)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.visit_record_client),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = clienteNombreState,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                painter = painterResource(R.drawable.chevron_forward),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Notes Field
                Column {
                    OutlinedTextField(
                        value = novedades,
                        onValueChange = { viewModel.setNovedades(it) },
                        label = { Text(stringResource(R.string.visit_record_notes)) },
                        placeholder = { Text(stringResource(R.string.visit_record_write_notes)) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.notes),
                                contentDescription = null
                            )
                        },
                        supportingText = {
                            Text("${novedades.length}/500")
                        },
                        isError = viewModel.getNovedadesErrorMessage(novedades) != null,
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Notes validation error
                    val novedadesError = viewModel.getNovedadesErrorMessage(novedades)
                    if (novedadesError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (novedadesError) {
                                VisitRecordViewModel.ERROR_NOTES_MAX_LENGTH -> stringResource(R.string.validation_notes_max_length)
                                else -> novedadesError
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
                
                // Order Generated Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.visit_record_order_generated),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = pedidoGenerado,
                        onCheckedChange = { viewModel.setPedidoGenerado(it) }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        // Subir evidencia button: navigate to full screen
        OutlinedButton(
            onClick = {
                if (isFormValid && !isLoading && !success) {
                    navController.navigate("upload_evidence")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = isFormValid && !isLoading && !success
        ) {
            Icon(imageVector = Icons.Default.UploadFile, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.upload_evidence))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Save Button
        Button(
            onClick = { viewModel.recordVisit() },
            enabled = isFormValid && !isLoading && !success,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.recording_visit),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                Text(stringResource(R.string.visit_record_save))
            }
        }
        
        
        // Error Message
        error?.let { errorMessage ->
            val localizedErrorMessage = when (errorMessage) {
                VisitRecordViewModel.ERROR_RECORDING_VISIT -> stringResource(R.string.visit_record_error)
                VisitRecordViewModel.ERROR_NETWORK_CONNECTION -> stringResource(R.string.visit_record_network_error)
                VisitRecordViewModel.ERROR_DATE_REQUIRED -> stringResource(R.string.validation_date_required)
                VisitRecordViewModel.ERROR_TIME_REQUIRED -> stringResource(R.string.validation_time_required)
                VisitRecordViewModel.ERROR_CLIENT_REQUIRED -> stringResource(R.string.validation_client_required)
                VisitRecordViewModel.ERROR_DATE_FORMAT -> stringResource(R.string.validation_date_format)
                VisitRecordViewModel.ERROR_TIME_FORMAT -> stringResource(R.string.validation_time_format)
                VisitRecordViewModel.ERROR_FUTURE_DATE -> stringResource(R.string.validation_future_date)
                VisitRecordViewModel.ERROR_NOTES_MAX_LENGTH -> stringResource(R.string.validation_notes_max_length)
                else -> errorMessage
            }
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = localizedErrorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = content
    )
}