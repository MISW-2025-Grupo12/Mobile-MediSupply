package com.medisupplyg4.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medisupplyg4.R
import com.medisupplyg4.models.Language

@Composable
fun CompactLanguageSelector(
    selectedLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
    ) {
        // Selector compacto
        Card(
            modifier = Modifier
                .clickable { expanded = true },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Bandera pequeña
                Image(
                    painter = painterResource(id = selectedLanguage.flagResId),
                    contentDescription = stringResource(selectedLanguage.countryResId),
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Código del idioma
                Text(
                    text = when (selectedLanguage) {
                        Language.SPANISH -> "ES"
                        Language.ENGLISH -> "EN"
                        else -> "ES"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Flecha pequeña
                Icon(
                    painter = painterResource(id = R.drawable.chevron_forward),
                    contentDescription = stringResource(R.string.startup_expand),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(12.dp)
                        .rotate(if (expanded) 90f else 0f)
                )
            }
        }
        
        // Opciones desplegables
        if (expanded) {
            Card(
                modifier = Modifier
                    .padding(top = 40.dp)
                    .width(80.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(4.dp)
                ) {
                    Language.AVAILABLE_LANGUAGES.forEach { language ->
                        if (language != selectedLanguage) {
                            CompactLanguageOption(
                                language = language,
                                onClick = {
                                    onLanguageSelected(language)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactLanguageOption(
    language: Language,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Bandera pequeña
            Image(
                painter = painterResource(id = language.flagResId),
                contentDescription = stringResource(language.countryResId),
                modifier = Modifier
                    .size(14.dp)
                    .clip(RoundedCornerShape(7.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Código del idioma
            Text(
                text = when (language) {
                    Language.SPANISH -> "ES"
                    Language.ENGLISH -> "EN"
                    else -> "ES"
                },
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
