package com.medisupplyg4.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.medisupplyg4.base.BaseActivity
import com.medisupplyg4.models.Language
import com.medisupplyg4.ui.screens.StartupScreen
import com.medisupplyg4.ui.screens.WorkingRoutesScreen
import com.medisupplyg4.viewmodels.StartupViewModel

@Composable
fun MediSupplyNavigation(
    navController: NavHostController,
    startupViewModel: StartupViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedLanguage by startupViewModel.selectedLanguage.observeAsState(Language.SPANISH)
    
    NavHost(
        navController = navController,
        startDestination = "startup",
        modifier = modifier
    ) {
        composable("startup") {
            StartupScreen(
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { language: Language ->
                    startupViewModel.selectLanguage(language)
                    if (context is BaseActivity) {
                        startupViewModel.applyLanguageChange(context, language)
                    }
                },
                onContinue = {
                    startupViewModel.markAsCompleted()
                    navController.navigate("routes") {
                        popUpTo("startup") { inclusive = true }
                    }
                }
            )
        }
        
        composable("routes") {
            WorkingRoutesScreen()
        }
    }
}
