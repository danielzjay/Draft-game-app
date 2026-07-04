package com.example.network

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // BODY logging is fine for your own backend in debug builds; it never sees the Relworx
        // secret key since that key only lives on the server, not in this app.
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val sanitizedBaseUrl: String
        get() {
            var url = BuildConfig.PAYMENT_BACKEND_BASE_URL.trim()
            if (url.endsWith("relworx.php")) {
                url = url.substringBeforeLast("relworx.php")
            }
            if (!url.endsWith("/")) {
                url += "/"
            }
            return url
        }

    // Points at YOUR backend proxy (see /server/relworxProxy.js), configured via .env ->
    // BuildConfig.PAYMENT_BACKEND_BASE_URL. Set the real value in your own .env file, which is
    // git-ignored — never commit it.
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(sanitizedBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val paymentApi: PaymentApiService = retrofit.create(PaymentApiService::class.java)
}
