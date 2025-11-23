package com.medisupplyg4.ui.screens.seller

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.medisupplyg4.R
import com.medisupplyg4.models.VisitSuggestionsResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitSuggestionsScreen(
    navController: NavController,
    suggestions: VisitSuggestionsResponse
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.visit_suggestions_title)) },
                navigationIcon = {
                    IconButton(onClick = { 
                        // Pop back to visits_list, removing visit_record from back stack
                        navController.popBackStack("visits_list", inclusive = false)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Scrollable content area
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    FormattedSuggestionsText(
                        text = suggestions.sugerencia.sugerenciasTexto
                    )
                }
            }
            
            // Back button at the bottom
            Button(
                onClick = {
                    // Pop back to visits_list, removing visit_record from back stack
                    navController.popBackStack("visits_list", inclusive = false)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp)
            ) {
                Text(stringResource(R.string.back))
            }
        }
    }
}

@Composable
private fun FormattedSuggestionsText(text: String) {
    val lines = text.split("\n")
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        lines.forEachIndexed { index, line ->
            val trimmedLine = line.trim()
            
            when {
                // Empty line
                trimmedLine.isEmpty() -> {
                    if (index < lines.size - 1) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
                // Headers with ** (bold) - full line
                trimmedLine.startsWith("**") && trimmedLine.endsWith("**") && trimmedLine.count { it == '*' } == 4 -> {
                    Text(
                        text = trimmedLine.removePrefix("**").removeSuffix("**"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                // Bullet points with single asterisk at start (after trimming)
                trimmedLine.startsWith("* ") && !trimmedLine.startsWith("**") -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "• ",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        FormattedLineText(
                            text = trimmedLine.removePrefix("* ").trim(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                // Regular line with potential bold text
                else -> {
                    FormattedLineText(text = trimmedLine)
                }
            }
        }
    }
}

@Composable
private fun FormattedLineText(
    text: String,
    modifier: Modifier = Modifier
) {
    if (text.isEmpty()) {
        return
    }
    
    val annotatedString = buildAnnotatedString {
        var currentIndex = 0
        while (currentIndex < text.length) {
            val boldStart = text.indexOf("**", currentIndex)
            if (boldStart == -1) {
                // No more bold markers, add remaining text
                append(text.substring(currentIndex))
                break
            } else {
                // Add text before bold
                append(text.substring(currentIndex, boldStart))
                
                // Find end of bold
                val boldEnd = text.indexOf("**", boldStart + 2)
                if (boldEnd == -1) {
                    // No closing marker, add remaining text (including the **)
                    append(text.substring(boldStart))
                    break
                } else {
                    // Add bold text
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(text.substring(boldStart + 2, boldEnd))
                    }
                    currentIndex = boldEnd + 2
                }
            }
        }
    }
    
    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Start,
        modifier = modifier
    )
}

