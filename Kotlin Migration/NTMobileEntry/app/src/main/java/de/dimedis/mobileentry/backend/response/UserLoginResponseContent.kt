package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName

class UserLoginResponseContent : BaseResponseContent() {
    companion object {
        const val USER_SESSION = "user_session"
        const val USER_SUID = "user_suid"
        const val USER_FUNCTIONS = "user_functions"
        const val USER_PREFS = "user_prefs"
        const val USER_FULLNAME = "user_fullname"
        const val USER_NAME = "user_name"
    }

    @SerializedName(USER_SESSION)
    private val user_session: String? = null

    @SerializedName(USER_SUID)
    private val user_suid: String? = null

    @SerializedName(USER_FUNCTIONS)
    private val user_functions: List<String?>? = null

    @SerializedName(USER_PREFS)
    private val mUser_prefs: UserPrefs? = null

    @SerializedName(USER_FULLNAME)
    private val user_fullname: String? = null

    @SerializedName(USER_NAME)
    private val user_name: String? = null

    fun getUserSession(): String? {
        return user_session
    }

    fun getUserSuid(): String? {
        return user_suid
    }

    fun getListUserFunctions(): List<String?>? {
        return user_functions
    }

    fun getUserPrefs(): UserPrefs? {
        return mUser_prefs
    }

    fun getUserFullname(): String? {
        return user_fullname
    }

    fun getUserName(): String? {
        return user_name
    }
}