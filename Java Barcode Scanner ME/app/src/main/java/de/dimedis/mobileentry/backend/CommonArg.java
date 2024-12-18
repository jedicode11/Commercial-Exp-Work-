package de.dimedis.mobileentry.backend;

import android.content.Intent;

import de.dimedis.mobileentry.ConfigPref;
import de.dimedis.mobileentry.MobileEntryApplication;
import de.dimedis.mobileentry.util.ConfigPrefHelper;

public class CommonArg extends BaseArg implements CommonArgImpl {
    private String userSession;
    private String userSuid;
    private String userName;
    private String fair;
    private String border;

    public CommonArg() {
    }

    public CommonArg(Intent intent) {
        super(intent);
        this.intent = intent;
    }

    @Override
    public CommonArg getParamFromIntent() {
        super.getParamFromIntent();
        setUserSession(intent.getStringExtra(BackendService.EXTRA_USER_SESSION));
        setUserSuid(intent.getStringExtra(BackendService.EXTRA_USER_SUID));
        setUserName(intent.getStringExtra(BackendService.EXTRA_USER_NAME));
        setFair(intent.getStringExtra(BackendService.EXTRA_FAIR));
        setBorder(intent.getStringExtra(BackendService.EXTRA_BORDER));
        return this;
    }

    public static void setArgToIntent(Intent intent) {

        ConfigPref pref = MobileEntryApplication.getConfigPreferences();
        BaseArg.setArgToIntent(intent);

        intent.putExtra(BackendService.EXTRA_USER_SESSION, pref.userSession());
        intent.putExtra(BackendService.EXTRA_USER_SUID, pref.userSuid());
        intent.putExtra(BackendService.EXTRA_USER_NAME, pref.userName());

        if (null != ConfigPrefHelper.getUsersBorder()) {
            intent.putExtra(BackendService.EXTRA_FAIR, ConfigPrefHelper.getUsersBorder().getFair());// fair
            intent.putExtra(BackendService.EXTRA_BORDER, ConfigPrefHelper.getUsersBorder().getBorder());//border
        } else {
            intent.putExtra(BackendService.EXTRA_FAIR, "");// fair
            intent.putExtra(BackendService.EXTRA_BORDER, "");//border
        }
    }

    public static CommonArg fromPreferences() {
        CommonArg commonArg = new CommonArg();
        ConfigPref pref = MobileEntryApplication.getConfigPreferences();

        String lang = pref.currentLanguage();
        String commKey = pref.commKey();
        String deviceSuid = pref.deviceSuid();
        String deviceType = pref.deviceType();
        String userSession = pref.userSession();
        String userSuid = pref.userSuid();
        String userName = pref.userName();
        String fair = ConfigPrefHelper.getUsersBorder().getFair();
        String border = ConfigPrefHelper.getUsersBorder().getBorder();

        commonArg.setLang(lang);
        commonArg.setCommKey(commKey);
        commonArg.setDeviceSuid(deviceSuid);
        commonArg.setDeviceType(deviceType);
        commonArg.setUserSession(userSession);
        commonArg.setUserSuid(userSuid);
        commonArg.setUserName(userName);
        commonArg.setFair(fair);
        commonArg.setBorder(border);

        return commonArg;
    }

    public String getUserSession() {
        return userSession;
    }

    public void setUserSession(String userSession) {
        this.userSession = userSession;
    }

    public String getUserSuid() {
        return userSuid;
    }

    public void setUserSuid(String userSuid) {
        this.userSuid = userSuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFair() {
        return fair;
    }

    public void setFair(String fair) {
        this.fair = fair;
    }

    public String getBorder() {
        return border;
    }

    public void setBorder(String border) {
        this.border = border;
    }
}
