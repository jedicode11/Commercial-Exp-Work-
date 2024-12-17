package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName

class UserPrefs {

    @SerializedName(KEYS)
    private val mKeys: Keys = Keys()

    val keys: Keys
        get() = mKeys

    companion object {
        const val KEYS = "keys"
    }
}