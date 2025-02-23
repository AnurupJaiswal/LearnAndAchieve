package com.anurupjaiswal.learnandachieve.basic.network

import android.content.Context
import com.anurupjaiswal.learnandachieve.basic.utilitytools.AppController
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ConnectivityInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Use the AppController's connectivity check
        if (!AppController.instance.isOnline) {
            throw NoConnectivityException()
        }
        return chain.proceed(chain.request())
    }
}

// Custom exception thrown when no connectivity is available
class NoConnectivityException : IOException("No internet connection")