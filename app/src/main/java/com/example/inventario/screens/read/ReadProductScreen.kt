package com.example.inventario.screens.read

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inventario.model.Producto
import com.example.inventario.utils.ConexionNube
import com.example.inventario.utils.QRScannerHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadProductScreen(onBack: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var mostrarCamara by remember { mutableStateOf(false) }
    var productoEncontrado by remember { mutableStateOf<Producto?>(null) }
    var buscando by remember { mutableStateOf(false) }
    var errorBusqueda by remember { mutableStateOf(false) }

    val launcherPermisoCamara = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) mostrarCamara = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Consultar Producto") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }
            )
        }
    ) { padding ->
        if (mostrarCamara) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                QRScannerHelper(
                    onQrDetected = { qr ->
                        mostrarCamara = false
                        buscando = true
                        errorBusqueda = false
                        coroutineScope.launch {
                            val resultado = ConexionNube.buscarProducto(qr)
                            productoEncontrado = resultado
                            buscando = false
                            if (resultado == null) errorBusqueda = true
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
                Text("Buscador de Inventario", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { launcherPermisoCamara.launch(Manifest.permission.CAMERA) },
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                ) {
                    Text("ESCANEAR CÓDIGO QR", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (buscando) {
                    CircularProgressIndicator()
                    Text("Conectando con la base de datos...")
                }

                if (errorBusqueda) {
                    Text("❌ Producto no encontrado en el sistema", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }

                productoEncontrado?.let { prod ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("DETALLES DEL PRODUCTO", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                            Divider()

                            DatoFila("Nombre:", prod.nombreProducto)
                            DatoFila("Modelo:", prod.modelo)
                            DatoFila("ID QR:", prod.idQr)
                            DatoFila("Estado:", if (prod.funciona) "✅ Funciona" else "⚠️ Requiere reparación")
                            DatoFila("Garantía:", prod.garantiaMeses)

                            Divider()
                            Text("Descripción:", fontWeight = FontWeight.Bold)
                            Text(prod.descripcion, fontSize = 15.sp)

                            Divider()
                            Text("Registrado por: ${prod.nombreUsuario}", fontSize = 12.sp, color = Color.Gray)
                            Text("Fecha: ${prod.fechaEscaneo}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DatoFila(label: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text(valor, modifier = Modifier.weight(2f))
    }
}