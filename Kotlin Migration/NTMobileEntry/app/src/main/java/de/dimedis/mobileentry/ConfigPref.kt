package de.dimedis.mobileentry

import android.content.SharedPreferences
import de.dimedis.mobileentry.util.AppContext


object ConfigPref {

    var pref: SharedPreferences = AppContext.get().getSharedPreferences("ConfigPref", 0)
    var editor: SharedPreferences.Editor = pref.edit()

    var currentLanguage: String?
        get() = pref.getString("currentLanguage", Config.DEFAULT_LANGUAGE)
        set(value) {
            editor.putString("currentLanguage", value)
            editor.commit()
        }

    var customerToken: String?
        get() = pref.getString("customerToken", null)
        set(value) {
            editor.putString("customerToken", value) // token
            editor.commit()
        }

    var rpcUrl: String?
        get() = pref.getString("rpcUrl", null)
        set(value) {
            editor.putString("rpcUrl", value)
            editor.commit()
        }

    var languages: String?
        get() = pref.getString("languages", null)
        set(value) {
            editor.putString("languagesContainer", value) // languages
            editor.commit()
        }

    var languagesContainer2: String?
        get() = pref.getString("languagesContainer2", null)
        set(value) {
            editor.putString("languagesContainer2", value)
            editor.commit()
        }

    var serverName: String?
        get() = pref.getString("serverName", null)
        set(value) {
            editor.putString("serverName", value)
            editor.commit()
        }

    var deviceID: String?
        get() = pref.getString("deviceID", "")
        set(value) {
            editor.putString("deviceID", value)
            editor.commit()
        }

    var login: String?
        get() = pref.getString("login", null)
        set(value) {
            editor.putString("login", value) // log
            editor.commit()
        }

    var passwd: String?
        get() = pref.getString("passwd", null)
        set(value) {
            editor.putString("passwd", value) // pass
            editor.commit()
        }

    var commKey: String?
        get() = pref.getString("commKey", null)
        set(value) {
            editor.putString("commKey", value)
            editor.commit()
        }

    var deviceSuid: String?
        get() = pref.getString("deviceSuid", null)
        set(value) {
            editor.putString("deviceSuid", value)
            editor.commit()
        }

    var deviceType: String?
        get() = pref.getString("deviceType", null)
        set(value) {
            editor.putString("deviceType", value) //type
            editor.commit()
        }

    var userSession: String?
        get() = pref.getString("userSession", null)
        set(value) {
            editor.putString("userSession", value)
            editor.commit()
        }

    var userSuid: String?
        get() = pref.getString("userSuid", null)
        set(value) {
            editor.putString("userSuid", value) //suid
            editor.commit()
        }

    var loginBarcode: String?
        get() = pref.getString("loginBarcode", null)
        set(value) {
            editor.putString("loginBarcode", value)
            editor.commit()
        }

    var userName: String?
        get() = pref.getString("userName", null)
        set(value) {
            editor.putString("userName", value) //name
            editor.commit()
        }

    var userFullName: String?
        get() = pref.getString("userFullName", null)
        set(value) {
            editor.putString("userFullName", value) //name
            editor.commit()
        }

    var localRecords: Boolean
        get() = pref.getBoolean("localRecords", false)
        set(`val`) {
            editor.putBoolean("localRecords", false)
            editor.commit()
        }

    var app: String?
        get() = pref.getString("app", null)
        set(value) {
            editor.putString("app", value) //app
            editor.commit()
        }

    var listUserFunctions: String?
        get() = pref.getString("listUserFunctions", null)
        set(value) {
            editor.putString("listUserFunctions", value) // functions
            editor.commit()
        }

    var userPrefs: String?
        get() = pref.getString("userPrefs", null)
        set(value) {
            editor.putString("userPrefs", value) // prefs
            editor.commit()
        }

    var versionsContainer: String?
        get() = pref.getString("versionsContainer", null)
        set(value) {
            editor.putString("versionsContainer", value) // version
            editor.commit()
        }

    var versionsFromServerContainer: String?
        get() = pref.getString("versionsFromServerContainer", null)
        set(value) {
            editor.putString("versionsFromServerContainer", value)
            editor.commit()
        }

    var bordersContainer: String?
        get() = pref.getString("bordersContainer", null)
        set(value) {
            editor.putString("bordersContainer", value)
            editor.commit()
        }

    var usersBorder: String?
        get() = pref.getString("usersBorder", null)
        set(value) {
            editor.putString("usersBorder", value)
            editor.commit()
        }

    var progressSound: Int
        get() = pref.getInt("progressSound", 0)
        set(int) {
            editor.putInt("progressSound", 0)
            editor.commit()
        }

    var isUpdatesAvalable: Boolean
        get() = pref.getBoolean("isUpdatesAvalable", false)
        set(available) {
            editor.putBoolean("isUpdatesAvalable", false)
            editor.commit()
        }


    var heartbeatInterval: Int
        get() = pref.getInt("heartbeatInterval", 0)
        set(beat: Int) {
            editor.putInt("heartbeatInterval", 0)
            editor.commit()
        }

    var lmlibFilePathOld: String?
        get() = pref.getString("lmlibFilePathOld", null)
        set(value) {
            editor.putString("lmlibFilePathOld", value)
            editor.commit()
        }

    var lmlibFilePath: String?
        get() = pref.getString("lmlibFilePath", null)
        set(value) {
            editor.putString("lmlibFilePath", value)
            editor.commit()
        }

    var offlineConfig: String?
        get() = pref.getString("offlineConfig", null)
        set(value) {
            editor.putString("offlineConfig", value)
            editor.commit()
        }

    var offlineConfigServer: String?
        get() = pref.getString("offlineConfigServer", null)
        set(value) {
            editor.putString("offlineConfigServer", value) //server
            editor.commit()
        }

    var isBordersInit: Boolean
        get() = pref.getBoolean("isBordersInit", false)
        set(init : Boolean) {
            editor.putBoolean("isBordersInit", false)
            editor.commit()
        }

    var isLanguagesInit: Boolean
        get() = pref.getBoolean("isLanguagesInit", false)
        set(init) {
            editor.putBoolean("isLanguagesInit", false)
            editor.commit()
        }
}