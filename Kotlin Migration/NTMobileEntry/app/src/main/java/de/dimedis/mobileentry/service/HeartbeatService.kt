package de.dimedis.mobileentry.service

import android.content.Intent
import android.app.IntentService
import android.app.AlarmManager
import android.app.PendingIntent
import android.util.Log
import de.dimedis.mobileentry.backend.BackendServiceUtil
import de.dimedis.mobileentry.receiver.HeartbeatWakefulReceiver
import de.dimedis.mobileentry.util.AppContext
import de.dimedis.mobileentry.util.DataBaseUtil
import de.dimedis.mobileentry.util.PrefUtils
import de.dimedis.mobileentry.util.UpdateUtil.updateSettings
import java.util.*

class HeartbeatService : IntentService(TAG) {
    override fun onHandleIntent(intent: Intent?) {
        Log.i(TAG, "intent:$intent")
        BackendServiceUtil.sendHeartbeat()
        DataBaseUtil.uploadCachedTickets()
        DataBaseUtil.uploadCachedOfflineSessions()
        updateSettings()
    }

    companion object {
        const val TAG = "HeartbeatService"
        fun scheduleService() {
            val context = AppContext.get()
            var updateIntervalSec: Long = PrefUtils.getHeartbeatIntervalIdle().toLong()
            updateIntervalSec *= 1000L
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            val updateIntent = Intent(context, HeartbeatService::class.java)
            val pendingUpdateIntent = PendingIntent.getService(context, 0, updateIntent, PendingIntent.FLAG_CANCEL_CURRENT)

            // schedule to run in few minutes from now
            val curr = Date(System.currentTimeMillis())
            var runAt = Date(System.currentTimeMillis() + updateIntervalSec)
            if (curr.after(runAt)) {
                runAt = Date(runAt.time + updateIntervalSec)
                if (curr.after(runAt)) runAt = Date(runAt.time + updateIntervalSec)
            }
            alarmManager.cancel(pendingUpdateIntent)
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, curr.time + 1000, updateIntervalSec, pendingUpdateIntent)
            Log.i(TAG, "Update notification service scheduled to run at $runAt")
            context.sendBroadcast(Intent(context, HeartbeatWakefulReceiver::class.java))
        }

        fun cancelService() {
            val context = AppContext.get()
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            val updateIntent = Intent(context, HeartbeatService::class.java)
            val pendingUpdateIntent = PendingIntent.getService(context, 0, updateIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            alarmManager.cancel(pendingUpdateIntent)
            Log.i(TAG, "Update notification service cancelled")
        }
    }
}