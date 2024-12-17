package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName

class SendHeartbeatResponseContent : BaseResponseContent() {
    companion object {
        const val VERSIONS = "versions"
    }

    @SerializedName(VERSIONS)
    private val mVersions: Versions? = null

    val versions: Versions?
        get() = mVersions
}