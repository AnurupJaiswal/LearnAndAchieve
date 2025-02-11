package com.anurupjaiswal.learnandachieve.basic.utilitytools

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.anurupjaiswal.learnandachieve.basic.database.UserDataHelper

class AppController : Application() {


    /**
     * @return
     */
    init {
        Log.e("","AppCanteroller")
        instance = this
    }

    /**
     * @return
     */
    val isOnline: Boolean
        get() {
            try {
                val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
        Log.e("","AppCanteroller    onCreate")
        UserDataHelper(this)


    }



    companion object {
        var context: Context? = null

        @get:Synchronized
        lateinit var instance: AppController
            private set

        @JvmName("getContext1")
        fun getContext(): Context {
            return instance
        }

        /**
         * @param ctx
         * @return
         */
        fun getInstance(ctx: Context): AppController {
            context = ctx.applicationContext
            return instance
        }
    }
}