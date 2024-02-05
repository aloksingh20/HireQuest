package com.example.gethired.NetworkManagement

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow


data class NetworkInfo(val isConnected: Boolean, val type: String, val signalStrength: Int)

class NetworkManager(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun getNetworkConnectivityFlow() = callbackFlow<NetworkInfo> {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                val networkType = getNetworkType(network)
//                val signalStrength = getSignalStrength(network)
                trySend(NetworkInfo(true, networkType, 3)).isSuccess

            }

            override fun onLost(network: Network) {
                trySend(NetworkInfo(false, "", 0))
            }
        }

        // Register the callback to receive network change notifications
        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        // Handle cleanup when the flow is cancelled
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    fun isNetworkAvailable(): Boolean {
        // Check for active network and its connectivity status
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

    private fun getNetworkType(network: Network): String {
        return when (connectivityManager.getNetworkInfo(network)?.type) {
            ConnectivityManager.TYPE_WIFI -> "Wi-Fi"
            ConnectivityManager.TYPE_MOBILE -> "Cellular"
            else -> "Unknown"
        }
    }

}

