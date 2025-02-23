package com.anurupjaiswal.learnandachieve.basic.network

object GlobalRequestManager {
    // A list of lambdas that when executed will re-fire a previously failed call.
    private val requestQueue = mutableListOf<() -> Unit>()

    // Queue a request lambda.
    fun queueRequest(request: () -> Unit) {
        synchronized(this) {
            requestQueue.add(request)
        }
    }

    // Execute all queued requests and clear the queue.
    fun retryQueuedRequests() {
        synchronized(this) {
            val requests = requestQueue.toList()
            requestQueue.clear()
            requests.forEach { it() }
        }
    }
}