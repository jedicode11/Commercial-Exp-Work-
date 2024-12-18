package de.dimedis.mobileentry.model;

import de.dimedis.mobileentry.ConfigPref;
import de.dimedis.mobileentry.MobileEntryApplication;
import de.dimedis.mobileentry.backend.response.Border;
import de.dimedis.mobileentry.util.ConfigPrefHelper;

public class PerfData {

    private String deviceSuid;
    private String userSession;
    private String userSuid;
    private String userName;
    private String lang;

    private String fair;
    private String border;
    private String commKey;


    public PerfData() {
    }

    public PerfData initFromPref() {
        Border border = ConfigPrefHelper.getUsersBorder();
        ConfigPref pref = MobileEntryApplication.getConfigPreferences();
        if (border != null) {
            setFair(border.getFair());
            setBorder(border.getBorder());
        }

        if (pref != null) {
            setDeviceSuid(pref.deviceSuid());
            setUserSession(pref.userSession());
            setUserSuid(pref.userSuid());
            setUserName(pref.userName());
            setLang(pref.currentLanguage());
            setCommKey(pref.commKey());
        }
        return this;
    }


    public String getDeviceSuid() {
        return deviceSuid;
    }

    public void setDeviceSuid(String deviceSuid) {
        this.deviceSuid = deviceSuid;
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

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
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

    public String getCommKey() {
        return commKey;
    }

    public void setCommKey(String commKey) {
        this.commKey = commKey;
    }
}
