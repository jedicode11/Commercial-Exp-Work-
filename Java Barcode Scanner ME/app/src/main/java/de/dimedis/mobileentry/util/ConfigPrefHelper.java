package de.dimedis.mobileentry.util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

import de.dimedis.mobileentry.BuildConfig;
import de.dimedis.mobileentry.ConfigPref;
import de.dimedis.mobileentry.MobileEntryApplication;
import de.dimedis.mobileentry.backend.response.Border;
import de.dimedis.mobileentry.backend.response.Localize;
import de.dimedis.mobileentry.backend.response.UserPrefs;
import de.dimedis.mobileentry.backend.response.Versions;

public class ConfigPrefHelper {

    static public Map<String, Localize> getLanguages() {
        Gson gson = new Gson();
        String container = MobileEntryApplication.getConfigPreferences().languagesContainer2();
        return gson.fromJson(container, new TypeToken<Map<String, Localize>>() {
        }.getType());
    }

    static public void setLanguages(Map<String, Localize> languages) {
        MobileEntryApplication.getConfigPreferences().setLanguagesContainer2(new Gson().toJson(languages));
    }

    static public Versions getVersions() {
        String versionsContainer = MobileEntryApplication.getConfigPreferences().versionsContainer();  //versionsFromServerContainer();
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        String libVersion = ConfigPrefHelper.getLibVersion();

        Versions defaultVersion = new Versions();

        if (TextUtils.isEmpty(versionsContainer)) {
            defaultVersion.setLanguages("0.01");
            defaultVersion.setMyAvailableBorders("2015090116:00:54.980");
            defaultVersion.setLocalConfig("0");
            defaultVersion.setSettings("0");
            MobileEntryApplication.getConfigPreferences().setVersionsContainer(new Gson().toJson(defaultVersion));  //(version));
        } else {
            defaultVersion = new Gson().fromJson(versionsContainer, Versions.class);
        }
        defaultVersion.setApp(String.format("%s (%d)", versionName, versionCode));
        defaultVersion.setLibrary(libVersion);
        return defaultVersion;
    }

    private static String getLibVersion() {
        return PrefUtils.getLibraryVersion();
    }

    static public Versions getVersionsFromServer() {
        String versionsFromServerContainer = MobileEntryApplication.getConfigPreferences().versionsFromServerContainer();
        if (TextUtils.isEmpty(versionsFromServerContainer)) {
            MobileEntryApplication.getConfigPreferences().setVersionsFromServerContainer(new Gson().toJson(getVersions()));
        }
        return new Gson().fromJson(versionsFromServerContainer, Versions.class);
    }
    static public boolean isUpdateAvailable() {
        return !(getVersions().getLibrary().equals(getVersionsFromServer().getLibrary()) && getVersions().getApp().equals(getVersionsFromServer().getApp()));
    }

    static public void setVersions(Versions version) {
        MobileEntryApplication.getConfigPreferences().setVersionsContainer(new Gson().toJson(version));
    }

    static public void setServerVersions(Versions version) {
        MobileEntryApplication.getConfigPreferences().setVersionsFromServerContainer(new Gson().toJson(version));
    }

    static public List<Border> getBorders() {
        return new Gson().fromJson(MobileEntryApplication.getConfigPreferences().bordersContainer(), new TypeToken<List<Border>>() {
        }.getType());
    }

    static public void setBorders(List<Border> borders) {
        MobileEntryApplication.getConfigPreferences().setBordersContainer(new Gson().toJson(borders));
    }

    static public ConfigPref getPref() {
        return MobileEntryApplication.getConfigPreferences();
    }

    static public Border getUsersBorder() {
        try {
            return new Gson().fromJson(MobileEntryApplication.getConfigPreferences().usersBorder(), Border.class);
        } catch (JsonSyntaxException jse) {
            Log.e("ConfigPrefHelper", "getUsersBorder()", jse);
        }
        return null;
    }

    static public void setUsersBorder(Border border) {
        MobileEntryApplication.getConfigPreferences().setUsersBorder(new Gson().toJson(border));
        PrefUtils.setBorderName(border.getBorderName());
    }

    static public void setUserFunctions(List<String> list) {
        MobileEntryApplication.getConfigPreferences().setListUserFunctions(new Gson().toJson(list));
    }

    static public List<String> getUserFunctions() {
        return new Gson().fromJson(MobileEntryApplication.getConfigPreferences().listUserFunctions(), new TypeToken<List<String>>() {
        }.getType());
    }

    static public void setUserPrefs(UserPrefs userPrefs) {
        String newPrefs = new Gson().toJson(userPrefs);
        MobileEntryApplication.getConfigPreferences().setUserPrefs(newPrefs);
        try {
            Logger.asyncLog("Saving new F-KEYS", String.format("New set of F-KEYS: %s;", newPrefs), LogType.INFO);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static public UserPrefs getUserPrefs() {
        return new Gson().fromJson(MobileEntryApplication.getConfigPreferences().userPrefs(), UserPrefs.class);
    }

    static public void resetDevice() {
        PrefUtils.setInitCompleted(false);
        PrefUtils.setLoginCompleted(false);
    }

    static public void userLogout() {
        PrefUtils.setLoginCompleted(false);
    }
}
