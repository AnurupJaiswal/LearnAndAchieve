package com.anurupjaiswal.learnandachieve.basic.utilitytools

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log

object NetworkUtil {
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var networkReceiver: BroadcastReceiver? = null

    // ✅ Check Internet Connection (Android 6 to 15+)
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Android 6+
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        return false
    }

    // ✅ Register Network Callback (Android 7+)
    fun registerNetworkCallback(context: Context, listener: (Boolean) -> Unit) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // Android 7+ (Nougat)
            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    listener(true) // Internet Connected
                }

                override fun onLost(network: Network) {
                    listener(false) // Internet Disconnected
                }
            }
            connectivityManager.registerDefaultNetworkCallback(networkCallback!!)
        } else {

            registerNetworkReceiver(context, listener)
        }
    }

    // ✅ Register Broadcast Receiver (Fallback for Android 6 and below)
    fun registerNetworkReceiver(context: Context, listener: (Boolean) -> Unit) {
        networkReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                context?.let {
                    val isConnected = isInternetAvailable(it)
                    listener(isConnected)
                }
            }
        }
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(networkReceiver, intentFilter)
    }

    // ✅ Unregister Network Callback (Clean up memory)
    fun unregisterNetworkCallback(context: Context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
            } catch (e: Exception) {
                Log.e("NetworkUtil", "⚠ Error unregistering network callback: ${e.message}")
            }
        } else {
            networkReceiver?.let { context.unregisterReceiver(it) }
        }
    }
}
