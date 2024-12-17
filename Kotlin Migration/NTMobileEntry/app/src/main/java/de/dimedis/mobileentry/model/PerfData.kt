package de.dimedis.mobileentry.model

import de.dimedis.mobileentry.ConfigPref
import de.dimedis.mobileentry.util.ConfigPrefHelper

class PerfData {
    var deviceSuid: String? = null
    var userSession: String? = null
    var userSuid: String? = null
    var userName: String? = null
    var lang: String? = null
    var fair: String? = null
    var border: String? = null
    var commKey: String? = null
    fun initFromPref(): PerfData {
        val border = ConfigPrefHelper.getUsersBorder()
        val pref: ConfigPref? = ConfigPref
        if (border != null) {
            setFair(border.fair)
            setBorder(border.border)
        }
        if (pref != null) {
            setDeviceSuid(pref.deviceSuid)
            setUserSession(pref.userSession)
            setUserSuid(pref.userSuid)
            setUserName(pref.userName)
            setLang(pref.currentLanguage)
            setCommKey(pref.commKey)
        }
        return this
    }

    @JvmName("getDeviceSuid1")
    fun getDeviceSuid(): String? {
        return deviceSuid
    }

    @JvmName("setDeviceSuid1")
    fun setDeviceSuid(deviceSuid: String?) {
        this.deviceSuid = deviceSuid
    }

    @JvmName("getUserSession1")
    fun getUserSession(): String? {
        return userSession
    }

    @JvmName("setUserSession1")
    fun setUserSession(userSession: String?) {
        this.userSession = userSession
    }

    @JvmName("getUserSuid1")
    fun getUserSuid(): String? {
        return userSuid
    }

    @JvmName("setUserSuid1")
    fun setUserSuid(userSuid: String?) {
        this.userSuid = userSuid
    }

    @JvmName("getUserName1")
    fun getUserName(): String? {
        return userName
    }

    @JvmName("setUserName1")
    fun setUserName(userName: String?) {
        this.userName = userName
    }

    @JvmName("getLang1")
    fun getLang(): String? {
        return lang
    }

    @JvmName("setLang1")
    fun setLang(lang: String?) {
        this.lang = lang
    }

    @JvmName("getFair1")
    fun getFair(): String? {
        return fair
    }

    @JvmName("setFair1")
    fun setFair(fair: String?) {
        this.fair = fair
    }

    @JvmName("getBorder1")
    fun getBorder(): String? {
        return border
    }

    @JvmName("setBorder1")
    fun setBorder(border: String?) {
        this.border = border
    }

    @JvmName("getCommKey1")
    fun getCommKey(): String? {
        return commKey
    }

    @JvmName("setCommKey1")
    fun setCommKey(commKey: String?) {
        this.commKey = commKey
    }
}