package de.dimedis.mobileentry.model

import de.dimedis.lmlib.myinterfaces.Session
import de.dimedis.mobileentry.bbapi.Constants
import de.dimedis.mobileentry.util.SessionUtils

class OnUserOfflineLoginCompleted(session: Session?) {
    private var session: Session? = null

    init {
        setSession(session)
    }

    fun setSession(session: Session?) {
        this.session = session
    }

    fun getSession(): Session? {
        return session
    }

    fun isSuccessfulLogin(): Boolean {
        return session!!.getStatus().equals(Constants.OFFLINE_LOGIN_SUCCESS_STATUS, ignoreCase = true)
    }

    fun saveUserSessionPrefs() {
        getSession()?.let { SessionUtils.setUsersParam(it) }
        getSession()?.let { SessionUtils.storeOfflineSession(it) }
    }
}