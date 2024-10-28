package com.ihsanfrr.ourdicodingevent.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object ConnectionHelper {
    @SuppressLint("ObsoleteSdkInt")
    private fun hasInternetConnection(context: Context): Boolean {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connManager.activeNetwork ?: return false
            val networkCapabilities = connManager.getNetworkCapabilities(activeNetwork) ?: return false
            return isNetworkAvailable(networkCapabilities)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            networkInfo?.isConnected == true
        }
    }

    private fun isNetworkAvailable(networkCapabilities: NetworkCapabilities): Boolean {
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    fun isNetworkConnected(context: Context): Boolean {
        return hasInternetConnection(context)
    }
}