package com.example.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    // /pdf-get-question
    // https://n8n.viettelmedia.vn/webhook/pdf-get-question
    private const val BASE_URL = "https://n8n.viettelmedia.vn/webhook/"

    @JvmStatic
    fun getApiService(): ApiService {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiService::class.java)
    }
}