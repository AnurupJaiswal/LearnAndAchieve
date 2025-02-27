package com.anurupjaiswal.learnandachieve.basic.retrofit

import android.os.Build
import com.anurupjaiswal.learnandachieve.basic.network.ConnectivityInterceptor
import com.google.gson.GsonBuilder
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var retrofit: Retrofit? = null

    private fun getUserAgent(): String {
        val deviceModel = Build.MODEL ?: "Unknown"
        val osVersion = Build.VERSION.RELEASE ?: "Unknown"
        return "Device: $deviceModel | OS: android $osVersion | browser: Chrome/0 Mobile"
    }

    private val userAgentInterceptor = Interceptor { chain ->
        val request: Request = chain.request().newBuilder()
            .header("User-Agent", getUserAgent()) // **Set Custom User-Agent**
            .build()
        chain.proceed(request)
    }

    @JvmStatic
    val client: ApiService
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val clientBuilder = OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .addInterceptor(userAgentInterceptor) // **Apply User-Agent Interceptor**
                .addInterceptor(ConnectivityInterceptor())

            if (Const.Development == Constants.Debug) {
                clientBuilder.addInterceptor(interceptor)
            }

            val gson = GsonBuilder()
                .setLenient()
                .create()

            retrofit = Retrofit.Builder()
                .baseUrl(Const.HOST_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(clientBuilder.build())
                .build()

            return retrofit!!.create(ApiService::class.java)
        }
}
