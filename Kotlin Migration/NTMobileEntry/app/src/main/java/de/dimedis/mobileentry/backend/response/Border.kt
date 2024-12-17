package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName

object Border {
    const val FAIR_NAME = "fair_name"
    const val FAIR = "fair"
    const val BORDER_NAME = "border_name"
    const val BORDER = "border"

    @SerializedName(FAIR_NAME)
    val fairName: String? = null

    @SerializedName(FAIR)
    val fair: String? = null

    @SerializedName(BORDER_NAME)
    val borderName: String? = null

    @SerializedName(BORDER)
    val border: String? = null
}