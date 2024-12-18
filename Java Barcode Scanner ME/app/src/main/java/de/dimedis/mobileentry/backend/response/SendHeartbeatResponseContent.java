package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

public class SendHeartbeatResponseContent extends BaseResponseContent {
    public static final String VERSIONS = "versions";

    @SerializedName(VERSIONS)
    private Versions mVersions;


    public Versions getVersions() {
        return mVersions;
    }

}
