package de.dimedis.mobileentry.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.text.TextUtils;

import androidx.preference.PreferenceManager;

import java.io.File;

import de.dimedis.mobileentry.MobileEntryApplication;
import de.dimedis.mobileentry.SettingsControllerImpl;
import de.dimedis.mobileentry.model.LocalModeHelper;
import de.dimedis.mobileentry.model.StatusManager;

public class PrefUtils {
    public static final String PREF_RPC_URL = "rpc_url";
    public static final String PREF_ORIGINAL_HOME_PACKAGE = "original_home_package";
    public static final String PREF_IS_FIRST_LAUNCH = "is_first_launch";
    public static final String PREF_IS_INIT_COMPLETED = "is_init_completed";
    public static final String PREF_IS_LOGIN_COMPLETED = "is_login_completed";
    public static final String PREF_HEARTBEAT_INTERVAL_IDLE = "heartbeat_interval_idle";
    public static final String PREF_HEARTBEAT_INTERVAL_ON_DUTY = "heartbeat_interval_on_duty";
    public static final String PREF_OFFLINE_DETECT_TIMEOUT = "offline_detect_timeout";
    public static final String PREF_OFFLINE_DETECT_COUNT = "offline_detect_count";
    public static final String PREF_SCAN_DOUBLE_SCAN_DELAY = "scan_doublescan_delay";
    public static final String PREF_SCAN_OK_SWITCH_DELAY = "scan_ok_switch_delay";
    public static final String PREF_SCAN_DENIED_SWITCH_DELAY = "scan_denied_switch_delay";
    public static final String PREF_SCAN_CANCEL_TIMEOUT = "scan_cancel_timeout";
    public static final String PREF_BORDER_NAME = "border_name";
    public static final String PREF_BRIGHTNESS = "pref_brightness";
    public static final String PREF_SOUND_VALUE = "pref_sound_value";
    public static final String PREF_WRITE_LOGFILE = "pref_write_logfile";
    public static final String PREF_IS_LOCAL_SCAN_ENABLED = "is_local_scan_enabled";
    public static final String PREF_IS_VIBRATION_ENABLED = "is_vibration_enabled";
    private static final String PREF_LIB_VERSION = "PREF_LIB_VERSION";

    SharedPreferences mSharedPreferences;

    private PrefUtils() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(AppContext.get());
    }

    static PrefUtils _PrefUtils;

    public static void init() {
        _PrefUtils = new PrefUtils();
    }

    private static SharedPreferences getPrefs() {
        return _PrefUtils.mSharedPreferences;//PreferenceManager.getDefaultSharedPreferences(AppContext.get());
    }

    public static void registerOnPrefChangeListener(OnSharedPreferenceChangeListener listener) {
        getPrefs().registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterOnPrefChangeListener(OnSharedPreferenceChangeListener listener) {
        getPrefs().unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static String getRpcUrl() {
        return getPrefs().getString(PREF_RPC_URL, "");
    }

    public static void setRpcUrl(String url) {
        getPrefs().edit().putString(PREF_RPC_URL, url).apply();
    }

    public static String getOriginalHomePackage() {
        return getPrefs().getString(PREF_ORIGINAL_HOME_PACKAGE, null);
    }

    public static void setOriginalHomePackage(String packageName) {
        getPrefs().edit().putString(PREF_ORIGINAL_HOME_PACKAGE, packageName).apply();
    }

    public static boolean isFirstLaunch() {
        return getPrefs().getBoolean(PREF_IS_FIRST_LAUNCH, true);
    }

    public static void resetFirstLaunch() {
        getPrefs().edit().putBoolean(PREF_IS_FIRST_LAUNCH, false).apply();
    }

    public static boolean isInitCompleted() {
        return getPrefs().getBoolean(PREF_IS_INIT_COMPLETED, false);
    }

    public static void setInitCompleted(boolean isCompleted) {
        getPrefs().edit().putBoolean(PREF_IS_INIT_COMPLETED, isCompleted).apply();
    }

    public static boolean isLoginCompleted() {
        return getPrefs().getBoolean(PREF_IS_LOGIN_COMPLETED, false);
    }

    public static void setLoginCompleted(boolean isCompleted) {
        getPrefs().edit().putBoolean(PREF_IS_LOGIN_COMPLETED, isCompleted).apply();
    }

    public static int getHeartbeatIntervalIdle() {
        return getPrefs().getInt(PREF_HEARTBEAT_INTERVAL_IDLE, 300);  // seconds
    }

    public static void setHeartbeatIntervalIdle(int interval) {
        getPrefs().edit().putInt(PREF_HEARTBEAT_INTERVAL_IDLE, interval).apply();
    }

    public static int getHeartbeatIntervalOnDuty() {
        return getPrefs().getInt(PREF_HEARTBEAT_INTERVAL_ON_DUTY, 1800);  // seconds
    }

    public static void setHeartbeatIntervalOnDuty(int interval) {
        getPrefs().edit().putInt(PREF_HEARTBEAT_INTERVAL_ON_DUTY, interval).apply();
    }

    public static int getOfflineDetectTimeout() {
        return getPrefs().getInt(PREF_OFFLINE_DETECT_TIMEOUT, 2500);  // milliseconds
    }

    public static void setOfflineDetectTimeout(int timeout) {
        getPrefs().edit().putInt(PREF_OFFLINE_DETECT_TIMEOUT, timeout).apply();
    }

    public static int getOfflineDetectCount() {
        return getPrefs().getInt(PREF_OFFLINE_DETECT_COUNT, 555); // was 5
    }

    public static void setOfflineDetectCount(int count) {
        getPrefs().edit().putInt(PREF_OFFLINE_DETECT_COUNT, count).apply();
    }

    public static int getScanOkSwitchDelay() {
        return getPrefs().getInt(PREF_SCAN_OK_SWITCH_DELAY, 1500);  // milliseconds
    }

    public static void setScanOkSwitchDelay(int delay) {
        getPrefs().edit().putInt(PREF_SCAN_OK_SWITCH_DELAY, delay).apply();
    }

    public static int getScanDeniedSwitchDelay() {
        return getPrefs().getInt(PREF_SCAN_DENIED_SWITCH_DELAY, 3000);  // milliseconds
    }

    public static void setScanDeniedSwitchDelay(int delay) {
        getPrefs().edit().putInt(PREF_SCAN_DENIED_SWITCH_DELAY, delay).apply();
    }

    public static int getScanDoubleDelay() {
        return getPrefs().getInt(PREF_SCAN_DOUBLE_SCAN_DELAY, 2000);  // milliseconds
    }

    public static void setScanDoubleDelay(int delay) {
        getPrefs().edit().putInt(PREF_SCAN_DOUBLE_SCAN_DELAY, delay).apply();
    }

    public static int getScanCancelTimeout() {
        return getPrefs().getInt(PREF_SCAN_CANCEL_TIMEOUT, 5000);  // milliseconds
    }

    public static void setScanCancelTimeout(int timeout) {
        getPrefs().edit().putInt(PREF_SCAN_CANCEL_TIMEOUT, timeout).apply();
    }

    public static String getBorderName() {
        return getPrefs().getString(PREF_BORDER_NAME, null);
    }

    public static void setBorderName(String name) {
        getPrefs().edit().putString(PREF_BORDER_NAME, name).apply();
    }

    public static boolean isLocalScanEnabled() {
        return getPrefs().getBoolean(PREF_IS_LOCAL_SCAN_ENABLED, false);
    }

    public static void setLocalScanEnabled(boolean isEnabled) {
        getPrefs().edit().putBoolean(PREF_IS_LOCAL_SCAN_ENABLED, isEnabled).apply();
        StatusManager.getInstance().updateStatus();
    }

    public static boolean isVibrationEnabled() {
        return getPrefs().getBoolean(PREF_IS_VIBRATION_ENABLED, true);
    }

    public static void setVibrationEnabled(boolean isEnabled) {
        getPrefs().edit().putBoolean(PREF_IS_VIBRATION_ENABLED, isEnabled).apply();
    }


    public static void setWriteLogfile(boolean writeLogfile) {
        getPrefs().edit().putBoolean(PREF_WRITE_LOGFILE, writeLogfile).apply();
    }

    public static void setBrightness(int brightness) {
        getPrefs().edit().putInt(PREF_BRIGHTNESS, brightness).apply();
    }

    public static void setSoundValue(int soundValue) {
        getPrefs().edit().putInt(PREF_SOUND_VALUE, soundValue).apply();
    }

    public static int getBrightness(int defaultBrightness) {
        return getPrefs().getInt(PREF_BRIGHTNESS, defaultBrightness);
    }


    public static int getSoundValue(int defaultVolume) {
        return getPrefs().getInt(PREF_SOUND_VALUE, defaultVolume);
    }

    public static boolean isWriteLogfile() {
        return getPrefs().getBoolean(PREF_WRITE_LOGFILE, true);
    }

    public static void clear() {
        getPrefs().edit().commit();
    }

    public static void setCameraReaderValue(boolean cameraReaderAvailable) {
        getPrefs().edit().putBoolean(SettingsControllerImpl.KEY_CAM_READER_ENABLED, cameraReaderAvailable).apply();
    }

    public static boolean getCameraReaderValue() {
        return getPrefs().getBoolean(SettingsControllerImpl.KEY_CAM_READER_ENABLED, !CommonUtils.hasBBApi());
    }

    public static boolean getPermScanModeValue() {
        return getPrefs().getBoolean(SettingsControllerImpl.KEY_PERM_SCAN_MODE, false);
    }

    public static void setPermScannerMode(boolean permScanMode) {
        getPrefs().edit().putBoolean(SettingsControllerImpl.KEY_PERM_SCAN_MODE, permScanMode).apply();
    }

    public static String getLibraryVersion() {
        String version = getPrefs().getString(PREF_LIB_VERSION, "0.0.2");
        if (TextUtils.isEmpty(version)) {
            String pathToLib = MobileEntryApplication.getConfigPreferences().lmlibFilePath();
            if (!TextUtils.isEmpty(pathToLib)) {
                version = LocalModeHelper.getLocalLibVersionFromManifestFile(new File(pathToLib));
                if (!TextUtils.isEmpty(version)) {
                    setLibraryVersion(pathToLib);
                    return version;
                }
            }
        }
        return version;
    }

    public static void setLibraryVersion(String fullPathToFile) {
        String version = LocalModeHelper.getLocalLibVersionFromManifestFile(new File(fullPathToFile));
        getPrefs().edit().putString(PREF_LIB_VERSION, version).apply();
    }

    public static void setLibraryVersion(File newFile) {
        setLibraryVersion(newFile.getAbsolutePath());
    }
}
