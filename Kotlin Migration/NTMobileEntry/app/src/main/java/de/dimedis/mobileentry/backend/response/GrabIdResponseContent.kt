package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName

class GrabIdResponseContent : BaseResponseContent() {

    companion object {
        const val DEVICE_ID = "device_id"
        const val OTHER_DEVICE_INSTALL_TS = "other_device_install_ts"
        const val OTHER_USER_SESSION_START_TEXT = "other_user_session_start_text"
        const val OTHER_USER_SESSION_START_TS = "other_user_session_start_ts"
        const val OTHER_DEVICE_INSTALL_TEXT = "other_device_install_text"
    }


    @SerializedName("comm_key")
    var commKey: String? = null

    //device_name=FM-Mobile-Entry 800
    @SerializedName("device_name")
    var deviceName: String? = null


    //device_suid=dim.d.800
    @SerializedName("device_suid")
    var deviceSuid: String? = null


    @SerializedName(GrabIdResponseContent.OTHER_DEVICE_INSTALL_TS)
    private val other_device_install_ts: String? = null

    @SerializedName(GrabIdResponseContent.OTHER_USER_SESSION_START_TEXT)
    private val other_user_session_start_text: String? = null

    @SerializedName(GrabIdResponseContent.DEVICE_ID)
    private val device_id = 0

    @SerializedName(GrabIdResponseContent.OTHER_USER_SESSION_START_TS)
    private val other_user_session_start_ts: String? = null

    @SerializedName(GrabIdResponseContent.OTHER_DEVICE_INSTALL_TEXT)
    private val other_device_install_text: String? = null

    fun getDevice_id(): Int {
        return device_id
    }

    fun getOtherDeviceInstall_ts(): String? {
        return other_device_install_ts
    }

    fun getOtherUserSessionStart_text(): String? {
        return other_user_session_start_text
    }

    fun getOtherUserSessionStart_ts(): String? {
        return other_user_session_start_ts
    }

    fun getOtherDeviceInstall_text(): String? {
        return other_device_install_text
    }
}