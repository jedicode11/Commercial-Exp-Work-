package de.dimedis

import android.content.Context
import android.content.SharedPreferences

class UserPref_(context: Context) : UserPref {
    var pref: SharedPreferences
    var editor: SharedPreferences.Editor

    init {
        val pref = context.getSharedPreferences("UserPref", 0)
        this.pref = pref
        editor = pref.edit()
    }

    override fun userSuid(): String? {
        return null
    }

    override fun userName(): String? {
        return null
    }

    override fun userFullName(): String? {
        return null
    }

    override fun userPrefs(): String? {
        return null
    }

    override fun usersBorder(): String? {
        return null
    }

    override fun listUserFunctions(): String? {
        return null
    }

    override fun userSession(): String? {
        return null
    }
}