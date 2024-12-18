package de.dimedis.mobileentry.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.dimedis.mobileentry.model.StatusManager;
import de.dimedis.mobileentry.util.CommonUtils;
import de.dimedis.mobileentry.util.Logger;

public class ConnectivityChangeReceiver extends BroadcastReceiver {
    private static final String TAG = ConnectivityChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.i(TAG, "Connectivity changed, is Internet connected: " + CommonUtils.isInternetConnected());
        StatusManager.getInstance().updateStatus();
    }
}
