package com.example.inventario.screens.history

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.inventario.model.Producto
import com.example.inventario.utils.ConexionNube
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Estados para la lista y la carga
    var listaProductos by remember { mutableStateOf(emptyList<Producto>()) }
    var cargando by remember { mutableStateOf(true) }

    // Estados para el diálogo de edición
    var mostrarDialogo by remember { mutableStateOf(false) }
    var productoAEditar by remember { mutableStateOf<Producto?>(null) }
    var actualizando by remember { mutableStateOf(false) }

    // Función para refrescar la lista desde la nube
    fun cargarDatos() {
        cargando = true
        coroutineScope.launch {
            listaProductos = ConexionNube.obtenerHistorial()
            cargando = false
        }
    }

    // Carga inicial al abrir la pantalla
    LaunchedEffect(Unit) {
        cargarDatos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial y Actualización") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (cargando) {
                // Aquí usamos el indicador por defecto (grande y centrado)
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(listaProductos) { producto ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(2.dp),
                            onClick = {
                                productoAEditar = producto
                                mostrarDialogo = true
                            }
                        ) {
                            ListItem(
                                headlineContent = { Text(producto.nombreProducto, fontWeight = FontWeight.Bold) },
                                supportingContent = { Text("QR: ${producto.idQr} | Modelo: ${producto.modelo}") },
                                trailingContent = {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                colors = ListItemDefaults.colors(
                                    containerColor = if (producto.funciona) Color.Transparent else Color(0xFFFFEBEE)
                                )
                            )
                        }
                    }
                }
            }
        }

        // --- CUADRO DE DIÁLOGO PARA EDITAR ---
        if (mostrarDialogo && productoAEditar != null) {
            var nombreTemp by remember { mutableStateOf(productoAEditar!!.nombreProducto) }
            var descTemp by remember { mutableStateOf(productoAEditar!!.descripcion) }
            var funcionaTemp by remember { mutableStateOf(productoAEditar!!.funciona) }

            AlertDialog(
                onDismissRequest = { if (!actualizando) mostrarDialogo = false },
                title = { Text("Editar Estado del Equipo") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("ID QR: ${productoAEditar!!.idQr}", fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = nombreTemp,
                            onValueChange = { nombreTemp = it },
                            label = { Text("Nombre del Producto") }
                        )
                        OutlinedTextField(
                            value = descTemp,
                            onValueChange = { descTemp = it },
                            label = { Text("Notas/Observaciones") }
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = funcionaTemp, onCheckedChange = { funcionaTemp = it })
                            Text("¿El equipo está operativo?")
                        }
                    }
                },
                confirmButton = {
                    Button(
                        enabled = !actualizando,
                        onClick = {
                            actualizando = true
                            coroutineScope.launch {
                                val exito = ConexionNube.actualizarProducto(
                                    productoAEditar!!.copy(
                                        nombreProducto = nombreTemp,
                                        descripcion = descTemp,
                                        funciona = funcionaTemp
                                    )
                                )
                                if (exito) {
                                    Toast.makeText(context, "¡Actualizado!", Toast.LENGTH_SHORT).show()
                                    cargarDatos() // Refrescamos la lista
                                    mostrarDialogo = false
                                } else {
                                    Toast.makeText(context, "Error al conectar", Toast.LENGTH_SHORT).show()
                                }
                                actualizando = false
                            }
                        }
                    ) {
                        // AQUÍ ESTÁ LA CORRECCIÓN EXACTA DE LA RUEDITA
                        if (actualizando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Guardar Cambios")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogo = false }, enabled = !actualizando) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}