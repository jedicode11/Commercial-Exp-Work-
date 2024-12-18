package de.dimedis.mobileentry.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public final class Action {
    @SerializedName("fair")
    public final String location;
    @SerializedName("timestamp")
    public final String timestamp;
    @SerializedName("date_text")
    public final String dateText;
    @SerializedName("time_text")
    public final String timeText;
    @SerializedName("device_suid")
    public final String deviceSuid;
    @SerializedName("device_name")
    public final String deviceName;

    @SerializedName("type")
    public final String type;

    @SerializedName("message")
    public final String message;
    @SerializedName("ticket_code")
    public final String ticketCode;

    public Action(String timestamp, String dateText, String timeText, String deviceSuid, String deviceName, String type, String message, String ticketCode, String location) {
        this.timestamp = timestamp;
        location = timestamp;
        this.dateText = dateText;
        this.timeText = timeText;
        this.deviceSuid = deviceSuid;
        this.deviceName = deviceName;
        this.type = type;
        this.message = message;
        this.ticketCode = ticketCode;
        this.location = location;
    }

    public static Action fromLmLib(@NonNull de.dimedis.lmlib.myinterfaces.Action action) {
        return new Action(action.getTimestamp(), action.getDateText(), action.getTimeText(), action.getDeviceSuid(), action.getDeviceName(), action.getType(), action.getMessage(), action.getTicketCode(), null/*action.getLocation()*/);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Action action = (Action) o;
        return timestamp.equals(action.timestamp) && deviceSuid.equals(action.deviceSuid) && ticketCode.equals(action.ticketCode);
    }

    @Override
    public int hashCode() {
        int result = timestamp.hashCode();
        result = 31 * result + deviceSuid.hashCode();
        result = 31 * result + ticketCode.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Action{" + "timestamp='" + timestamp + '\'' + ", dateText='" + dateText + '\'' + ", timeText='" + timeText + '\'' + ", deviceSuid='" + deviceSuid + '\'' + ", deviceName='" + deviceName + '\'' + ", type='" + type + '\'' + ", message='" + message + '\'' + ", ticketCode='" + ticketCode + '\'' + '}';
    }
}
