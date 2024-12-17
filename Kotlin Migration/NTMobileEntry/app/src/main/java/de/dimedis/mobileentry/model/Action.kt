package de.dimedis.mobileentry.model

import com.google.gson.annotations.SerializedName

open class Action(timestamp: String?, dateText: String?, timeText: String?, deviceSuid: String?,
                  deviceName: String?, type: String?, message: String?, ticketCode: String?, location: String?) {

    @SerializedName("fair")
    val location: String?

    @SerializedName("timestamp")
    val timestamp: String?

    @SerializedName("date_text")
    var dateText: String?

    @SerializedName("time_text")
    var timeText: String?

    @SerializedName("device_suid")
    var deviceSuid: String?

    @SerializedName("device_name")
    var deviceName: String?

    @SerializedName("type")
    var type: String?

    @SerializedName("message")
    var message: String?

    @SerializedName("ticket_code")
    var ticketCode: String?

    init {
        var location = location
        this.timestamp = timestamp
        location = timestamp
        this.dateText = dateText
        this.timeText = timeText
        this.deviceSuid = deviceSuid
        this.deviceName = deviceName
        this.type = type
        this.message = message
        this.ticketCode = ticketCode
        this.location = location
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val action = other as Action
        return timestamp == action.timestamp && deviceSuid == action.deviceSuid && ticketCode == action.ticketCode
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + deviceSuid.hashCode()
        result = 31 * result + ticketCode.hashCode()
        return result
    }

    override fun toString(): String {
        return "Action{timestamp='$timestamp', dateText='$dateText', timeText='$timeText', deviceSuid='$deviceSuid', deviceName='$deviceName', type='$type', message='$message', ticketCode='$ticketCode'}"
    }

    companion object {
        fun fromLmLib(action: Action?): Action { /*action.getLocation()*/
            return Action(action?.timestamp, action?.dateText, action?.timeText, action?.deviceSuid,
                action?.deviceName, action?.type, action?.message, action?.ticketCode, null)
        }
    }
}