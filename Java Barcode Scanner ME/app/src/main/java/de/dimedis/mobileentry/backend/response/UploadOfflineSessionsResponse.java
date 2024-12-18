package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

public class UploadOfflineSessionsResponse extends BaseResponseContent {
    @SerializedName("content")
    public OfflineSessionsBaseResponseContent content;

    @SerializedName("error")
    public ResponseError error;
}
