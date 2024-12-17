package de.dimedis.mobileentry.util

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.preference.PreferenceManager
import android.text.TextUtils
import de.dimedis.mobileentry.ConfigPref
import de.dimedis.mobileentry.SettingsController
import de.dimedis.mobileentry.model.LocalModeHelper
import de.dimedis.mobileentry.model.StatusManager
import java.io.File

object PrefUtils {
    var mSharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(AppContext.get())

    const val PREF_RPC_URL = "rpc_url"
    const val PREF_ORIGINAL_HOME_PACKAGE = "original_home_package"
    const val PREF_IS_FIRST_LAUNCH = "is_first_launch"
    const val PREF_IS_INIT_COMPLETED = "is_init_completed"
    const val PREF_IS_LOGIN_COMPLETED = "is_login_completed"
    const val PREF_IS_KIOSK_MODE_ENABLED = "is_kiosk_mode_enabled"
    const val PREF_HEARTBEAT_INTERVAL_IDLE = "heartbeat_interval_idle"
    const val PREF_HEARTBEAT_INTERVAL_ON_DUTY = "heartbeat_interval_on_duty"
    const val PREF_OFFLINE_DETECT_TIMEOUT = "offline_detect_timeout"
    const val PREF_OFFLINE_DETECT_COUNT = "offline_detect_count"
    const val PREF_SCAN_DOUBLE_SCAN_DELAY = "scan_doublescan_delay"
    const val PREF_SCAN_OK_SWITCH_DELAY = "scan_ok_switch_delay"
    const val PREF_SCAN_DENIED_SWITCH_DELAY = "scan_denied_switch_delay"
    const val PREF_SCAN_CANCEL_TIMEOUT = "scan_cancel_timeout"
    const val PREF_BORDER_NAME = "border_name"
    const val PREF_BRIGHTNESS = "pref_brightness"
    const val PREF_SOUND_VALUE = "pref_sound_value"
    const val PREF_WRITE_LOGFILE = "pref_write_logfile"
    const val PREF_IS_LOCAL_SCAN_ENABLED = "is_local_scan_enabled"
    const val PREF_IS_VIBRATION_ENABLED = "is_vibration_enabled"
    private const val PREF_LIB_VERSION = "PREF_LIB_VERSION"


    private fun getPrefs(): SharedPreferences {
        return mSharedPreferences
    }

    fun registerOnPrefChangeListener(listener: OnSharedPreferenceChangeListener?) {
        getPrefs().registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterOnPrefChangeListener(listener: OnSharedPreferenceChangeListener?) {
        getPrefs().unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun getRpcUrl(): String? {
        return getPrefs().getString(PREF_RPC_URL, "")
    }

    fun setRpcUrl(url: String?) {
        getPrefs().edit().putString(PREF_RPC_URL, url).apply()
    }

    fun getOriginalHomePackage(): String? {
        return getPrefs().getString(PREF_ORIGINAL_HOME_PACKAGE, null)
    }

    fun setOriginalHomePackage(packageName: String?) {
        getPrefs().edit().putString(PREF_ORIGINAL_HOME_PACKAGE, packageName).apply()
    }

    fun isFirstLaunch(): Boolean {
        return getPrefs().getBoolean(PREF_IS_FIRST_LAUNCH, true)
    }

    fun resetFirstLaunch() {
        getPrefs().edit().putBoolean(PREF_IS_FIRST_LAUNCH, false).apply()
    }

    fun isInitCompleted(): Boolean {
        return getPrefs().getBoolean(PREF_IS_INIT_COMPLETED, false)
    }

    fun setInitCompleted(isCompleted: Boolean) {
        getPrefs().edit().putBoolean(PREF_IS_INIT_COMPLETED, isCompleted).apply()
    }

    fun isLoginCompleted(): Boolean {
        return getPrefs().getBoolean(PREF_IS_LOGIN_COMPLETED, false)
    }

    fun setLoginCompleted(isCompleted: Boolean) {
        getPrefs().edit().putBoolean(PREF_IS_LOGIN_COMPLETED, isCompleted).apply()
    }

    fun isKioskModeEnabled(): Boolean {
        return getPrefs().getBoolean(PREF_IS_KIOSK_MODE_ENABLED, true)
    }

    fun setKioskModeEnabled(isEnabled: Boolean) {
        getPrefs().edit().putBoolean(PREF_IS_KIOSK_MODE_ENABLED, isEnabled).apply()
    }

    fun getHeartbeatIntervalIdle(): Int {
        return getPrefs().getInt(PREF_HEARTBEAT_INTERVAL_IDLE, 300) // seconds
    }

    fun setHeartbeatIntervalIdle(interval: Int) {
        getPrefs().edit().putInt(PREF_HEARTBEAT_INTERVAL_IDLE, interval).apply()
    }

    fun getHeartbeatIntervalOnDuty(): Int {
        return getPrefs().getInt(PREF_HEARTBEAT_INTERVAL_ON_DUTY, 1800) // seconds
    }

    fun setHeartbeatIntervalOnDuty(interval: Int) {
        getPrefs().edit().putInt(PREF_HEARTBEAT_INTERVAL_ON_DUTY, interval).apply()
    }

    fun getOfflineDetectTimeout(): Int {
        return getPrefs().getInt(PREF_OFFLINE_DETECT_TIMEOUT, 2500) // milliseconds
    }

    fun setOfflineDetectTimeout(timeout: Int) {
        getPrefs().edit().putInt(PREF_OFFLINE_DETECT_TIMEOUT, timeout).apply()
    }

    fun getOfflineDetectCount(): Int {
        return getPrefs().getInt(PREF_OFFLINE_DETECT_COUNT, 5)
    }

    fun setOfflineDetectCount(count: Int) {
        getPrefs().edit().putInt(PREF_OFFLINE_DETECT_COUNT, count).apply()
    }

    fun getScanOkSwitchDelay(): Int {
        return getPrefs().getInt(PREF_SCAN_OK_SWITCH_DELAY, 1500) // milliseconds
    }

    fun setScanOkSwitchDelay(delay: Int) {
        getPrefs().edit().putInt(PREF_SCAN_OK_SWITCH_DELAY, delay).apply()
    }

    fun getScanDeniedSwitchDelay(): Int {
        return getPrefs().getInt(PREF_SCAN_DENIED_SWITCH_DELAY, 3000) // milliseconds
    }

    fun setScanDeniedSwitchDelay(delay: Int) {
        getPrefs().edit().putInt(PREF_SCAN_DENIED_SWITCH_DELAY, delay).apply()
    }

    fun getScanDoubleDelay(): Int {
        return getPrefs().getInt(PREF_SCAN_DOUBLE_SCAN_DELAY, 2000) // milliseconds
    }

    fun setScanDoubleDelay(delay: Int) {
        getPrefs().edit().putInt(PREF_SCAN_DOUBLE_SCAN_DELAY, delay).apply()
    }

    fun getScanCancelTimeout(): Int {
        return getPrefs().getInt(PREF_SCAN_CANCEL_TIMEOUT, 5000) // milliseconds
    }

    fun setScanCancelTimeout(timeout: Int) {
        getPrefs().edit().putInt(PREF_SCAN_CANCEL_TIMEOUT, timeout).apply()
    }

    fun getBorderName(): String? {
        return getPrefs().getString(PREF_BORDER_NAME, null)
    }

    fun setBorderName(name: String?) {
        getPrefs().edit().putString(PREF_BORDER_NAME, name).apply()
    }

    fun isLocalScanEnabled(): Boolean {
        return getPrefs().getBoolean(PREF_IS_LOCAL_SCAN_ENABLED, false)
    }

    fun setLocalScanEnabled(isEnabled: Boolean) {
        getPrefs().edit().putBoolean(PREF_IS_LOCAL_SCAN_ENABLED, isEnabled).apply()
        StatusManager.getInstance()?.updateStatus()
    }

    fun isVibrationEnabled(): Boolean {
        return getPrefs().getBoolean(PREF_IS_VIBRATION_ENABLED, true)
    }

    fun setVibrationEnabled(isEnabled: Boolean) {
        getPrefs().edit().putBoolean(PREF_IS_VIBRATION_ENABLED, isEnabled).apply()
    }

    fun setWriteLogfile(writeLogfile: Boolean) {
        getPrefs().edit().putBoolean(PREF_WRITE_LOGFILE, writeLogfile).apply()
    }

    fun setBrightness(brightness: Int) {
        getPrefs().edit().putInt(PREF_BRIGHTNESS, brightness).apply()
    }

    fun setSoundValue(soundValue: Int) {
        getPrefs().edit().putInt(PREF_SOUND_VALUE, soundValue).apply()
    }

    fun getBrightness(defaultBrightness: Int): Int {
        return getPrefs().getInt(PREF_BRIGHTNESS, defaultBrightness)
    }

    fun getSoundValue(defaultVolume: Int): Int {
        return getPrefs().getInt(PREF_SOUND_VALUE, defaultVolume)
    }

    fun isWriteLogfile(): Boolean {
        return getPrefs().getBoolean(PREF_WRITE_LOGFILE, true)
    }

    fun clear() {
        getPrefs().edit().commit()
    }

    fun setCameraReaderValue(cameraReaderAvailable: Boolean) {
        getPrefs().edit().putBoolean(SettingsController.KEY_CAM_READER_ENABLED, cameraReaderAvailable).apply()
    }

    fun getCameraReaderValue(): Boolean {
        return getPrefs().getBoolean(
            SettingsController.KEY_CAM_READER_ENABLED,
            !CommonUtils.hasBBApi()
        )
    }

    fun getPermScanModeValue(): Boolean {
        return getPrefs().getBoolean(SettingsController.KEY_PERM_SCAN_MODE, false)
    }

    fun setPermScannerMode(permScanMode: Boolean) {
        getPrefs().edit().putBoolean(SettingsController.KEY_PERM_SCAN_MODE, permScanMode).apply()
    }

    fun getLibraryVersion(): String? {
        var version = getPrefs().getString(PREF_LIB_VERSION, "0.0.2")
        if (TextUtils.isEmpty(version)) {
            val pathToLib: String? = ConfigPref.lmlibFilePath
            if (!TextUtils.isEmpty(pathToLib)) {
                version = pathToLib?.let {
                    File(it)
                }?.let { LocalModeHelper.getLocalLibVersionFromManifestFile(it) }
                if (!TextUtils.isEmpty(version)) {
                    setLibraryVersion(pathToLib)
                    return version
                }
            }
        }
        return version
    }

    fun setLibraryVersion(fullPathToFile: String?) {
        val version = fullPathToFile?.let {
            File(it)
        }?.let { LocalModeHelper.getLocalLibVersionFromManifestFile(it) }
        getPrefs().edit().putString(PREF_LIB_VERSION, version).apply()
    }

    fun setLibraryVersion(newFile: File) {
        setLibraryVersion(newFile.absolutePath)
    }

}