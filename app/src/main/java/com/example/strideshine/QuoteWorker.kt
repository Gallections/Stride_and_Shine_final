package com.example.strideshine

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QuoteWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Create Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.quotable.io/") // Base URL for the quote API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val quoteApi = retrofit.create(QuoteApi::class.java)

        // Fetch a random quote
        val response = quoteApi.getRandomQuote().execute()

        return if (response.isSuccessful) {
            response.body()?.let { quoteResponse ->
                // Show the notification with the fetched quote
                val notification = QuoteNotification(applicationContext)
                notification.showNotification(quoteResponse.content, quoteResponse.author)
                Result.success()
            } ?: Result.failure()
        } else {
            Result.failure()
        }
    }
}