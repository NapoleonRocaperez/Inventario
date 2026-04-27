package com.example.inventario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.inventario.navigation.AppNavGraph
import com.example.inventario.ui.theme.InventarioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilita el diseño de pantalla completa (borde a borde)
        enableEdgeToEdge()

        setContent {
            // Envuelve toda la aplicación en el tema de colores y bordes redondeados
            InventarioTheme {
                // Surface actúa como el "lienzo" de fondo de la app
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // AQUÍ ESTÁ LA MAGIA: Llamamos a nuestro mapa de navegación
                    AppNavGraph()
                }
            }
        }
    }
}