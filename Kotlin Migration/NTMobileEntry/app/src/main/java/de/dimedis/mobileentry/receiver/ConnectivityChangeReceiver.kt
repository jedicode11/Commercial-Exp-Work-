package de.dimedis.mobileentry.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.dimedis.mobileentry.model.StatusManager
import de.dimedis.mobileentry.util.CommonUtils.isInternetConnected
import de.dimedis.mobileentry.util.Logger.i

class ConnectivityChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        i(TAG, "Connectivity changed, is Internet connected: " + isInternetConnected())
        StatusManager.getInstance()!!.updateStatus()
    }

    companion object {
        private val TAG = ConnectivityChangeReceiver::class.java.simpleName
    }
}