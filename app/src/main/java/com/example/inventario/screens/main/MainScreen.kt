package com.example.inventario.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToRead: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Control de Inventario", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(50.dp))

        // PARTE 1
        Button(onClick = onNavigateToAdd, modifier = Modifier.fillMaxWidth().height(65.dp)) {
            Text("1. Nuevo Ingreso (Escanear)", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // PARTE 2
        Button(onClick = onNavigateToRead, modifier = Modifier.fillMaxWidth().height(65.dp)) {
            Text("2. Consultar Producto", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // PARTE 3
        Button(onClick = onNavigateToHistory, modifier = Modifier.fillMaxWidth().height(65.dp)) {
            Text("3. Historial de Registros", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // PARTE 4
        OutlinedButton(onClick = onNavigateToAbout, modifier = Modifier.fillMaxWidth().height(65.dp)) {
            Text("4. Acerca del Sistema...", fontSize = 16.sp)
        }
    }
}