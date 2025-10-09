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
import androidx.navigation.compose.rememberNavController
import com.medisupplyg4.navigation.MediSupplyNavigation
import com.medisupplyg4.ui.theme.MedisupplyG4Theme

class MainActivity : ComponentActivity() {
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
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // El bottom navigation se maneja dentro de cada pantalla
        }
    ) { innerPadding ->
        MediSupplyNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}