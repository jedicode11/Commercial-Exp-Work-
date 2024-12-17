package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName

open class DownloadSettingsResponseContent : BaseResponseContent() {

    @SerializedName("heartbeat_interval_idle")
    var heartbeatIntervalIdle = 0

    @SerializedName("heartbeat_interval_on_duty")
    var heartbeatIntervalOnDuty = 0

    @SerializedName("offline_detect_timeout")
    var offlineDetectTimeout = 0

    @SerializedName("offline_detect_count")
    var offlineDetectCount = 0

    @SerializedName("scan_ok_switch_delay")
    var scanOkSwitchDelay = 0

    @SerializedName("scan_denied_switch_delay")
    var scanDeniedSwitchDelay = 0

    @SerializedName("scan_cancel_timeout")
    var scanCancelTimeout = 0

    @SerializedName("scan_doublescan_delay")
    var scanDoubleScanDelay = 0

    override fun toString(): String {
        return "DownloadSettingsResponseContent{" +
                "heartbeatIntervalIdle=" + heartbeatIntervalIdle +
                ", heartbeatIntervalOnDuty=" + heartbeatIntervalOnDuty +
                ", offlineDetectTimeout=" + offlineDetectTimeout +
                ", offlineDetectCount=" + offlineDetectCount +
                ", scanOkSwitchDelay=" + scanOkSwitchDelay +
                ", scanDeniedSwitchDelay=" + scanDeniedSwitchDelay +
                ", scanCancelTimeout=" + scanCancelTimeout +
                ", scanDoubleScanDelay=" + scanDoubleScanDelay +
                '}'
    }
}