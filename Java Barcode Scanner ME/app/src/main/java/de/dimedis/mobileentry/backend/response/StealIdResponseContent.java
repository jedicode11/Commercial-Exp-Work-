package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

public class StealIdResponseContent extends BaseResponseContent {

    @SerializedName("comm_key")
    public String commKey;

    @SerializedName("device_name")
    public String deviceName;

    @SerializedName("device_suid")
    public String deviceSuid;
}
