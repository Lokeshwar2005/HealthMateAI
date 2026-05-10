package com.example.healthmateai.network.openrouter

import android.util.Log
import com.example.healthmateai.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

private const val OPEN_ROUTER_BASE_URL = "https://openrouter.ai/"

interface OpenRouterApiService {
    @POST("api/v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authorization: String,
        @Header("HTTP-Referer") referer: String = "https://healthmate-ai.local",
        @Header("X-Title") title: String = "HealthMateAI",
        @Body request: OpenRouterRequest
    ): OpenRouterResponse
}

object OpenRouterApiClient {
    val service: OpenRouterApiService by lazy {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("OpenRouterApi", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(45, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .callTimeout(45, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(OPEN_ROUTER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenRouterApiService::class.java)
    }
}

fun openRouterAuthHeader(): String {
    val key = BuildConfig.OPENROUTER_API_KEY.trim()
    require(key.isNotBlank()) {
        "OpenRouter API key missing. Add OPENROUTER_API_KEY to local.properties"
    }
    return "Bearer $key"
}
