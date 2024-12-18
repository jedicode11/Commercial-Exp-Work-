package de.dimedis.mobileentry.backend.response;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class BaseResponse<T> {
    public static final String RESULT_OK = "ok";
    public static final String RESULT_ERROR = "error";

    @SerializedName("request_id")
    public String requestId;
    public String result;
    public T content;
    public ResponseError error;
    public Throwable throwable;

    public boolean isResultOk() {
        return TextUtils.equals(result, RESULT_OK);
    }

    @Override
    public String toString() {
        return "BaseResponse{" + "requestId='" + requestId + '\'' + ", result='" + result + '\'' + ", content=" + content + ", error=" + error + '}';
    }
}
