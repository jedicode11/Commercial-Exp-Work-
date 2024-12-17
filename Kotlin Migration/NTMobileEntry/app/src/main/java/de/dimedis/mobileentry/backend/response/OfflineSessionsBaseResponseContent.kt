package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName

class OfflineSessionsBaseResponseContent : BaseResponseContent() {
    @SerializedName("user_session")
    var user_session: String? = null

    @SerializedName("user_suid")
    var user_suid: String? = null

    @SerializedName("user_name")
    var user_name: String? = null

    @SerializedName("user_fullname")
    var user_fullname: String? = null

    @SerializedName("user_functions")
    var user_functions: List<String>? = null

    @SerializedName("user_prefs")
    var user_prefs: UserPrefs? = null

    override fun toString(): String {
        return "BaseResponseContent{" +
                "status='" + status + '\'' +
                ", statusDescription='" + statusDescription + '\'' +
                ", userMessage='" + userMessage + '\'' +
                ", user_session='" + user_session + '\'' +
                ", user_suid='" + user_suid + '\'' +
                ", user_name='" + user_name + '\'' +
                ", user_fullname='" + user_fullname + '\'' +
                ", user_functions='" + user_functions + '\'' +
//                ", user_prefs='" + user_prefs + '\'' +
                '}'
    }
}