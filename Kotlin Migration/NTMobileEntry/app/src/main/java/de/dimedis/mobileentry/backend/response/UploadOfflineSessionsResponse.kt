package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName

class UploadOfflineSessionsResponse : BaseResponseContent() {

    @SerializedName("content")
    var content: OfflineSessionsBaseResponseContent? = null

    @SerializedName("error")
    var error: ResponseError? = null

}