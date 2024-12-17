package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName

class SimplyDownloadOfflineConfigResponseContent {
    companion object {
        const val CONFIG = "config"
    }

    @SerializedName(CONFIG)
    val config: Any? = null
}