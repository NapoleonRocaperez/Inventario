package com.example.inventario.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class WarrantyAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val nombreProducto = intent.getStringExtra("producto_nombre") ?: "Producto en inventario"
        val modeloProducto = intent.getStringExtra("producto_modelo") ?: "Sin modelo"

        Log.d("AlarmaGarantia", "¡Alerta activada para: $nombreProducto!")

        val channelId = "garantia_channel"

        // 1. Crear el canal de notificaciones (Obligatorio en Android modernos)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas de Garantía",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Avisa cuando vence la garantía de un producto"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Construir el mensaje visual
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Ícono por defecto
            .setContentTitle("Revisión de Garantía")
            .setContentText("El producto: $nombreProducto ($modeloProducto) requiere atención.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)

        // 3. Mostrar la notificación (Verificando permisos)
        val notificationManagerCompat = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManagerCompat.notify(nombreProducto.hashCode(), builder.build())
            }
        } else {
            notificationManagerCompat.notify(nombreProducto.hashCode(), builder.build())
        }
    }
}
