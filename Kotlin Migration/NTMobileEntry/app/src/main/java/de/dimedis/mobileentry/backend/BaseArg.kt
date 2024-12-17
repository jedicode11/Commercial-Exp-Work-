package de.dimedis.mobileentry.backend

import android.content.Intent
import android.os.Build
import de.dimedis.mobileentry.ConfigPref

open class BaseArg {
    private var intent: Intent? = null
    var lang: String? = null
    var commKey: String? = null
    var deviceSuid: String? = null
    var deviceType: String? = null

    constructor() {
        lang = ConfigPref.currentLanguage
        commKey = ConfigPref.commKey
        deviceSuid = ConfigPref.deviceSuid
    }

    internal constructor(intent: Intent?) {
        this.intent = intent
        getParamFromIntent()
    }

    open fun getParamFromIntent(): BaseArg {
        lang = intent!!.getStringExtra(BackendServiceConst.EXTRA_LANG)
        commKey = intent!!.getStringExtra(BackendServiceConst.EXTRA_COMM_KEY)
        deviceSuid = intent!!.getStringExtra(BackendServiceConst.EXTRA_DEVICE_SUID)
        return this
    }



    @JvmName("getLang1")
    fun getLang(): String? {
        return lang
    }

    @JvmName("setLang1")
    fun setLang(lang: String?) {
        this.lang = lang
    }

    @JvmName("getCommKey1")
    fun getCommKey(): String? {
        return commKey
    }

    @JvmName("setCommKey1")
    fun setCommKey(commKey: String?) {
        this.commKey = commKey
    }

    @JvmName("getDeviceSuid1")
    fun getDeviceSuid(): String? {
        return deviceSuid
    }

    @JvmName("setDeviceSuid1")
    fun setDeviceSuid(deviceSuid: String?) {
        this.deviceSuid = deviceSuid
    }

    @JvmName("getDeviceType1")
    fun getDeviceType(): String {
        return Build.MODEL
        //        return "PM75";
//        return deviceType;
    }

    @JvmName("setDeviceType1")
    fun setDeviceType(deviceType: String?) {
        this.deviceType = deviceType
    }

    companion object {
        fun setArgToIntent(intent: Intent) {
            intent.putExtra(BackendServiceConst.EXTRA_LANG, ConfigPref?.currentLanguage)
            intent.putExtra(BackendServiceConst.EXTRA_COMM_KEY, ConfigPref?.commKey)
            intent.putExtra(BackendServiceConst.EXTRA_DEVICE_SUID, ConfigPref?.deviceSuid)
        }
    }
}