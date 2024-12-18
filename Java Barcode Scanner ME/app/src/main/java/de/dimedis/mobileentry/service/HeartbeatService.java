package de.dimedis.mobileentry.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

import de.dimedis.mobileentry.backend.BackendService;
import de.dimedis.mobileentry.receiver.HeartbeatWakefulReceiver;
import de.dimedis.mobileentry.util.AppContext;
import de.dimedis.mobileentry.util.DataBaseUtil;
import de.dimedis.mobileentry.util.PrefUtils;
import de.dimedis.mobileentry.util.UpdateUtil;

public class HeartbeatService extends IntentService {
    static final String TAG = "HeartbeatService";

    public static void scheduleService() {
        Context context = AppContext.get();
        long updateIntervalSec = PrefUtils.getHeartbeatIntervalIdle();

        updateIntervalSec *= 1000L;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent updateIntent = new Intent(context, HeartbeatService.class);
        PendingIntent pendingUpdateIntent = PendingIntent.getService(context, 0, updateIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // schedule to run in few minutes from now
        Date curr = new Date(System.currentTimeMillis());
        Date runAt = new Date(System.currentTimeMillis() + updateIntervalSec);

        if (curr.after(runAt)) {
            runAt = new Date(runAt.getTime() + updateIntervalSec);
            if (curr.after(runAt)) runAt = new Date(runAt.getTime() + updateIntervalSec);
        }
        alarmManager.cancel(pendingUpdateIntent);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, curr.getTime() + 1000/*runAt.getTime()*/, updateIntervalSec, pendingUpdateIntent);

        Log.i(TAG, "Update notification service scheduled to run at " + runAt);
        context.sendBroadcast(new Intent(context, HeartbeatWakefulReceiver.class));
    }


    public static void cancelService() {
        Context context = AppContext.get();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent updateIntent = new Intent(context, HeartbeatService.class);
        PendingIntent pendingUpdateIntent = PendingIntent.getService(context, 0, updateIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingUpdateIntent);
        Log.i(TAG, "Update notification service cancelled");
    }

    public HeartbeatService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "intent:" + intent);
        BackendService.sendHeartbeat();
        DataBaseUtil.uploadCachedTickets();
        DataBaseUtil.uploadCachedOfflineSessions();
        UpdateUtil.updateSettings();
    }
}
