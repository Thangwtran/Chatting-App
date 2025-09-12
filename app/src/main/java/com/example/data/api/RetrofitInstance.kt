package com.example.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    // /pdf-get-question
    // https://n8n.viettelmedia.vn/webhook/pdf-get-question
    private const val BASE_URL = "https://n8n.viettelmedia.vn/webhook/"

    @JvmStatic
    fun getApiService(): ApiService {
        val client = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS) // timeout kết nối
            .readTimeout(2, TimeUnit.SECONDS)    // timeout đọc dữ liệu
            .writeTimeout(2, TimeUnit.SECONDS)   // timeout ghi dữ liệu
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
//            .client(client) // gắn client vào đây
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}