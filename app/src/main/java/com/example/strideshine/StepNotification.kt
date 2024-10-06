package com.example.strideshine

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class StepNotification(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Step Tracker Notifications", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for steps and quotes"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Request POST_NOTIFICATIONS permission for Android 13+
    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(activity, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 2)
        }
    }


    fun handlePermissionResult(grantResults: IntArray): Boolean {
        return grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val CHANNEL_ID = "step_tracker_channel"
    }
}

