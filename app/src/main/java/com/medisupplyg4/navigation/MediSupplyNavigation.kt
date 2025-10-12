package com.medisupplyg4.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.medisupplyg4.base.BaseActivity
import com.medisupplyg4.models.Language
import com.medisupplyg4.models.UserRole
import com.medisupplyg4.R
import com.medisupplyg4.ui.screens.StartupScreen
import com.medisupplyg4.ui.screens.WorkingRoutesScreen
import com.medisupplyg4.viewmodels.DeliveryRouteViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.medisupplyg4.viewmodels.StartupViewModel

@Composable
fun MediSupplyNavigation(
    navController: NavHostController,
    startupViewModel: StartupViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedLanguage by startupViewModel.selectedLanguage.observeAsState(Language.SPANISH)
    val snackbarHostState = remember { SnackbarHostState() }
    var showNotImplementedSnackbar by remember { mutableStateOf(false) }
    
    NavHost(
        navController = navController,
        startDestination = "startup",
        modifier = modifier
    ) {
        composable("startup") {
            // Mostrar snackbar cuando se selecciona un rol no implementado
            LaunchedEffect(showNotImplementedSnackbar) {
                if (showNotImplementedSnackbar) {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.role_not_implemented)
                    )
                    showNotImplementedSnackbar = false
                }
            }
            
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                modifier = Modifier.fillMaxSize()
            ) { paddingValues ->
                StartupScreen(
                    selectedLanguage = selectedLanguage,
                    onLanguageSelected = { language: Language ->
                        startupViewModel.selectLanguage(language)
                        if (context is BaseActivity) {
                            startupViewModel.applyLanguageChange(context, language)
                        }
                    },
                    onRoleSelected = { role: UserRole ->
                        when (role) {
                            UserRole.DRIVER -> {
                                startupViewModel.selectRole(role)
                                startupViewModel.markAsCompleted()
                                navController.navigate("routes")
                            }
                            UserRole.CLIENT, UserRole.SELLER -> {
                                // Activar snackbar para roles no implementados
                                showNotImplementedSnackbar = true
                            }
                        }
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
        
        composable("routes") {
            val deliveryRouteViewModel: DeliveryRouteViewModel = viewModel()
            WorkingRoutesScreen(
                viewModel = deliveryRouteViewModel,
                navController = navController
            )
        }
    }
}
