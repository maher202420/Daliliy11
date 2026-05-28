package com.example.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object SupabaseClient {
    private const val DEFAULT_BASE_URL = "https://sazbudkuzxbvmuztaxeg.supabase.co/rest/v1/"
    private const val DEFAULT_API_KEY = "sb_publishable_vvR8V-Y4Ge4-PMZa1AuFnQ_t9TJrwnx"

    @Volatile
    var currentUrl: String = DEFAULT_BASE_URL
        private set

    @Volatile
    var currentApiKey: String = DEFAULT_API_KEY
        private set

    @Volatile
    var api: SupabaseApi = buildApi(DEFAULT_BASE_URL, DEFAULT_API_KEY)
        private set

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private fun buildApi(baseUrl: String, apiKey: String): SupabaseApi {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("apikey", apiKey)
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SupabaseApi::class.java)
    }

    /**
     * Hot swaps the database configurations dynamically.
     */
    fun updateConfig(newUrl: String, newApiKey: String) {
        val verifiedUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        currentUrl = verifiedUrl
        currentApiKey = newApiKey
        api = buildApi(verifiedUrl, newApiKey)
    }
}

