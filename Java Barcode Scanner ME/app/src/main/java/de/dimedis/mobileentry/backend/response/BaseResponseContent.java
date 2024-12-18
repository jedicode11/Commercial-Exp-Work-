package de.dimedis.mobileentry.backend.response;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import de.dimedis.mobileentry.backend.BackendService;

public class BaseResponseContent {
    @SerializedName("status")
    public String status;
    @SerializedName("status_descr")
    public String statusDescription;
    @SerializedName("user_message")
    public String userMessage;

    public boolean isStatusSuccess() {
        return TextUtils.equals(status, BackendService.STATUS_SUCCESS);
    }

    @Override
    public String toString() {
        return "BaseResponseContent{" +
                "status='" + status + '\'' +
                ", statusDescription='" + statusDescription + '\'' +
                ", userMessage='" + userMessage + '\'' +
                '}';
    }
}
