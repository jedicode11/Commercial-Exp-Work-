package de.dimedis.mobileentry.model

import android.util.Log
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.util.CommonUtils
import de.dimedis.mobileentry.util.DataBaseUtil
import de.dimedis.mobileentry.util.Logger
import de.dimedis.mobileentry.util.PrefUtils
import de.dimedis.mobileentry.util.dynamicres.DynamicString
import org.greenrobot.eventbus.EventBus

class StatusManager private constructor() {
    enum class Status {
        OFFLINE, LOCAL_SCAN, ONLINE;

        fun getText(status: Status?): String? {
            return when (status) {
                OFFLINE -> DynamicString.instance?.getString(R.string.SCAN_STATUS_OFFLINE)
                LOCAL_SCAN -> DynamicString.instance?.getString(R.string.SCAN_STATUS_LOCAL)
                ONLINE -> DynamicString.instance?.getString(R.string.SCAN_STATUS_ONLINE)
                else -> null
            }
        }
    }

    private var mStatus: Status

    init {
        mStatus = checkStatus()
    }

    fun getStatus(): Status {
        return mStatus
    }

    fun isOnline(): Boolean {
        return mStatus == Status.ONLINE
    }

    fun updateStatus() {
        val status: Status = checkStatus()
        setStatus(status)
    }

    fun setStatus(status: Status) {
        if (mStatus != status) {
            Logger.i(TAG, "Status changed to $status")
            mStatus = status
            EventBus.getDefault().post(StatusChangedEvent(status))
            if (status == Status.ONLINE) {
                DataBaseUtil.uploadCachedTickets()
                Log.w(TAG, "ONLINE isLoginCompleted:" + PrefUtils.isLoginCompleted())
                DataBaseUtil.uploadCachedOfflineSessions()
            }
        }
    }

    class StatusChangedEvent(val status: Status)
    companion object {
        private val TAG = StatusManager::class.java.simpleName

        @Volatile
        private var sInstance: StatusManager? = null
        fun getInstance(): StatusManager? {
            if (sInstance == null) {
                synchronized(StatusManager::class.java) {
                    if (sInstance == null) {
                        sInstance = StatusManager()
                    }
                }
            }
            return sInstance
        }

        private fun checkStatus(): Status {
            if (PrefUtils.isLocalScanEnabled()) {
                return Status.LOCAL_SCAN
            }
            return if (CommonUtils.isInternetConnected()) Status.ONLINE else Status.OFFLINE
        }
    }
}