package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

public class DownloadSettingsResponseContent extends BaseResponseContent {
    @SerializedName("heartbeat_interval_idle")
    public int heartbeatIntervalIdle;
    @SerializedName("heartbeat_interval_on_duty")
    public int heartbeatIntervalOnDuty;
    @SerializedName("offline_detect_timeout")
    public int offlineDetectTimeout;
    @SerializedName("offline_detect_count")
    public int offlineDetectCount;
    @SerializedName("scan_ok_switch_delay")
    public int scanOkSwitchDelay;
    @SerializedName("scan_denied_switch_delay")
    public int scanDeniedSwitchDelay;
    @SerializedName("scan_cancel_timeout")
    public int scanCancelTimeout;
    @SerializedName("scan_doublescan_delay")
    public int scanDoubleScanDelay;

    @Override
    public String toString() {
        return "DownloadSettingsResponseContent{" +
                "heartbeatIntervalIdle=" + heartbeatIntervalIdle +
                ", heartbeatIntervalOnDuty=" + heartbeatIntervalOnDuty +
                ", offlineDetectTimeout=" + offlineDetectTimeout +
                ", offlineDetectCount=" + offlineDetectCount +
                ", scanOkSwitchDelay=" + scanOkSwitchDelay +
                ", scanDeniedSwitchDelay=" + scanDeniedSwitchDelay +
                ", scanCancelTimeout=" + scanCancelTimeout +
                ", scanDoubleScanDelay=" + scanDoubleScanDelay +
                '}';
    }
}
