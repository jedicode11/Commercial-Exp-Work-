package de.dimedis.mobileentry.util

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import de.dimedis.lmlib.SessionImpl
import de.dimedis.lmlib.myinterfaces.Session
import de.dimedis.mobileentry.*
import de.dimedis.mobileentry.backend.BackendServiceUtil
import de.dimedis.mobileentry.backend.BaseArg
import de.dimedis.mobileentry.backend.response.OfflineSessionsBaseResponseContent
import de.dimedis.mobileentry.backend.response.UserLoginResponseContent
import de.dimedis.mobileentry.backend.response.UserPrefs
import de.dimedis.mobileentry.db.managers.DataBaseManager
import de.dimedis.mobileentry.model.LocalModeHelper
import de.dimedis.mobileentry.model.OnUserOfflineLoginCompleted
import de.dimedis.mobileentry.model.StatusManager
import de.dimedis.mobileentry.service.HeartbeatService
import de.dimedis.mobileentry.ui.LoginActivity
import de.dimedis.mobileentry.ui.ScanActivity
import de.dimedis.mobileentry.util.dynamicres.DynamicString
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object SessionUtils {
    const val TAG = "SessionUtils"
    var sDeviceIsGoingToBeReset = false
    fun setUsersParam(con: UserLoginResponseContent) {
        ConfigPref.userSuid = con.getUserSuid() // pref.setUserSuid(con.userSuid)
        ConfigPref.userName = con.getUserName()
        ConfigPrefHelper.setUserFunctions(con.getListUserFunctions())
        ConfigPrefHelper.setUserPrefs(con.getUserPrefs())
        ConfigPref.userFullName = con.getUserFullname()
        ConfigPref.userSession = con.getUserSession()
    }

    fun setUsersParam(session: Session) {
        ConfigPref
        ConfigPref.userSuid = session.getUserSuid()
        ConfigPref.userName = session.getUserName()
        ConfigPrefHelper.setUserFunctions(session.getUserFunctions())
        val prefs = convertFromSessionPrefs(session.getUserPrefs())
        ConfigPrefHelper.setUserPrefs(prefs)
        ConfigPref.userFullName = (session.getUserFullname())
        ConfigPref.userSession = (session.getUserSession())
    }

    fun convertFromSessionPrefs(userPrefs: HashMap<String, HashMap<String, String>>?): UserPrefs {
        val prefs = UserPrefs()
        for (key in userPrefs!!.keys) {
            val fkeys = userPrefs[key]!!
            if (key.equals(Constants.USER_PREFERENCES_FKEYS_KEY, ignoreCase = true)) {
                for (fKey_id in fkeys.keys) {
                    val function = fkeys[fKey_id]
                    if (!TextUtils.isEmpty(function)) {
                        when (fKey_id) {
                            Constants.FKEY_FUNCTION_1 -> prefs.keys.f1 = function
                            Constants.FKEY_FUNCTION_2 -> prefs.keys.f2 = function
                            Constants.FKEY_FUNCTION_3 -> prefs.keys.f3 = function
                            Constants.FKEY_FUNCTION_4 -> prefs.keys.f4 = function
                        }
                    }
                }
            }
        }
        return prefs
    }

    fun loginOffline(barcode: String) {
        doOfflineLogin(barcode)
    }

    fun loginOffline(user: String, password: String) {
        doOfflineLogin(user, password)
    }

    private fun doOfflineLogin(user: String, password: String) {
        val baseArg = BaseArg()
        val session = LocalModeHelper.getLocalMode().userLoginByUsernameAndPassword(user, password, baseArg.deviceSuid, baseArg.lang)
        Log.d(TAG, session.toString() + "")
        EventBus.getDefault().post(OnUserOfflineLoginCompleted(session))
    }

    private fun doOfflineLogin(barcode: String) {
        val baseArg = BaseArg()
        val session = LocalModeHelper.getLocalMode().userLoginByBarcode(barcode, baseArg.deviceSuid, baseArg.lang)
        Log.d(TAG, session.toString() + "")
        ConfigPref.loginBarcode = (barcode)
        EventBus.getDefault().post(OnUserOfflineLoginCompleted(session))
    }

    fun storeOfflineSession(session: Session) {
        try {
            DataBaseManager.instance()!!.saveOfflineUserSession(session)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun saveCredentials(login: String?, passwrd: String?) {
        ConfigPref.login = (login)
        ConfigPref.passwd = (passwrd)
    }

    @JvmOverloads
    fun logout(forceOfflineLogout: Boolean, shouldResetDeviceState: Boolean = false, isSessionInvalid: Boolean = false) {
        PrefUtils.setLoginCompleted(false)
        PrefUtils.setKioskModeEnabled(false)
        sDeviceIsGoingToBeReset = shouldResetDeviceState
        if (CommonUtils.isInternetConnected() && !forceOfflineLogout) {
            BackendServiceUtil.userLogout()
        } else {
            val logoutSession = LocalModeHelper.getLocalMode().userLogout(ConfigPref.userSession, ConfigPref.userSuid, ConfigPref.userName, null)
            ConfigPref.userSession?.let {
                if (logoutSession != null) {
                    DataBaseManager.instance()!!.reportUserLoggedOut(it, ConfigPref.userSuid!!, logoutSession)
                }
            }
            doUserLogout(shouldResetDeviceState, AppContext.get())
        }
        try {
            if (isSessionInvalid) {
                Toast.makeText(AppContext.get(), DynamicString.instance!!
                        .getString(R.string.MESSAGE_INVALID_SESSION), Toast.LENGTH_LONG).show()
            }
        } catch (th: Throwable) {
            Log.e(TAG, "logout: Toast", th)
        }
    }

    fun doUserLogout(shouldResetDevice: Boolean, context: Context) {
        ConfigPrefHelper.userLogout()
        HeartbeatService.cancelService()
        if (shouldResetDevice) {
            if (StatusManager.getInstance()?.getStatus() == StatusManager.Status.ONLINE) {
                BackendServiceUtil.resetDevice()
            }
        } else {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }

    fun resetDeviceLocalState(context: Context?) {
        ConfigPrefHelper.resetDevice()
        val uploadWorkRequest: WorkRequest = OneTimeWorkRequest.Builder(UtilsService::class.java).build()
        WorkManager.getInstance(context!!).enqueue(uploadWorkRequest)
        sDeviceIsGoingToBeReset = false
    }
    /**
     * Please find a refined structure description as well as two structure examples in http://doku.dimedis.de/23_fm_api_specs:010_mobile_entry_3_0:008_offline_session_handling.
     * To summarize:
     * the session id provided by local lib has to be noted underneath 'data' element
     * every session collected offline which has not yet been transferred to the server should be contained in the upload:offline_sessions() request
     * if a session has logged in and logged out offline, provide both login and logout element
     * if a session only has logged out offline, drop the login element for this session record
     * if a session only has logged in offline, drop the logout element for this session record
     * if a session has logged in offline but not yet logged out, it is most likely the still running on the device. In this case, the parameter 'active_user_session' has to be send, filled with the session id
     *
     * @param sessions - sessions to be uploaded
     * @return JSON body for the request parameter data
     */
    fun buildOfflineSessionsRequestBody(sessions: ArrayList<SessionImpl.SessionDB?>): String {
        val data = JSONObject()
        for (i in sessions.indices) {
            val sessionDB = sessions[i]
            try {
                val logoutSessionJson = JSONObject(((if (sessionDB?.logoutSession == null) "{}" else sessionDB.logoutSession).toString()))
                val loginSessionJson = JSONObject(((if (sessionDB?.loginSessionRaw == null) "{}" else sessionDB.loginSessionRaw).toString()))
                val userPreferences = buildUserPrefsJsonString()?.let { JSONObject(it) }
                val session = JSONObject()
                session.put("user_suid", sessionDB?.getUserSuid())
                session.put("user_name", sessionDB?.getUserName())
                if (TextUtils.isEmpty(sessionDB?.authType)) {
                    if (!TextUtils.isEmpty(sessionDB?.val1)) {
                        loginSessionJson.put("auth_type", "username_and_password")
                        loginSessionJson.put("auth_username", sessionDB?.val1)
                    }
                } else {
                    loginSessionJson.put("auth_type", "barcode")
                    loginSessionJson.put("auth_barcode", sessionDB?.authType)
                }
                if (!TextUtils.isEmpty(sessionDB?.timeStampLogin)) {
                    loginSessionJson.put("timestamp", getDbDateFormat(sessionDB?.timeStampLogin!!))
                    session.put("login", loginSessionJson)
                }
                logoutSessionJson.put("user_prefs", userPreferences)
                if (!TextUtils.isEmpty(sessionDB?.timeStampLogout)) {
                    logoutSessionJson.put("timestamp", getDbDateFormat(sessionDB?.timeStampLogout!!))
                    session.put("logout", logoutSessionJson)
                }
                val userSession = getUserSessionId(loginSessionJson, logoutSessionJson)
                data.put(userSession, session)
            } catch (e: JSONException) {
                Log.e(TAG, "error in buildOfflineSessionsRequestBody():", e)
            }
        }
        val body = data.toString()
        Logger.w(TAG, "json array: $body")
        return body
    }

    private fun getDbDateFormat(timeStampLogout: String): String {
        val date = Date(timeStampLogout.toLong())
        return Config.DB_DATE_TIME_FORMAT.format(date)
    }

    fun getUserSessionFromJson(`object`: JSONObject?): String? {
        return `object`?.optString("userSession")
    }

    @Throws(JSONException::class)
    private fun getUserSessionId(loginSessionJson: JSONObject, logoutSessionJson: JSONObject): String? {
        val out = getUserSessionFromJson(loginSessionJson)
        return out ?: getUserSessionFromJson(logoutSessionJson)
    }

    private fun buildUserPrefsJsonString(): String? {
        return ConfigPref.userPrefs
    }

    fun prolongSession(content: OfflineSessionsBaseResponseContent) {
        Logger.w(TAG, "Prolong session!")
        if (!TextUtils.isEmpty(content.user_fullname)) {
            ConfigPref.userFullName = (content.user_fullname)
        }
        if (!TextUtils.isEmpty(content.user_name)) {
            ConfigPref.userName = (content.user_name)
        }
        if (!TextUtils.isEmpty(content.user_session)) {
            ConfigPref.userSession = (content.user_session)
        }
        if (!TextUtils.isEmpty(content.user_suid)) {
            ConfigPref.userSuid = (content.user_suid)
        }
        ConfigPrefHelper.setUserFunctions(content.user_functions)
        ConfigPrefHelper.setUserPrefs(content.user_prefs)
        EventBus.getDefault().post(ScanActivity.OnFKeysUpdateEvent())
        DataBaseManager.instance()!!.deleteOfflineUserSessions()
        Logger.w(TAG, "deleteOfflineUserSessions")
    }
}