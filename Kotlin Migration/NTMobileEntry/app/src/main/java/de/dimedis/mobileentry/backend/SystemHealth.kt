package de.dimedis.mobileentry.backend

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

import android.os.BatteryManager
import de.dimedis.mobileentry.util.Logger
import de.dimedis.mobileentry.util.PrefUtils

class SystemHealth(context: Context) {
    /**
     * Number (0-100), battery level in percent
     */
    var battery_percent: Int //0-100
    /**
     * Number, free storage space on file system where local scan data is kept (in megabytes)
     */
    var local_data_storage_free: Long
    /**
     * Number, free storage space on file system where logfile is kept (in megabytes)
     */
    var logfile_storage_free: Long
    /**
     * Number, logfile size (in megabytes)
     */
    var logfile_size: Long
    /**
     * Boolean, true if logfile writes
     */
    var logfile_active: Boolean
    fun getBatteryLevel(context: Context): Float {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        // Error checking that probably isn't needed but I added just in case.
        return if (level == -1 || scale == -1) {
            50.0f
        } else level.toFloat() / scale.toFloat() * 100.0f
    }

    init {
        val externalFilesDir = context.getExternalFilesDir(null)
        val freeSizeInBytes = externalFilesDir!!.freeSpace
        val freeSizeInMB = freeSizeInBytes ushr 20
        Log.i(TAG, "freeSizeInBytes: $freeSizeInBytes($freeSizeInMB MB)")
        local_data_storage_free = freeSizeInMB
        logfile_storage_free = local_data_storage_free
        val batteryLevel = getBatteryLevel(context)
        Log.i(TAG, "batteryLevel: $batteryLevel")
        battery_percent = batteryLevel.toInt()
        logfile_active = PrefUtils.isWriteLogfile()
        logfile_size = Logger.geLogfileSize()
    }

    companion object {
        //system_health
        const val TAG = "SystemHealth"
    }
}