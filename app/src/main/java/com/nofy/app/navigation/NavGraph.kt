package com.nofy.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nofy.feature.configuracion.ConfiguracionScreen
import com.nofy.feature.gasto.AddGastoScreen
import com.nofy.feature.home.HomeScreen
import com.nofy.feature.plantillas.PlantillasScreen
import com.nofy.feature.registros.RegistrosScreen

@Composable
fun NofyNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToRegistros = { navController.navigate("registros") },
                onNavigateToAddGasto = { navController.navigate("add_gasto") },
                onNavigateToConfiguracion = { navController.navigate("configuracion") },
                onNavigateToPlantillas = { navController.navigate("plantillas") }
            )
        }
        composable("registros") {
            RegistrosScreen()
        }
        composable("add_gasto") {
            AddGastoScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("configuracion") {
            ConfiguracionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("plantillas") {
            PlantillasScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
