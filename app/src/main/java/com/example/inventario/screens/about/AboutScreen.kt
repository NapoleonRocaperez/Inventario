package com.example.inventario.screens.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acerca de") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Espacio para el LOGO de la Institución
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Info, contentDescription = "Logo", modifier = Modifier.size(60.dp), tint = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Desarrollado por:", fontSize = 18.sp, color = Color.Gray)
            Text("Ingeniero Napoleón Roca", fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("Docente de la técnica en sistemas", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(24.dp))

            Text("Estudiante:", fontSize = 18.sp, color = Color.Gray)
            Text("Sebastián Daniel Roca", fontSize = 22.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(40.dp))

            Text("Fecha de desarrollo:", fontSize = 14.sp, color = Color.Gray)
            Text("26 de abril de 2026", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}