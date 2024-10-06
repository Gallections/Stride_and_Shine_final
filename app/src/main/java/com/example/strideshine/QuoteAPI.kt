package com.example.strideshine

import retrofit2.Call
import retrofit2.http.GET

interface QuoteApi {
    @GET("random") // Use the correct endpoint for your chosen API
    fun getRandomQuote(): Call<QuoteResponse>
}