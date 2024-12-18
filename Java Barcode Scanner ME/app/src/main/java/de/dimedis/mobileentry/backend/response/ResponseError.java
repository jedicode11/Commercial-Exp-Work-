package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

public class ResponseError {
    public String code;
    public String message;
    public String timestamp;
    @SerializedName("log_token")
    public String logToken;

    @Override
    public String toString() {
        return "ResponseError{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", logToken='" + logToken + '\'' +
                '}';
    }
}
