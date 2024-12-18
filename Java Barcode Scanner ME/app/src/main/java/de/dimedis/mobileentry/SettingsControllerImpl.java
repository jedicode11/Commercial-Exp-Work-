package de.dimedis.mobileentry;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import de.dimedis.mobileentry.backend.BackendService;
import de.dimedis.mobileentry.backend.response.DownloadLibrary;
import de.dimedis.mobileentry.backend.response.ResetDeviceResponse;
import de.dimedis.mobileentry.backend.response.UserLogoutResponse;
import de.dimedis.mobileentry.backend.response.Versions;
import de.dimedis.mobileentry.fragments.menus.LogoutMenuFragment;
import de.dimedis.mobileentry.fragments.util.AlertFragment;
import de.dimedis.mobileentry.util.ConfigPrefHelper;
import de.dimedis.mobileentry.util.LogType;
import de.dimedis.mobileentry.util.Logger;
import de.dimedis.mobileentry.util.PrefUtils;
import de.dimedis.mobileentry.util.SessionUtils;
import de.dimedis.mobileentry.util.UpdateUtil;

public class SettingsControllerImpl implements SettingsController {
    private static final String TAG = "Settings";
    public static final int BRIGHTNESS_MAX = 100;
    private static final String KEY_BRIGHTNESS = "brightness";
    private static final String KEY_VIBRATION = "vibration";
    private static final String KEY_SOUND = "sound";
    private static final String KEY_LOCAL_SCAN = "local_scan";
    private static final String KEY_WRITE_LOGFILE = "write_logfile";
    public static final String KEY_CAM_READER_ENABLED = "KEY_CAM_READER_ENABLED";
    public static final String KEY_PERM_SCAN_MODE = "KEY_PERM_SCAN_MODE";
    private FragmentActivity mActivity;
    private Bundle mValues = new Bundle();
    private Bundle mTemporary = new Bundle();
    public static final int REQUEST = 113;
    public static final int STORAGE_REQUEST = 114;

    public SettingsControllerImpl(FragmentActivity activity) {
        mActivity = activity;
    }

    public static SettingsController getDefault(FragmentActivity activity) {
        return ((SettingsControllerContainer) activity).getSettingsController();
    }

    private void load() {
        setLocalScan(PrefUtils.isLocalScanEnabled());
        setVibration(PrefUtils.isVibrationEnabled());
        setBrightness(PrefUtils.getBrightness(getBrightnessMax() / 2));
        setSound(PrefUtils.getSoundValue(getSoundMax()));
        setWriteLogfile(PrefUtils.isWriteLogfile());
        setCamReader(PrefUtils.getCameraReaderValue());
        setPermScanMode(PrefUtils.getPermScanModeValue());
    }

    @Override
    public void update(Context context) {
        Versions versions = ConfigPrefHelper.getVersions();
        Versions versionsServer = ConfigPrefHelper.getVersionsFromServer();
        boolean isNeedUpdateApp = versionsServer != null && !versionsServer.getApp().equalsIgnoreCase(versions.getApp());
        boolean isNeedUpdateLibrary = versionsServer != null && !versionsServer.getLibrary().equalsIgnoreCase(versions.getLibrary());
        if (isNeedUpdateApp) {
            String[] PERMISSIONS = {WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(context, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, REQUEST);
            } else {
                UpdateUtil.loadApp();
            }
        } else if (isNeedUpdateLibrary) {
            String[] PERMISSIONS = {WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(context, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, REQUEST);
            } else {
                BackendService.downloadLibrary();
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void save() {
        PrefUtils.setLocalScanEnabled(isLocalScan());
        PrefUtils.setVibrationEnabled(isVibration());
        PrefUtils.setWriteLogfile(isWriteLogfile());
        PrefUtils.setBrightness(getBrightness());
        PrefUtils.setSoundValue(getSound());
        PrefUtils.setCameraReaderValue(isCameraReaderAvailable());
        PrefUtils.setPermScannerMode(isPermScanMode());
        saveTemporary();
        try {
            Logger.asyncLog("Saving new Settings", String.format(Locale.GERMANY, "LocalScanEnabled: %s; VibrationEnabled: %s; WriteLogfile: %s; Brightness: %d; Sound: %d;", isLocalScan() + "", isVibration() + "", isWriteLogfile() + "", getBrightness(), getSound()), LogType.INFO);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean isCameraReaderAvailable() {
        return mValues.getBoolean(KEY_CAM_READER_ENABLED);
    }

    @Override
    public void switchToPhone() {
        mActivity.startActivity(new Intent(Intent.ACTION_DIAL));
    }

    @Override
    public void exit() {
        SessionUtils.logout(false);
        mActivity.finishAffinity();
    }

    @Override
    public void logout(boolean forceOfflineLogout, boolean shouldResetDeviceState, boolean isSessionInvalid) {
        SessionUtils.logout(forceOfflineLogout, shouldResetDeviceState, isSessionInvalid);
    }

    @Override
    public void reset(boolean forceOfflineLogout, boolean shouldResetDeviceState, boolean isSessionInvalid) {
        SessionUtils.logout(forceOfflineLogout, shouldResetDeviceState, isSessionInvalid);
        SessionUtils.resetDeviceLocalState(mActivity.getBaseContext());
        BackendService.resetDevice();
    }

    @Override
    public void setBrightness(int v) {
        mValues.putInt(KEY_BRIGHTNESS, v);
        v = Math.min(v, BRIGHTNESS_MAX);
        Window window = mActivity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = v / (float) BRIGHTNESS_MAX;
        window.setAttributes(lp);
    }

    @Override
    public int getBrightness() {
        return mValues.getInt(KEY_BRIGHTNESS);
    }

    @Override
    public void setVibration(boolean value) {
        mValues.putBoolean(KEY_VIBRATION, value);
        PrefUtils.setVibrationEnabled(value);
        AudioManager audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        audioManager.getRingerMode();
        audioManager.getRingerMode();
    }

    @Override
    public void setSound(int volume) {

        mValues.putInt(KEY_SOUND, volume);
        AudioManager audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        boolean isVolumeFixed = audioManager.isVolumeFixed();
        Log.i(TAG, "isVolumeFixed: " + isVolumeFixed + " max:" + max + " volume:" + volume);
        Log.i(TAG, " max:" + max + " volume:" + volume);
        volume = Math.min(volume, max);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    @Override
    public int getSound() {
        return mValues.getInt(KEY_SOUND);
    }

    @Override
    public void saveTemporary() {
        mTemporary.putAll(mValues);
    }

    @Override
    public void loadTemporary() {
        mTemporary.putAll(mTemporary);
        setBrightness(mTemporary.getInt(KEY_BRIGHTNESS));
        setVibration(mTemporary.getBoolean(KEY_VIBRATION));
        setSound(mTemporary.getInt(KEY_SOUND));
    }

    @Override
    public int getSoundMax() {
        AudioManager audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public int getBrightnessMax() {
        return BRIGHTNESS_MAX;
    }

    @Override
    public boolean isLocalScan() {
        return mValues.getBoolean(KEY_LOCAL_SCAN);
    }

    @Override
    public void setLocalScan(boolean value) {
        mValues.putBoolean(KEY_LOCAL_SCAN, value);
    }

    @Override
    public boolean isWriteLogfile() {
        return mValues.getBoolean(KEY_WRITE_LOGFILE);
    }

    @Override
    public void setWriteLogfile(boolean value) {
        mValues.putBoolean(KEY_WRITE_LOGFILE, value);
    }

    @Override
    public boolean isCamReader() {
        return mValues.getBoolean(KEY_CAM_READER_ENABLED);
    }

    @Override
    public void setCamReader(boolean isChecked) {
        mValues.putBoolean(KEY_CAM_READER_ENABLED, isChecked);
    }

    @Override
    public boolean isPermScanMode() {
        return mValues.getBoolean(KEY_PERM_SCAN_MODE);
    }

    @Override
    public void setPermScanMode(boolean isChecked) {
        mValues.putBoolean(KEY_PERM_SCAN_MODE, isChecked);
    }

    @Override
    public boolean isVibration() {
        return mValues.getBoolean(KEY_VIBRATION);
    }

    public void onStart() {
        EventBus.getDefault().register(this);
        load();
    }

    public void onStop() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UserLogoutResponse event) {
        if (event.error != null && event.error.code.equals(BackendService.ERROR_CODE_COMM_KEY_INVALID)) {
            PrefUtils.setLoginCompleted(true);
            AlertFragment.show(event.error, null, mActivity.getSupportFragmentManager());
            Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag("LogoutMenuFragment");
            if (null != fragment && fragment instanceof LogoutMenuFragment) {
                ((LogoutMenuFragment) fragment).setVisibilityProgressBar(false);
            }
            return;
        }
        Log.d(TAG, "UserLogoutResponse: " + event);
        if (isLogoutResponseOK(event)) {
            SessionUtils.doUserLogout(SessionUtils.sDeviceIsGoingToBeReset, mActivity);
        } else {
            logout(true, SessionUtils.sDeviceIsGoingToBeReset, false);
        }
    }

    private boolean isLogoutResponseOK(UserLogoutResponse event) {
        return event.isResultOk() && (event.content.isStatusSuccess() || TextUtils.equals(event.content.status, BackendService.STATUS_SESSION_ALREADY_CLOSED));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ResetDeviceResponse event) {
        Log.d(TAG, "RESET DEVICE RESPONSE: " + event);
        SessionUtils.resetDeviceLocalState(mActivity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DownloadLibrary event) {
        if (event.libFile != null) {
            //...
        }
    }
}