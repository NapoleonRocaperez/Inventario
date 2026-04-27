package com.example.inventario.model

/**
 * Representa la estructura de un producto que se enviará a Google Sheets
 */
data class Producto(
    val idQr: String = "",             // El código QR escaneado (Parte 1)
    val nombreUsuario: String = "",    // Quién inició sesión con Google
    val nombreProducto: String = "",   // Nombre digitado en el formulario
    val fechaEscaneo: String = "",     // Fecha automática del sistema
    val modelo: String = "",           // Modelo o referencia
    val descripcion: String = "",      // Detalles técnicos
    val funciona: Boolean = true,      // Checkbox (True = Sí, False = No)
    val garantiaMeses: String = "",    // Lo que se selecciona en el Spinner
    val urlFotografia: String = ""     // El enlace a la imagen en Drive
)