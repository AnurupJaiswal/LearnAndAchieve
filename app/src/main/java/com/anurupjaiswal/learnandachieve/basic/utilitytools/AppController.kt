package com.anurupjaiswal.learnandachieve.basic.utilitytools

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.anurupjaiswal.learnandachieve.basic.database.UserDataHelper



class AppController : Application() {

    init {
        Log.e("AppController", "AppController instance created")
        instance = this
    }
    val isOnline: Boolean
        get() {
            try {
                val connectivityManager =
                    applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val network = connectivityManager.activeNetwork ?: return false
                val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } catch (e: Exception) {
                Log.e("Connectivity", "CheckConnectivity Exception: ${e.message}")
            }
            return false
        }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.e("AppController", "onCreate")
        // Initialize your database helper or other singletons here
        UserDataHelper(this)
    }

    companion object {
        lateinit var instance: AppController
            private set

        fun getContext(): Context {
            return instance.applicationContext
        }
    }
}
