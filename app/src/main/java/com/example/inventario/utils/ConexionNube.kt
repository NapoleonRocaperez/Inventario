package com.example.inventario.utils

import android.util.Log
import com.example.inventario.model.Producto
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ConexionNube {
    // 🔥 TU URL MÁS RECIENTE 🔥
    private const val SCRIPT_URL = "https://script.google.com/macros/s/AKfycbyRmMOjZilY0JgKIYquX4-p0skUulXtfX2qFV5C5V59RXVRZo6dKeONb5R1K2aCQJYX4w/exec"

    private val cliente = OkHttpClient()

    // ----------------------------------------------------
    // 1. GUARDAR (Parte 1: Nuevo Ingreso)
    // ----------------------------------------------------
    suspend fun enviarProducto(producto: Producto, fotoBase64: String): Boolean = suspendCoroutine { continuation ->
        try {
            val jsonDatos = JSONObject().apply {
                put("idQr", producto.idQr)
                put("nombreUsuario", producto.nombreUsuario)
                put("nombreProducto", producto.nombreProducto)
                put("fechaEscaneo", producto.fechaEscaneo)
                put("modelo", producto.modelo)
                put("descripcion", producto.descripcion)
                put("funciona", producto.funciona)
                put("garantiaMeses", producto.garantiaMeses)
                put("imageBase64", fotoBase64)
            }

            val cuerpoPeticion = jsonDatos.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val peticion = Request.Builder().url(SCRIPT_URL).post(cuerpoPeticion).build()

            cliente.newCall(peticion).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { continuation.resume(false) }
                override fun onResponse(call: Call, response: Response) {
                    val respuesta = response.body?.string() ?: ""
                    continuation.resume(respuesta.contains("success"))
                }
            })
        } catch (e: Exception) { continuation.resume(false) }
    }

    // ----------------------------------------------------
    // 2. BUSCAR UN SOLO QR (Parte 2: Consultar)
    // ----------------------------------------------------
    suspend fun buscarProducto(idQr: String): Producto? = suspendCoroutine { continuation ->
        try {
            val urlBusqueda = "$SCRIPT_URL?idQr=$idQr"
            val peticion = Request.Builder().url(urlBusqueda).get().build()

            cliente.newCall(peticion).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { continuation.resume(null) }
                override fun onResponse(call: Call, response: Response) {
                    val respuestaServidor = response.body?.string() ?: ""

                    if (respuestaServidor.contains("found")) {
                        val jsonProducto = JSONObject(respuestaServidor).getJSONObject("producto")

                        val p = Producto(
                            idQr = jsonProducto.getString("idQr"),
                            nombreUsuario = jsonProducto.getString("nombreUsuario"),
                            nombreProducto = jsonProducto.getString("nombreProducto"),
                            fechaEscaneo = jsonProducto.getString("fechaEscaneo"),
                            modelo = jsonProducto.getString("modelo"),
                            descripcion = jsonProducto.getString("descripcion"),
                            funciona = jsonProducto.getString("funciona") == "SÍ",
                            garantiaMeses = jsonProducto.getString("garantiaMeses"),
                            urlFotografia = jsonProducto.getString("urlFotografia")
                        )
                        continuation.resume(p)
                    } else {
                        continuation.resume(null)
                    }
                }
            })
        } catch (e: Exception) { continuation.resume(null) }
    }

    // ----------------------------------------------------
    // 3. TRAER TODO EL HISTORIAL (Parte 3)
    // ----------------------------------------------------
    suspend fun obtenerHistorial(): List<Producto> = suspendCoroutine { continuation ->
        try {
            val peticion = Request.Builder().url(SCRIPT_URL).get().build()

            cliente.newCall(peticion).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { continuation.resume(emptyList()) }
                override fun onResponse(call: Call, response: Response) {
                    val respuestaServidor = response.body?.string() ?: "[]"
                    val lista = mutableListOf<Producto>()
                    try {
                        val array = JSONArray(respuestaServidor)
                        for (i in 0 until array.length()) {
                            val item = array.getJSONObject(i)
                            lista.add(Producto(
                                idQr = item.optString("idQr", ""),
                                nombreProducto = item.optString("nombreProducto", ""),
                                modelo = item.optString("modelo", ""),
                                descripcion = item.optString("descripcion", ""),
                                nombreUsuario = item.optString("nombreUsuario", ""),
                                fechaEscaneo = item.optString("fechaEscaneo", ""),
                                funciona = item.optString("funciona", "SÍ") == "SÍ",
                                garantiaMeses = item.optString("garantiaMeses", ""),
                                urlFotografia = item.optString("urlFotografia", "")
                            ))
                        }
                    } catch (e: Exception) { Log.e("NUBE", "Error parseando historial: ${e.message}") }
                    continuation.resume(lista)
                }
            })
        } catch (e: Exception) { continuation.resume(emptyList()) }
    }

    // ----------------------------------------------------
    // 4. ACTUALIZAR ESTADO DE UN PRODUCTO (Parte 3)
    // ----------------------------------------------------
    suspend fun actualizarProducto(producto: Producto): Boolean = suspendCoroutine { continuation ->
        try {
            val jsonDatos = JSONObject().apply {
                put("action", "update")
                put("idQr", producto.idQr)
                put("nombreProducto", producto.nombreProducto)
                put("modelo", producto.modelo)
                put("descripcion", producto.descripcion)
                put("funciona", producto.funciona)
            }

            val cuerpoPeticion = jsonDatos.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val peticion = Request.Builder().url(SCRIPT_URL).post(cuerpoPeticion).build()

            cliente.newCall(peticion).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { continuation.resume(false) }
                override fun onResponse(call: Call, response: Response) {
                    val respuesta = response.body?.string() ?: ""
                    continuation.resume(respuesta.contains("success"))
                }
            })
        } catch (e: Exception) { continuation.resume(false) }
    }
}