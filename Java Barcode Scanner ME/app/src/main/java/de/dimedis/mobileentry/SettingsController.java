package de.dimedis.mobileentry;

import android.content.Context;

public interface SettingsController {

    void switchToPhone();

    void exit();

    void logout(boolean forceOfflineLogout, boolean shouldResetDeviceState, boolean isSessionInvalid);

    void reset(boolean forceOfflineLogout, boolean shouldResetDeviceState, boolean isSessionInvalid);

    void update(Context context);

    void setBrightness(int v);

    int getBrightness();

    void setVibration(boolean isOn);

    boolean isVibration();

    void setSound(int volume);

    int getSound();

    void saveTemporary();

    void loadTemporary();

    void save();

    int getSoundMax();

    int getBrightnessMax();

    boolean isLocalScan();

    void setLocalScan(boolean value);

    boolean isWriteLogfile();

    void setWriteLogfile(boolean value);

    boolean isCamReader();

    void setCamReader(boolean isChecked);

    void setPermScanMode(boolean isChecked);

    boolean isPermScanMode();

    public static interface SettingsControllerContainer {
        SettingsController getSettingsController();
    }
}
