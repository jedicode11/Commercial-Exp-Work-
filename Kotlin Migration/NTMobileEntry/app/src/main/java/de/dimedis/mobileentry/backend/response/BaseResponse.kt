package de.dimedis.mobileentry.backend.response

import android.text.TextUtils
import com.google.gson.annotations.SerializedName

open class BaseResponse<T> {
    @SerializedName("request_id")
    var requestId: String? = null
    var result: String? = null
    var content: T? = null
    var error: ResponseError? = null
    var throwable: Throwable? = null
    val isResultOk: Boolean
        get() = TextUtils.equals(result, RESULT_OK)

    override fun toString(): String {
        return "BaseResponse{" +
                "requestId='" + requestId + '\'' +
                ", result='" + result + '\'' +
                ", content=" + content +
                ", error=" + error +
                '}'
    }

    companion object {
        const val RESULT_OK = "ok"
        const val RESULT_ERROR = "error"
    }
}