package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

public class GrabIdResponseContent extends BaseResponseContent {

    public static final String DEVICE_ID = "device_id";
    public static final String OTHER_DEVICE_INSTALL_TS = "other_device_install_ts";
    public static final String OTHER_USER_SESSION_START_TEXT = "other_user_session_start_text";
    public static final String OTHER_USER_SESSION_START_TS = "other_user_session_start_ts";
    public static final String OTHER_DEVICE_INSTALL_TEXT = "other_device_install_text";


    @SerializedName("comm_key")
    public String commKey;

    //device_name=FM-Mobile-Entry 800
    @SerializedName("device_name")
    public String deviceName;


    //device_suid=dim.d.800
    @SerializedName("device_suid")
    public String deviceSuid;


    @SerializedName(OTHER_DEVICE_INSTALL_TS)
    private String other_device_install_ts;

    @SerializedName(OTHER_USER_SESSION_START_TEXT)
    private String other_user_session_start_text;

    @SerializedName(DEVICE_ID)
    private int device_id;

    @SerializedName(OTHER_USER_SESSION_START_TS)
    private String other_user_session_start_ts;

    @SerializedName(OTHER_DEVICE_INSTALL_TEXT)
    private String other_device_install_text;

    public int getDevice_id() {
        return device_id;
    }

    public String getOtherDeviceInstall_ts() {
        return other_device_install_ts;
    }

    public String getOtherUserSessionStart_text() {
        return other_user_session_start_text;
    }

    public String getOtherUserSessionStart_ts() {
        return other_user_session_start_ts;
    }

    public String getOtherDeviceInstall_text() {
        return other_device_install_text;
    }


}
