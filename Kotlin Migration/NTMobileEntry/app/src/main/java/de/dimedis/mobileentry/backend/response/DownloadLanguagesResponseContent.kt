package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName

class DownloadLanguagesResponseContent : BaseResponseContent() {
    companion object {
        const val LOCALIZATION = "localization"
    }

    @SerializedName(LOCALIZATION)
    private val mLang //mLocalization
            : HashMap<String?, Localize>? = null

    fun getLocalization(): HashMap<String?, Localize>? {
        return mLang
    }
}