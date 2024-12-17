package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName

class AccessConfig {
    @SerializedName(ARBITRARY_DATA)
    private val arbitrary_data: String? = null
    val arbitraryData: String?
        get() = arbitrary_data

    companion object {
        const val ARBITRARY_DATA = "Arbitrary data"
    }
}