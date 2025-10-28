package com.medisupplyg4.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import com.medisupplyg4.models.UserRole
import com.medisupplyg4.ui.screens.HomeScreen
import com.medisupplyg4.ui.screens.auth.LoginScreen
import com.medisupplyg4.ui.screens.auth.RegisterScreen
import com.medisupplyg4.viewmodels.StartupViewModel

@Composable
fun MediSupplyNavigation(
    navController: NavHostController,
    startupViewModel: StartupViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedLanguage by startupViewModel.selectedLanguage.observeAsState(Language.SPANISH)
    val selectedRole by startupViewModel.selectedRole.observeAsState(UserRole.CLIENT)
    
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = { userRole ->
                    startupViewModel.selectRole(userRole)
                    navController.navigate("home")
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { language: Language ->
                    startupViewModel.selectLanguage(language)
                    if (context is BaseActivity) {
                        startupViewModel.applyLanguageChange(context, language)
                    }
                }
            )
        }
        
        composable("register") {
            RegisterScreen(
                navController = navController,
                onRegisterSuccess = { userRole ->
                    startupViewModel.selectRole(userRole)
                    navController.navigate("login")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("home") {
            HomeScreen(
                userRole = selectedRole ?: UserRole.CLIENT,
                navController = navController
            )
        }
    }
}