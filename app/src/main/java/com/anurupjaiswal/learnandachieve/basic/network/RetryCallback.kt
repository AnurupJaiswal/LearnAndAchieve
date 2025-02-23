package com.anurupjaiswal.learnandachieve.basic.network

import android.os.Handler
import android.os.Looper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A generic callback wrapper that automatically retries the API call
 * on a NoConnectivityException using exponential backoff.
 */
class RetryCallback<T>(
    private val originalCall: Call<T>,
    private var retryCount: Int = 0,
    private val maxRetries: Int = 5,
    private val baseDelay: Long = 2000L, // base delay in milliseconds
    private val callback: Callback<T>
) : Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {
        // Forward successful response to the original callback.
        callback.onResponse(call, response)
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        // Check if the failure is due to connectivity issues.
        if (t is NoConnectivityException) {
            if (retryCount < maxRetries) {
                retryCount++
                // Calculate delay using exponential backoff.
                val delay = baseDelay * (1 shl (retryCount - 1))
                Handler(Looper.getMainLooper()).postDelayed({
                    // Retry by cloning the original call.
                    originalCall.clone().enqueue(this)
                }, delay)
            } else {

                callback.onFailure(call, t)
            }
        } else {
            // For other errors, forward immediately.
            callback.onFailure(call, t)
        }
    }
}
