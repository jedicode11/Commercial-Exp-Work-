package de.dimedis.mobileentry.receiver

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.legacy.content.WakefulBroadcastReceiver
import de.dimedis.mobileentry.service.HeartbeatService

class HeartbeatWakefulReceiver : WakefulBroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // This is the Intent to deliver to our service.
        val service = Intent(context, HeartbeatService::class.java)

        // Start the service, keeping the device awake while it is launching.
        Log.i("SimpleWakefulReceiver", "Starting service @ " + SystemClock.elapsedRealtime())
        startWakefulService(context, service)
    }
}