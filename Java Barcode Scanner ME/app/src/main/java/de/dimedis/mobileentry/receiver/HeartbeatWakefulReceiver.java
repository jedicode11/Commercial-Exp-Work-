package de.dimedis.mobileentry.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import androidx.legacy.content.WakefulBroadcastReceiver;

import de.dimedis.mobileentry.service.HeartbeatService;

public class HeartbeatWakefulReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // This is the Intent to deliver to our service.
        Intent service = new Intent(context, HeartbeatService.class);
        Log.i("SimpleWakefulReceiver", "Starting service @ " + SystemClock.elapsedRealtime());
        startWakefulService(context, service);
    }
}