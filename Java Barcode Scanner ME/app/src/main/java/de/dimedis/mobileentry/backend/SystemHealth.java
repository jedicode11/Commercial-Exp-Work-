package de.dimedis.mobileentry.backend;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import java.io.File;

import de.dimedis.mobileentry.util.Logger;
import de.dimedis.mobileentry.util.PrefUtils;

public class SystemHealth {//system_health
    static final String TAG = "SystemHealth";

    int battery_percent;//0-100
    /**
     * Number, free storage space on file system where local scan data is kept (in megabytes)
     */
    long local_data_storage_free;
    /**
     * Number, free storage space on file system where logfile is kept (in megabytes)
     */
    long logfile_storage_free;
    /**
     * Number, logfile size (in megabytes)
     */
    long logfile_size;
    /**
     * Boolean, true if logfile writes
     */
    boolean logfile_active;


    public float getBatteryLevel(Context context) {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if (level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float) level / (float) scale) * 100.0f;
    }

    public SystemHealth(Context context) {
        File externalFilesDir = context.getExternalFilesDir(null);
        long freeSizeInBytes = externalFilesDir.getFreeSpace();// statFs.getFreeBytes();
        long freeSizeInMB = freeSizeInBytes >>> 20;
        Log.i(TAG, "freeSizeInBytes: " + freeSizeInBytes + "(" + freeSizeInMB + " MB)");
        logfile_storage_free = local_data_storage_free = freeSizeInMB;

        float batteryLevel = getBatteryLevel(context);
        Log.i(TAG, "batteryLevel: " + batteryLevel);
        battery_percent = (int) batteryLevel;
        logfile_active = PrefUtils.isWriteLogfile();
        logfile_size = Logger.geLogfileSize();
    }
}
