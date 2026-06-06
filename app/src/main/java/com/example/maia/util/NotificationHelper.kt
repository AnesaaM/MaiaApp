package com.example.maia.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {
    private const val CHANNEL_ID = "maia_shopping"
    private const val CHANNEL_NAME = "Maia Shopping"
    private var notificationId = 0

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Shopping notifications for Maia" }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun showCartNotification(context: Context, productName: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Added to Cart")
            .setContentText("\"$productName\" was added to your cart")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(++notificationId, notification)
        } catch (_: SecurityException) {
            // Permission not granted yet
        }
    }

    fun showOrderConfirmationNotification(context: Context, orderId: Int = 0) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Order Placed!")
            .setContentText(
                if (orderId > 0) "Order #$orderId has been confirmed."
                else "Your order has been placed successfully!"
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(++notificationId, notification)
        } catch (_: SecurityException) {
            // Permission not granted yet
        }
    }
}
