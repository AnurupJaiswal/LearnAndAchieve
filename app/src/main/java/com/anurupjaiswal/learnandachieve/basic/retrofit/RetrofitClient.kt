package com.anurupjaiswal.learnandachieve.basic.retrofit

import com.google.gson.GsonBuilder
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private var retrofit: Retrofit? = null

    @JvmStatic
    val client: ApiService
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)


            val client = OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
            if (Const.Development == Constants.Debug) {
                client.addInterceptor(interceptor)
            }
            val gson = GsonBuilder()
                .setLenient()
                .create()

            retrofit = Retrofit.Builder()
                .baseUrl(Const.HOST_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.build())
                .build()
            return retrofit!!.create(ApiService::class.java)
        }

}