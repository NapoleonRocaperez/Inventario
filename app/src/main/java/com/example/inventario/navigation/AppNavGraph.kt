package com.example.inventario.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.inventario.screens.about.AboutScreen
import com.example.inventario.screens.add.AddProductScreen
import com.example.inventario.screens.history.HistoryScreen
import com.example.inventario.screens.login.LoginScreen
import com.example.inventario.screens.main.MainScreen
import com.example.inventario.screens.read.ReadProductScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    // Este es el mapa oficial de navegación de la aplicación
    NavHost(navController = navController, startDestination = "login") {

        // 1. Pantalla de Acceso con Google
        composable("login") {
            LoginScreen(
                // 🔥 EL TRUCO ESTÁ AQUÍ: Agregamos ", correo" para recibir los 2 datos 🔥
                onLoginSuccess = { nombreUsuario, correo ->

                    // Al entrar con éxito, vamos al menú principal y borramos el Login del historial
                    navController.navigate("main/$nombreUsuario") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // 2. Menú Principal (Recibe el nombre del operario)
        composable("main/{nombreUsuario}") { backStackEntry ->
            val nombreUsuario = backStackEntry.arguments?.getString("nombreUsuario") ?: "Usuario"
            MainScreen(
                onNavigateToAdd = { navController.navigate("add_product/$nombreUsuario") },
                onNavigateToRead = { navController.navigate("read_product") },
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToAbout = { navController.navigate("about") }
            )
        }

        // 3. Formulario para Registrar Producto (Recibe el nombre para el Excel)
        composable("add_product/{nombreUsuario}") { backStackEntry ->
            val nombreUsuario = backStackEntry.arguments?.getString("nombreUsuario") ?: "Usuario"
            AddProductScreen(
                nombreUsuario = nombreUsuario,
                onBack = { navController.popBackStack() }
            )
        }

        // 4. Consultar Producto (La cámara que busca en el Sheet)
        composable("read_product") {
            ReadProductScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // 5. Historial y Actualización (La lista con el lápiz para editar)
        composable("history") {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // 6. Acerca de (Créditos del equipo)
        composable("about") {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}