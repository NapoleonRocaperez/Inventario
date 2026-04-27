package com.example.inventario.screens.add

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inventario.model.Producto
import com.example.inventario.utils.ConexionNube
import com.example.inventario.utils.QRScannerHelper
import com.example.inventario.utils.WarrantyAlarmReceiver
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(nombreUsuario: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // --- VARIABLES DE MEMORIA ---
    var qrCode by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var funciona by remember { mutableStateOf(true) }
    var fotoBase64 by remember { mutableStateOf("") }

    var mostrarCamara by remember { mutableStateOf(false) }
    var guardando by remember { mutableStateOf(false) } // Controla la rueda de carga

    val fechaEscaneo = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) }
    var expandirSpinner by remember { mutableStateOf(false) }
    val opcionesGarantia = listOf("Sin garantía", "3 meses", "6 meses", "12 meses", "24 meses")
    var garantiaSeleccionada by remember { mutableStateOf(opcionesGarantia[0]) }

    val launcherPermisoCamara = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) mostrarCamara = true
    }

    // Lanzador de la cámara fotográfica
    val launcherFoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            fotoBase64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Producto") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }
            )
        }
    ) { padding ->
        if (mostrarCamara) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                QRScannerHelper(onQrDetected = { qrCode = it; mostrarCamara = false }, modifier = Modifier.fillMaxSize())
                Button(onClick = { mostrarCamara = false }, modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp)) { Text("Cancelar Escaneo") }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Operario actual: $nombreUsuario", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                // BOTÓN 1: QR
                Button(onClick = { launcherPermisoCamara.launch(Manifest.permission.CAMERA) }, modifier = Modifier.fillMaxWidth().height(55.dp)) { Text("1. Escanear Código QR", fontSize = 16.sp) }
                Text(text = "ID Escaneado: ${if (qrCode.isEmpty()) "Pendiente..." else qrCode}", color = if (qrCode.isEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // CAJONES DE TEXTO
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre del Producto") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo / Referencia") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción del producto") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                OutlinedTextField(value = fechaEscaneo, onValueChange = {}, label = { Text("Fecha de Ingreso") }, modifier = Modifier.fillMaxWidth(), readOnly = true)

                // CHECKBOX
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Checkbox(checked = funciona, onCheckedChange = { funciona = it })
                    Text("El equipo funciona correctamente", fontSize = 16.sp)
                }

                // SPINNER
                ExposedDropdownMenuBox(expanded = expandirSpinner, onExpandedChange = { expandirSpinner = !expandirSpinner }) {
                    OutlinedTextField(value = garantiaSeleccionada, onValueChange = {}, readOnly = true, label = { Text("Tiempo de Garantía") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirSpinner) }, colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(), modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(expanded = expandirSpinner, onDismissRequest = { expandirSpinner = false }) {
                        opcionesGarantia.forEach { opcion -> DropdownMenuItem(text = { Text(opcion) }, onClick = { garantiaSeleccionada = opcion; expandirSpinner = false }) }
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // --- BOTÓN 2: FOTOGRAFÍA (AQUÍ ESTÁ LA CORRECCIÓN) ---
                Button(
                    onClick = { launcherFoto.launch(null) }, // <-- Se agregó el null aquí
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (fotoBase64.isEmpty()) MaterialTheme.colorScheme.secondary else Color(0xFF4CAF50))
                ) {
                    if (fotoBase64.isEmpty()) Text("2. Tomar Fotografía (Evidencia)", fontSize = 16.sp)
                    else { Icon(Icons.Default.CheckCircle, contentDescription = "Foto OK", modifier = Modifier.padding(end = 8.dp)); Text("¡Fotografía Capturada!", fontSize = 16.sp) }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- BOTÓN 3: GUARDAR EN LA NUBE ---
                Button(
                    onClick = {
                        if (qrCode.isEmpty() || nombre.isEmpty()) {
                            Toast.makeText(context, "Falta el código QR o el Nombre", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        guardando = true

                        // Empaquetamos los datos
                        val nuevoProducto = Producto(
                            idQr = qrCode,
                            nombreUsuario = nombreUsuario,
                            nombreProducto = nombre,
                            fechaEscaneo = fechaEscaneo,
                            modelo = modelo,
                            descripcion = descripcion,
                            funciona = funciona,
                            garantiaMeses = garantiaSeleccionada
                        )

                        // Lanzamos el envío a internet en segundo plano
                        coroutineScope.launch {
                            val exito = ConexionNube.enviarProducto(nuevoProducto, fotoBase64)
                            guardando = false

                            if (exito) {
                                // Programamos la alarma si hay garantía
                                programarAlarma(context, nombre, modelo, garantiaSeleccionada)
                                Toast.makeText(context, "¡Guardado exitosamente!", Toast.LENGTH_LONG).show()
                                onBack() // Volvemos al menú principal
                            } else {
                                Toast.makeText(context, "Error al guardar. Revisa tu internet.", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(65.dp),
                    enabled = !guardando // Bloquea el botón mientras guarda
                ) {
                    if (guardando) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text("3. GUARDAR EN INVENTARIO", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

// ==========================================
// FUNCIÓN PARA CALCULAR Y PROGRAMAR LA ALARMA
// ==========================================
fun programarAlarma(context: Context, nombre: String, modelo: String, mesesGarantia: String) {
    if (mesesGarantia == "Sin garantía") return

    // Extraemos el número (ej: "3 meses" -> 3)
    val meses = mesesGarantia.split(" ")[0].toIntOrNull() ?: return

    // Calculamos la fecha futura sumando los meses
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, meses)

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, WarrantyAlarmReceiver::class.java).apply {
        putExtra("producto_nombre", nombre)
        putExtra("producto_modelo", modelo)
    }

    // Identificador único para cada producto
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        nombre.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Solo programa si tiene permisos (Android 12+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
        Toast.makeText(context, "Permite alarmas exactas en ajustes para la garantía", Toast.LENGTH_LONG).show()
        return
    }

    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
}