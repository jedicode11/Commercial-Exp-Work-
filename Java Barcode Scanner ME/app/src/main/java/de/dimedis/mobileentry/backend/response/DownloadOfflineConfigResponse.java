package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

public class DownloadOfflineConfigResponse extends BaseResponse<SimplyDownloadOfflineConfigResponseContent> {
}

class SimplyDownloadOfflineConfigResponseContent extends BaseResponseContent {
    public static final String CONFIG = "config";

    @SerializedName(CONFIG)
    private Object mConfig;

    public Object getConfig() {
        return mConfig;
    }

    ;
}

