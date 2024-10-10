package com.ihsanfrr.ourdicodingevent.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

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

    fun verifyInternet(context: Context) {
        if (!hasInternetConnection(context)) {
            "Tidak ada koneksi internet".displayToast(context)
            displayNoConnectionDialog(context)
        }
    }

    private fun displayNoConnectionDialog(context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle("Koneksi Hilang")
            setMessage("Anda sedang offline. Cek kembali koneksi internet Anda.")
            setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            create().show()
        }
    }

    private fun String.displayToast(context: Context) {
        Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
    }
}