package de.dimedis.mobileentry.model;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.util.CommonUtils;
import de.dimedis.mobileentry.util.DataBaseUtil;
import de.dimedis.mobileentry.util.Logger;
import de.dimedis.mobileentry.util.PrefUtils;
import de.dimedis.mobileentry.util.dynamicres.DynamicString;

public class StatusManager {
    private static final String TAG = StatusManager.class.getSimpleName();

    public enum Status {
        OFFLINE, LOCAL_SCAN, ONLINE;

        public String getText(Status status) {
            switch (status) {
                case OFFLINE:
                    return DynamicString.getInstance().getString(R.string.SCAN_STATUS_OFFLINE);
                case LOCAL_SCAN:
                    return DynamicString.getInstance().getString(R.string.SCAN_STATUS_LOCAL);
                case ONLINE:
                    return DynamicString.getInstance().getString(R.string.SCAN_STATUS_ONLINE);
                default:
                    return null;
            }
        }
    }

    private static volatile StatusManager sInstance;

    @NonNull
    private Status mStatus;

    @NonNull
    private static Status checkStatus() {
        if (PrefUtils.isLocalScanEnabled()) {
            return Status.LOCAL_SCAN;
        }
        return CommonUtils.isInternetConnected() ? Status.ONLINE : Status.OFFLINE;
    }

    private StatusManager() {
        mStatus = checkStatus();
    }

    @NonNull
    public static StatusManager getInstance() {
        if (sInstance == null) {
            synchronized (StatusManager.class) {
                if (sInstance == null) {
                    sInstance = new StatusManager();
                }
            }
        }
        return sInstance;
    }

    @NonNull
    public Status getStatus() {
        return mStatus;
    }

    public boolean isOnline() {
        return mStatus == Status.ONLINE;
    }

    public void updateStatus() {
        Status status = checkStatus();
        setStatus(status);
    }

    public void setStatus(Status status) {
        if (mStatus != status) {
            Logger.i(TAG, "Status changed to " + status);
            // Always update the status first
            mStatus = status;
            // Post the StatusChangedEvent
            EventBus.getDefault().post(new StatusChangedEvent(status));
            // Handle actions based on the new status
            switch (status) {
                case ONLINE:
                    Logger.w(TAG, "ONLINE isLoginCompleted:" + PrefUtils.isLoginCompleted());
                    DataBaseUtil.uploadCachedTickets();
                    DataBaseUtil.uploadCachedOfflineSessions();
                    break;
                case OFFLINE:
                    if (mStatus != Status.LOCAL_SCAN) {
                        int countTimeouts = PrefUtils.getOfflineDetectCount();
                        // Increment the count only if it's the first timeout
                        countTimeouts = (countTimeouts == 0) ? 1 : countTimeouts + 1;
                        if (countTimeouts >= 5) {
                            // Reset the count and set to LOCAL_SCAN
                            PrefUtils.setOfflineDetectCount(0);
                            getInstance().setStatus(Status.LOCAL_SCAN);
                        } else {
                            // Update the count and continue with OFFLINE status
                            PrefUtils.setOfflineDetectCount(countTimeouts);
                        }
                    }
                    break;
            }
        }
    }

    public static class StatusChangedEvent {
        public final Status status;

        public StatusChangedEvent(Status status) {
            this.status = status;
        }
    }
}
