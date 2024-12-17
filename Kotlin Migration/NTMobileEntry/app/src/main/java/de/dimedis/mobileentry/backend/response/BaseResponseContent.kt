package de.dimedis.mobileentry.backend.response

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import de.dimedis.mobileentry.backend.BackendServiceConst.STATUS_SUCCESS

open class BaseResponseContent {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("status_descr")
    var statusDescription: String? = null

    @SerializedName("user_message")
    var userMessage: String? = null

    fun isStatusSuccess(): Boolean {
        return TextUtils.equals(status, STATUS_SUCCESS)
    }

    override fun toString(): String {
        return "BaseResponseContent{" +
                "status='" + status + '\'' +
                ", statusDescription='" + statusDescription + '\'' +
                ", userMessage='" + userMessage + '\'' +
                '}'
    }
}