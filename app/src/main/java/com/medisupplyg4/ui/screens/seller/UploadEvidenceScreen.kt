package com.medisupplyg4.ui.screens.seller

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.medisupplyg4.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadEvidenceScreen(
    navController: NavController
) {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var comments by remember { mutableStateOf("") }

    val pickFile = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        selectedUri = uri
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.upload_evidence)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = stringResource(R.string.evidence_section_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(text = stringResource(R.string.evidence_section_subtitle), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            val borderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)

            // Dashed border box with pure white background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .drawBehind {
                        val stroke = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f), cap = StrokeCap.Round)
                        drawRoundRect(
                            color = borderColor,
                            style = stroke,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())
                        )
                    }
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(imageVector = Icons.Filled.Description, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(40.dp))
                    Text(text = stringResource(R.string.evidence_attach_title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(text = stringResource(R.string.evidence_attach_subtitle), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(onClick = { pickFile.launch(arrayOf("image/*", "video/*")) }, shape = RoundedCornerShape(20.dp)) {
                        Text(stringResource(R.string.select_file))
                    }
                    selectedUri?.let { uri ->
                        Text(uri.lastPathSegment ?: "", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            OutlinedTextField(
                value = comments,
                onValueChange = { comments = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                label = { Text(stringResource(R.string.comments)) },
                placeholder = { Text(stringResource(R.string.visit_record_write_notes)) }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("evidence_uri", selectedUri?.toString())
                    navController.previousBackStackEntry?.savedStateHandle?.set("evidence_comments", comments)
                    navController.popBackStack()
                },
                enabled = selectedUri != null || comments.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}
