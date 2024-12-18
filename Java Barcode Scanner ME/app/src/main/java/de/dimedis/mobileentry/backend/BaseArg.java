package de.dimedis.mobileentry.backend;

import android.content.Intent;

import de.dimedis.mobileentry.ConfigPref;
import de.dimedis.mobileentry.MobileEntryApplication;

public class BaseArg {
    String lang;
    String commKey;
    String deviceSuid;
    String deviceType;

    Intent intent;

    public BaseArg() {
        ConfigPref pref = MobileEntryApplication.getConfigPreferences();
        lang = pref.currentLanguage();
        commKey = pref.commKey();
        deviceSuid = pref.deviceSuid();
    }

    BaseArg(Intent intent) {
        this.intent = intent;
        getParamFromIntent();
    }

    public BaseArg getParamFromIntent() {
        lang = intent.getStringExtra(BackendService.EXTRA_LANG);
        commKey = intent.getStringExtra(BackendService.EXTRA_COMM_KEY);
        deviceSuid = intent.getStringExtra(BackendService.EXTRA_DEVICE_SUID);
        return this;
    }

    public static void setArgToIntent(Intent intent) {
        ConfigPref pref = MobileEntryApplication.getConfigPreferences();

        intent.putExtra(BackendService.EXTRA_LANG, pref.currentLanguage());
        intent.putExtra(BackendService.EXTRA_COMM_KEY, pref.commKey());
        intent.putExtra(BackendService.EXTRA_DEVICE_SUID, pref.deviceSuid());
    }


    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getCommKey() {
        return commKey;
    }

    public void setCommKey(String commKey) {
        this.commKey = commKey;
    }

    public String getDeviceSuid() {
        return deviceSuid;
    }

    public void setDeviceSuid(String deviceSuid) {
        this.deviceSuid = deviceSuid;
    }

    public String getDeviceType() {
        return android.os.Build.MODEL;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
