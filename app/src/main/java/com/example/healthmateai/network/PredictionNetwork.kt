package com.example.healthmateai.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://healthmateai-backend-y1xw.onrender.com/"

interface PredictionApiService {
    @POST("predict")
    suspend fun predict(@Body request: PredictionRequest): PredictionResponse
}

object PredictionApiClient {
    val service: PredictionApiService by lazy {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("PredictionApi", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PredictionApiService::class.java)
    }
}

class PredictionRepository(
    private val apiService: PredictionApiService = PredictionApiClient.service
) {
    suspend fun predict(request: PredictionRequest): PredictionResponse {
        return apiService.predict(request)
    }
}