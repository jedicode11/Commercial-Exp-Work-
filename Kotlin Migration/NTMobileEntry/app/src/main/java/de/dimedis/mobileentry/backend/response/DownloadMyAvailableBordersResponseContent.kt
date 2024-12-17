package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName

class DownloadMyAvailableBordersResponseContent : BaseResponseContent() {
    companion object {
        const val BORDERS = "borders"
    }

    @SerializedName(BORDERS)
    val listBorders: List<Border?>? = null

    override fun toString(): String {
        return "DownloadMyAvailableBordersResponseContent{" +
                "listBorders=" + listBorders +
                '}'
    }
}