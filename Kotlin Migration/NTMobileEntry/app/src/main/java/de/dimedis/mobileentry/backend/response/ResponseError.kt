package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName

class ResponseError {
    var code: String? = null
    var message: String? = null
    var timestamp: String? = null

    @SerializedName("log_token")
    var logToken: String? = null
    override fun toString(): String {
        return "ResponseError{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", logToken='" + logToken + '\'' +
                '}'
    }
}