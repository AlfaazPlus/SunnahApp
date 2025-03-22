package com.alfaazplus.sunnah.ui.utils.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import com.alfaazplus.sunnah.ui.utils.message.MessageUtils

class NetworkStateMonitor : ConnectivityManager.NetworkCallback() {
    private var previouslyConnected: Boolean? = null
    private val listeners: MutableSet<NetworkStateReceiverListener> = HashSet()
    private val networkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
        .build()

    fun enable(context: Context) {
        try {
            with(context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager) {
                registerNetworkCallback(networkRequest, this@NetworkStateMonitor)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disable(context: Context) {
        try {
            with(context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager) {
                unregisterNetworkCallback(this@NetworkStateMonitor)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAvailable(network: Network) {
        notifyStateToAll(true)
    }

    override fun onLosing(network: Network, maxMsToLive: Int) {
    }

    override fun onLost(network: Network) {
    }

    override fun onUnavailable() {
        notifyStateToAll(false)
    }

    private fun notifyStateToAll(isConnected: Boolean) {
        for (listener in listeners) notifyState(isConnected, listener)
    }

    private fun notifyState(isConnected: Boolean, listener: NetworkStateReceiverListener?) {
        if (listener == null) return

        if (isConnected) {
            if (previouslyConnected == true) return
            listener.onNetworkAvailable()
        } else {
            if (previouslyConnected == false) return
            listener.onNetworkLost()
        }

        previouslyConnected = isConnected
    }

    fun addListener(listener: NetworkStateReceiverListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: NetworkStateReceiverListener) {
        listeners.remove(listener)
    }

    interface NetworkStateReceiverListener {
        fun onNetworkAvailable()
        fun onNetworkLost()
    }

    companion object {
        @Suppress("DEPRECATION")
        fun isNetworkConnected(context: Context): Boolean {
            val mgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val nw = mgr.activeNetwork ?: return false
                val actNw = mgr.getNetworkCapabilities(nw) ?: return false
                return actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
            } else {
                return mgr.activeNetworkInfo?.state == NetworkInfo.State.CONNECTED
            }
        }

        @JvmOverloads
        fun canProceed(
            context: Context,
            cancelable: Boolean = true,
            runOnDismissIfCantProceed: (() -> Unit)? = null
        ): Boolean {
            if (!isNetworkConnected(context)) {
                MessageUtils.showNoInternet(context, cancelable, runOnDismissIfCantProceed)
                return false
            }

            return true
        }
    }

}