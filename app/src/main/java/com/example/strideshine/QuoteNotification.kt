package com.example.strideshine

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class QuoteNotification(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val hopefulQuotes = listOf(
        "Stay positive! – Anonymous",
        "You are stronger than you think. – Unknown",
        "Believe you can and you're halfway there. – Theodore Roosevelt",
        "The best is yet to come. – Frank Sinatra",
        "Keep your face always toward the sunshine—and shadows will fall behind you. – Walt Whitman",
        "Every day may not be good, but there's something good in every day. – Unknown",
        "You are important too. – InspireBot",
        "Happiness is not by chance, but by choice. – Jim Rohn",
        "Believe in yourself and all that you are. – Christian D. Larson"
    )

    fun showNotification(quote: String, author: String) {
        // Check if POST_NOTIFICATIONS permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, don't show notification
            Toast.makeText(context, "Permission required to show notifications", Toast.LENGTH_SHORT).show()
            return
        }

        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context, 1, activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, StepNotification.CHANNEL_ID)
            .setSmallIcon(R.drawable.awesome)
            .setContentTitle("Here's a quote made just for you!")
            .setContentText("\"$quote\" - $author")
            .setContentIntent(activityPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun getRandomHopefulQuote(): String {
        return hopefulQuotes.random() // Selects a random quote from the list
    }

    fun showRandomQuoteNotification() {
        val randomQuote = getRandomHopefulQuote()
        val (quote, author) = randomQuote.split(" – ")
        showNotification(quote, author)
    }
}
