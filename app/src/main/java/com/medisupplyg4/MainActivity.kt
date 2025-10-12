package com.medisupplyg4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.medisupplyg4.base.BaseActivity
import com.medisupplyg4.navigation.MediSupplyNavigation
import com.medisupplyg4.ui.theme.MedisupplyG4Theme
import com.medisupplyg4.viewmodels.StartupViewModel

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedisupplyG4Theme {
                MediSupplyApp()
            }
        }
    }
}

@Composable
fun MediSupplyApp() {
    val navController = rememberNavController()
    val startupViewModel: StartupViewModel = viewModel()
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // El bottom navigation se maneja dentro de cada pantalla
        }
    ) { innerPadding ->
        MediSupplyNavigation(
            navController = navController,
            startupViewModel = startupViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}