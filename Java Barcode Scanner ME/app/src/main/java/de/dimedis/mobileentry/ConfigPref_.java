package de.dimedis.mobileentry;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigPref_ implements ConfigPref {
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public ConfigPref_(Context context) {
        SharedPreferences pref = context.getSharedPreferences("ConfigPref", 0);
        this.pref = pref;
        this.editor = pref.edit();
    }

    @Override
    public String currentLanguage() {
        return pref.getString("currentLanguage", Config.DEFAULT_LANGUAGE);
    }

    public void setCurrentLanguage(String currentLanguage) {
        editor.putString("currentLanguage", currentLanguage);
        editor.commit();
    }

    @Override
    public String customerToken() {
        return pref.getString("customerToken", null);
    }

    public void setCustomerToken(String token) {
        editor.putString("customerToken", token);
        editor.commit();
    }

    @Override
    public String rpcUrl() {
        return pref.getString("rpcUrl", null);
    }

    public void setRpcUrl(String rpcUrl) {
        editor.putString("rpcUrl", rpcUrl);
        editor.commit();
    }

    @Override
    public String languagesContainer() {
        return pref.getString("languagesContainer", null);
    }

    public void setLanguagesContainer(String value) {
        editor.putString("languagesContainer", value);
        editor.commit();
    }

    @Override
    public String languagesContainer2() {
        return pref.getString("languagesContainer2", null);
    }

    public void setLanguagesContainer2(String value) {
        editor.putString("languagesContainer2", value);
        editor.commit();
    }

    @Override
    public String serverName() {
        return pref.getString("serverName", null);
    }

    public void setServerName(String name) {
        editor.putString("serverName", name);
        editor.commit();
    }

    @Override
    public String deviceID() {
        return pref.getString("deviceID", "");
    }

    public void setDeviceID(String deviceID) {
        editor.putString("deviceID", deviceID);
        editor.commit();
    }

    @Override
    public String login() {
        return pref.getString("login", null);
    }

    public void setLogin(String log) {
        editor.putString("login", log);
        editor.commit();
    }

    @Override
    public String passwd() {
        return pref.getString("passwd", null);
    }

    public void setPasswd(String pass) {
        editor.putString("passwd", pass);
        editor.commit();
    }

    @Override
    public String commKey() {
        return pref.getString("commKey", null);
    }

    public void setCommKey(String key) {
        editor.putString("commKey", key);
        editor.commit();
    }

    @Override
    public String deviceSuid() {
        return pref.getString("deviceSuid", null);
    }

    public void setDeviceSuid(String suid) {
        editor.putString("deviceSuid", suid);
        editor.commit();
    }

    @Override
    public String deviceType() {
        return pref.getString("deviceType", null);
    }

    @Override
    public void setDeviceType(String type) {
        editor.putString("deviceType", type);
        editor.commit();
    }

    @Override
    public String loginBarcode() {
        return pref.getString("loginBarcode", null);
    }

    public void setLoginBarcode(String barcode) {
        editor.putString("loginBarcode", barcode);
        editor.commit();
    }

    @Override
    public String userSuid() {
        return pref.getString("userSuid", null);
    }

    public void setUserSuid(String suid) {
        editor.putString("userSuid", suid);
        editor.commit();
    }

    @Override
    public String userName() {
        return pref.getString("userName", null);
    }

    public void setUserName(String name) {
        editor.putString("userName", name);
        editor.commit();
    }

    @Override
    public String userFullName() {
        return pref.getString("userFullName", null);
    }

    public void setUserFullName(String full) {
        editor.putString("userFullName", full);
        editor.commit();
    }

    @Override
    public String userSession() {
        return pref.getString("userSession", null);
    }

    public void setUserSession(String session) {
        editor.putString("userSession", session);
        editor.commit();
    }

    @Override
    public boolean localRecords() {
        return pref.getBoolean("valNew", false);
    }

    public void setLocalRecords(boolean val) {
        editor.putBoolean("localRecords", false);
        editor.commit();
    }

    @Override
    public String app() {
        return pref.getString("app", null);
    }

    public void setApp(String ap) {
        editor.putString("app", ap);
        editor.commit();
    }

    @Override
    public String listUserFunctions() {
        return pref.getString("listUserFunctions", null);
    }

    public void setListUserFunctions(String functions) {
        editor.putString("listUserFunctions", functions);
        editor.commit();
    }

    @Override
    public String userPrefs() {
        return pref.getString("userPrefs", null);
    }

    public void setUserPrefs(String prefs) {
        editor.putString("userPrefs", prefs);
        editor.commit();
    }

    @Override
    public String versionsContainer() {
        return pref.getString("versionsContainer", null);
    }

    public void setVersionsContainer(String version) {
        editor.putString("versionsContainer", version);
        editor.commit();
    }

    @Override
    public String versionsFromServerContainer() {
        return pref.getString("versionsFromServerContainer", null);
    }

    public void setVersionsFromServerContainer(String container) {
        editor.putString("versionsFromServerContainer", container);
        editor.commit();
    }

    @Override
    public String bordersContainer() {
        return pref.getString("bordersContainer", null);
    }

    public void setBordersContainer(String border) {
        editor.putString("bordersContainer", border);
        editor.commit();
    }

    @Override
    public String usersBorder() {
        return pref.getString("usersBorder", null);
    }

    public void setUsersBorder(String value) {
        editor.putString("usersBorder", value);
        editor.commit();
    }

    @Override
    public int progressSound() {
        return pref.getInt("progressSound", 0);
    }

    public void setProgressSound(int progressSound) {
        editor.putInt("progressSound", 0);
        editor.commit();
    }

    @Override
    public int progressBrightness() {
        return pref.getInt("progressBrightness", 0);
    }

    public void setProgressBrightness(int prog) {
        editor.putInt("progressBrightness", 0);
        editor.commit();
    }

    @Override
    public boolean isUpdatesAvailable() {
        return pref.getBoolean("isUpdatesAvailable", false);
    }

    public void setIsUpdatesAvailable(boolean available) {
        editor.putBoolean("isUpdatesAvailable", false);
        editor.commit();
    }

    @Override
    public int heartbeatInterval() {
        return pref.getInt("heartbeatInterval", 0);
    }

    public void setHeartbeatInterval(int beat) {
        editor.putInt("heartbeatInterval", 0);
        editor.commit();
    }

    @Override
    public String lmlibFilePathOld() {
        return pref.getString("lmlibFilePathOld", null);
    }

    public void setLmlibFilePathOld(String path) {
        editor.putString("lmlibFilePathOld", path);
        editor.commit();
    }

    @Override
    public String lmlibFilePath() {
        return pref.getString("lmlibFilePath", null);
    }

    public void setLmlibFilePath(String path) {
        editor.putString("lmlibFilePath", path);
        editor.commit();
    }

    @Override
    public String offlineConfig() {
        return pref.getString("offlineConfig", null);
    }

    public void setOfflineConfig(String config) {
        editor.putString("offlineConfig", config);
        editor.commit();
    }

    @Override
    public String offlineConfigServer() {
        return pref.getString("offlineConfigServer", null);
    }

    public void setOfflineConfigServer(String server) {
        editor.putString("offlineConfigServer", server);
        editor.commit();
    }

    @Override
    public boolean isBordersInit() {
        return pref.getBoolean("isBordersInit", false);
    }

    public void setIsBordersInit(boolean init) {
        editor.putBoolean("isBordersInit", false);
        editor.commit();
    }

    @Override
    public boolean isLanguagesInit() {
        return pref.getBoolean("isLanguagesInit", false);
    }

    public void setIsLanguagesInit(boolean init) {
        editor.putBoolean("isLanguagesInit", false);
        editor.commit();
    }
}
