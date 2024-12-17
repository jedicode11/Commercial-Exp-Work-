package de.dimedis.mobileentry

import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import de.dimedis.mobileentry.backend.BackendServiceConst
import de.dimedis.mobileentry.backend.BackendServiceUtil
import de.dimedis.mobileentry.backend.response.DownloadLibrary
import de.dimedis.mobileentry.backend.response.ResetDeviceResponse
import de.dimedis.mobileentry.backend.response.UserLogoutResponse
import de.dimedis.mobileentry.fragments.menus.LogoutMenuFragment
import de.dimedis.mobileentry.fragments.util.AlertFragment
import de.dimedis.mobileentry.util.*
import de.dimedis.mobileentry.util.SessionUtils.logout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class SettingsController(private val mActivity: FragmentActivity) {
    private val mValues = Bundle()
    private val mTemporary = Bundle()

    interface SettingsControllerContainer {
        fun getSettingsController(): SettingsController?
    }

    fun getDefault(activity: FragmentActivity): SettingsController? {
        return (activity as SettingsControllerContainer).getSettingsController()
    }

    private fun load() {
        localScan = PrefUtils.isLocalScanEnabled()
        vibration = PrefUtils.isVibrationEnabled()
        brightness = PrefUtils.getBrightness(brightnessMax / 2)
        sound = PrefUtils.getSoundValue(soundMax)
        writeLogfile = PrefUtils.isWriteLogfile()
        camReader = PrefUtils.getCameraReaderValue()
        permScanMode = PrefUtils.getPermScanModeValue()
    }

    fun update(context: Context?) {
        val versions = ConfigPrefHelper.getVersions()
        val versionsServer = ConfigPrefHelper.getVersionsFromServer()
        val isNeedUpdateApp = !versionsServer.app.equals(versions.app, ignoreCase = true)
        val isNeedUpdateLibrary =
            !versionsServer.library.equals(versions.library, ignoreCase = true)
        if (isNeedUpdateApp) {
            val PERMISSIONS = arrayOf(permission.WRITE_EXTERNAL_STORAGE)
            if (!hasPermissions(context, *PERMISSIONS)) {
                ActivityCompat.requestPermissions((context as Activity?)!!, PERMISSIONS, REQUEST)
            } else {
                UpdateUtil.loadApp()
            }
        } else if (isNeedUpdateLibrary) {
            val PERMISSIONS = arrayOf(permission.WRITE_EXTERNAL_STORAGE)
            if (!hasPermissions(context, *PERMISSIONS)) {
                ActivityCompat.requestPermissions((context as Activity?)!!, PERMISSIONS, REQUEST)
            } else {
                BackendServiceUtil.downloadLibrary() // Was BackendService
            }
        }
    }

    fun save() {
        PrefUtils.setLocalScanEnabled(localScan)
        PrefUtils.setVibrationEnabled(vibration)
        PrefUtils.setWriteLogfile(writeLogfile)
        PrefUtils.setBrightness(brightness)
        PrefUtils.setSoundValue(sound)
        PrefUtils.setCameraReaderValue(isCameraReaderAvailable)
        PrefUtils.setPermScannerMode(permScanMode)
        saveTemporary()
        try {
            Logger.asyncLog("Saving new Settings", String.format(Locale.GERMANY, "LocalScanEnabled: %s; " + "VibrationEnabled: %s; WriteLogfile: %s; Brightness: %d; Sound: %d;",
                localScan.toString() + "", vibration.toString() + "", writeLogfile.toString() + "", brightness, sound), LogType.INFO)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    val isCameraReaderAvailable: Boolean
    get() = mValues.getBoolean(KEY_CAM_READER_ENABLED)

    val switchToPhone
    get() = mActivity.startActivity(Intent(Intent.ACTION_DIAL))

    fun exit() {
        logout(false)
        mActivity.finishAffinity()
    }

    fun logout(forceOfflineLogout: Boolean, shouldResetDeviceState: Boolean, isSessionInvalid: Boolean) {
        SessionUtils.logout(forceOfflineLogout, shouldResetDeviceState, isSessionInvalid)
    }

    fun reset(forceOfflineLogout: Boolean, shouldResetDeviceState: Boolean, isSessionInvalid: Boolean) {
        SessionUtils.logout(forceOfflineLogout, shouldResetDeviceState, isSessionInvalid)
    }

    private fun changeScreenBrightness(context: Context, screenBrightnessValue: Int) {
        Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
        Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, screenBrightnessValue)
    }

    var brightness: Int
    get() = mValues.getInt(KEY_BRIGHTNESS)
    set(v) {
        var v = v
        mValues.putInt(KEY_BRIGHTNESS, v)
        v = v.coerceAtMost(BRIGHTNESS_MAX)
        val window = mActivity.window
        val lp = window.attributes
        lp.screenBrightness = v / BRIGHTNESS_MAX.toFloat()
        window.attributes = lp
    }

    var sound: Int
        get() = mValues.getInt(KEY_SOUND)
        set(volume) {
            var volume = volume
            mValues.putInt(KEY_SOUND, volume)
            val audioManager = mActivity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val isVolumeFixed = audioManager.isVolumeFixed
            Log.i(TAG, "isVolumeFixed: $isVolumeFixed max:$max volume:$volume")
            Log.i(TAG, " max:$max volume:$volume")
            volume = volume.coerceAtMost(max)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
        }


    fun saveTemporary() {
        mTemporary.putAll(mValues)
    }

    fun loadTemporary() {
        mTemporary.putAll(mTemporary)
        brightness = mTemporary.getInt(KEY_BRIGHTNESS)
        vibration = mTemporary.getBoolean(KEY_VIBRATION)
        sound = mTemporary.getInt(KEY_SOUND)
    }

    val soundMax: Int
        get() {
            val audioManager = mActivity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        }

    val brightnessMax: Int
        get() = BRIGHTNESS_MAX

    var localScan: Boolean
    get() = mValues.getBoolean(KEY_LOCAL_SCAN)
    set(value) = mValues.putBoolean(KEY_LOCAL_SCAN, value)

    var writeLogfile: Boolean
    get() = mValues.getBoolean(KEY_WRITE_LOGFILE)
    set(value) = mValues.putBoolean(KEY_WRITE_LOGFILE, value)


    var camReader: Boolean
    get() = mValues.getBoolean(KEY_CAM_READER_ENABLED)
    set(value) = mValues.putBoolean(KEY_CAM_READER_ENABLED, value)

    var permScanMode: Boolean
    get() = mValues.getBoolean(KEY_PERM_SCAN_MODE)
    set(value) = mValues.putBoolean(KEY_PERM_SCAN_MODE, value)


    var vibration: Boolean
    get() = mValues.getBoolean(KEY_VIBRATION)
    set(value) {
        mValues.putBoolean(KEY_VIBRATION, value)
        PrefUtils.setVibrationEnabled(value)
        val audioManager = mActivity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.ringerMode
        audioManager.ringerMode
    }

    fun onStart() {
        EventBus.getDefault().register(this)
        load()
    }

    fun onStop() {
        EventBus.getDefault().unregister(this)
    }

    @JvmName("onEventMainThread1")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: UserLogoutResponse) {
        if (event.error != null && event.error!!.code == BackendServiceConst.ERROR_CODE_COMM_KEY_INVALID) {
            PrefUtils.setLoginCompleted(true)
            PrefUtils.setKioskModeEnabled(true)
            AlertFragment.show(event.error, null, mActivity.supportFragmentManager)
            val fragment = mActivity.supportFragmentManager.findFragmentByTag("LogoutMenuFragment")
            if (null != fragment && fragment is LogoutMenuFragment) {
                fragment.setVisibilityProgressBar(false)
            }
            return
        }
        Log.d(TAG, "UserLogoutResponse: $event")
        if (isLogoutResponseOK(event)) {
            SessionUtils.doUserLogout(SessionUtils.sDeviceIsGoingToBeReset, mActivity)
        } else {
            logout(true, SessionUtils.sDeviceIsGoingToBeReset, false)
        }
    }

    private fun isLogoutResponseOK(event: UserLogoutResponse): Boolean {
        return event.isResultOk && (event.content?.isStatusSuccess() == true || TextUtils.equals(
            event.content?.status, BackendServiceConst.STATUS_SESSION_ALREADY_CLOSED))
    }

    @JvmName("onEventMainThread1")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: ResetDeviceResponse) {
        Log.d(TAG, "RESET DEVICE RESPONSE: $event")
        SessionUtils.resetDeviceLocalState(mActivity)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: DownloadLibrary) {
        if (event.libFile != null) {
            //...
        }
    }

    companion object {
        private const val TAG = "Settings"
        const val BRIGHTNESS_MAX = 100
        private const val KEY_BRIGHTNESS = "brightness"
        private const val KEY_VIBRATION = "vibration"
        private const val KEY_SOUND = "sound"
        private const val KEY_LOCAL_SCAN = "local_scan"
        private const val KEY_WRITE_LOGFILE = "write_logfile"
        const val KEY_CAM_READER_ENABLED = "KEY_CAM_READER_ENABLED"
        const val KEY_PERM_SCAN_MODE = "KEY_PERM_SCAN_MODE"
        const val REQUEST = 113
        const val STORAGE_REQUEST = 114
        fun getDefault(activity: FragmentActivity): SettingsController {
            return (activity as SettingsControllerContainer).settingsController
        }

        interface SettingsControllerContainer {
            val settingsController: SettingsController
        }

        private fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && context != null && permissions != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        return true
                    }
                }
            }
            return false
        }
    }
}