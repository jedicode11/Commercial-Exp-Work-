package de.dimedis.mobileentry.util

import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import de.dimedis.mobileentry.BuildConfig
import de.dimedis.mobileentry.ConfigPref
import de.dimedis.mobileentry.backend.response.Border
import de.dimedis.mobileentry.backend.response.Localize
import de.dimedis.mobileentry.backend.response.UserPrefs
import de.dimedis.mobileentry.backend.response.Versions

object ConfigPrefHelper {
    fun getLanguages(): Map<String?, Localize?>? {
        val gson = Gson()
        val container: String? = ConfigPref.languagesContainer2
        return gson.fromJson(container, object : TypeToken<Map<String?, Localize?>?>() {}.type)
    }

    fun setLanguages(languages: HashMap<String?, Localize>?) {
        ConfigPref.languagesContainer2 = Gson().toJson(languages)
    }

    fun getVersions(): Versions {
        val versionsContainer: String? = ConfigPref.versionsContainer //versionsFromServerContainer();
        val versionCode = BuildConfig.VERSION_CODE
        val versionName = BuildConfig.VERSION_NAME
        val libVersion = getLibVersion()
        var defaultVersion = Versions()
        if (TextUtils.isEmpty(versionsContainer)) {
            defaultVersion.languages = "0.01"
            defaultVersion.myAvailableBorders = "2015090116:00:54.980"
            defaultVersion.localConfig = "0"
            defaultVersion.settings = "0"
            ConfigPref.versionsContainer = Gson().toJson(defaultVersion) //(version));
        } else {
            defaultVersion = Gson().fromJson(versionsContainer, Versions::class.java)
        }
        defaultVersion.app = String.format(versionName, versionCode)
        defaultVersion.library = libVersion
        return defaultVersion
    }

    private fun getLibVersion(): String? {
        return PrefUtils.getLibraryVersion() // libraryVersion
    }

    fun getVersionsFromServer(): Versions {
        val versionsFromServerContainer: String? = ConfigPref.versionsFromServerContainer
        if (TextUtils.isEmpty(versionsFromServerContainer)) {
            ConfigPref.versionsFromServerContainer = (Gson().toJson(getVersions()))
        }
        return Gson().fromJson(versionsFromServerContainer, Versions::class.java)
    }

    fun isUpdateAvailable(): Boolean {
        return !(getVersions().library == getVersionsFromServer().library && getVersions().app!! >= getVersionsFromServer().app!!)
    }

    fun setVersions(version: Versions?) {
        ConfigPref.versionsContainer //(Gson().toJson(version))
    }

    fun setServerVersions(version: Versions?) {
        /*   version.setLibrary("2.2.0");
        version.setLanguages("2018060114:06:47");
        version.setSettings("2018052823:07:68");
        version.setLocalConfig("2018052823:07:71");
        version.setMyAvailableBorders("2018052823:07:63");*/
        ConfigPref.versionsFromServerContainer = (Gson().toJson(version))
    }

    fun getBorders(): List<Border> {
        return Gson().fromJson(ConfigPref.bordersContainer, object : TypeToken<List<Border?>?>() {}.type)
    }

    fun setBorders(borders: List<Border?>?) {
        ConfigPref.bordersContainer = (Gson().toJson(borders))
    }

    fun getUsersBorder(): Border? {
        try {
            return Gson().fromJson(ConfigPref.usersBorder, Border::class.java)
        } catch (jse: JsonSyntaxException) {
            Log.e("ConfigPrefHelper", "getUsersBorder()", jse)
        }
        return null
    }

    fun setUsersBorder(border: Border) {
        ConfigPref.usersBorder = (Gson().toJson(border))
        PrefUtils.setBorderName(border.borderName)
    }

    fun setUserFunctions(list: List<String?>?) {
        ConfigPref.listUserFunctions = (Gson().toJson(list))
    }

    fun getUserFunctions(): List<String> {
        return Gson().fromJson(ConfigPref.listUserFunctions ?: "[]", object : TypeToken<List<String?>?>() {}.type)
    }

    fun setUserPrefs(userPrefs: UserPrefs?) {
        val newPrefs = Gson().toJson(userPrefs)
        ConfigPref.userPrefs = (newPrefs)
        try {
            Logger.asyncLog("Saving new F-KEYS", String.format("New set of F-KEYS: %s;", newPrefs), LogType.INFO)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getUserPrefs(): UserPrefs {
        return Gson().fromJson(ConfigPref.userPrefs, UserPrefs::class.java)
    }

    fun resetDevice() {
        PrefUtils.setInitCompleted(false)
        PrefUtils.setLoginCompleted(false)
        PrefUtils.setKioskModeEnabled(false)
    }

    fun userLogout() {
        PrefUtils.setLoginCompleted(false)
    }
}