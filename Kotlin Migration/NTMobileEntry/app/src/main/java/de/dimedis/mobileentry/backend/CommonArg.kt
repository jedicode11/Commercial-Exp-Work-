package de.dimedis.mobileentry.backend

import android.content.Intent
import de.dimedis.mobileentry.ConfigPref
import de.dimedis.mobileentry.util.ConfigPrefHelper

class CommonArg(var intent: Intent?) : BaseArg(intent) {
    var userSession: String? = null
    var userSuid: String? = null
    var userName: String? = null
    var fair: String? = null
    var border: String? = null

    override fun getParamFromIntent(): CommonArg {
        super.getParamFromIntent()
        userSession(intent?.getStringExtra(BackendServiceConst.EXTRA_USER_SESSION))
        userSuid(intent?.getStringExtra(BackendServiceConst.EXTRA_USER_SUID))
        userName(intent?.getStringExtra(BackendServiceConst.EXTRA_USER_NAME))
        fair(intent?.getStringExtra(BackendServiceConst.EXTRA_FAIR))
        border(intent?.getStringExtra(BackendServiceConst.EXTRA_BORDER))
        return this
    }

    // override
    
    fun userSession(): String? {
        return userSession
    }
    //override
    fun userSession(userSession: String?) {
        this.userSession = userSession
    }

    fun userSuid(): String? {
        return userSuid
    }
    //override
    fun userSuid(userSuid: String?) {
        this.userSuid = userSuid
    }

    // override
    fun userName(): String? {
        return userName
    }

    // override
    fun userName(userName: String?) {
        this.userName = userName
    }

    // override
    fun fair(): String? {
        return fair
    }

    // override
    fun fair(fair: String?) {
        this.fair = fair
    }

    // override
    fun border(): String? {
        return border
    }

    // override
    fun border(border: String?) {
        this.border = border
    }

    companion object {
        fun setArgToIntent(intent: Intent) {
            BaseArg.setArgToIntent(intent)
            intent.putExtra(BackendServiceConst.EXTRA_USER_SESSION, ConfigPref.userSession)
            intent.putExtra(BackendServiceConst.EXTRA_USER_SUID, ConfigPref.userSuid)
            intent.putExtra(BackendServiceConst.EXTRA_USER_NAME, ConfigPref.userName)
            if (null != ConfigPrefHelper.getUsersBorder()) {
                intent.putExtra(BackendServiceConst.EXTRA_FAIR, ConfigPrefHelper.getUsersBorder()?.fair)
                intent.putExtra(BackendServiceConst.EXTRA_BORDER, ConfigPrefHelper.getUsersBorder()?.border)
            } else {
                intent.putExtra(BackendServiceConst.EXTRA_FAIR, "") // fair
                intent.putExtra(BackendServiceConst.EXTRA_BORDER, "") //border
            }
        }

        fun fromPreferences(): CommonArg {
            val commonArg = CommonArg(intent = null)
            val lang = ConfigPref.currentLanguage
            val commKey = ConfigPref.commKey
            val deviceSuid = ConfigPref.deviceSuid
            val deviceType = ConfigPref.deviceType
            val userSession = ConfigPref.userSession
            val userSuid = ConfigPref.userSuid
            val userName = ConfigPref.userName
            val fair = ConfigPrefHelper.getUsersBorder()?.fair
            val border = ConfigPrefHelper.getUsersBorder()?.border
            commonArg.lang = lang
            commonArg.commKey = commKey
            commonArg.deviceSuid = deviceSuid
            commonArg.deviceType = deviceType
            commonArg.userSession = userSession
            commonArg.userSuid = userSuid
            commonArg.userName = userName
            commonArg.fair = fair
            commonArg.border = border
            return commonArg
        }
    }
}