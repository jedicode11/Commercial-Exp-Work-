package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OfflineSessionsBaseResponseContent extends BaseResponseContent {
    @SerializedName("user_session")
    public String user_session;
    @SerializedName("user_suid")
    public String user_suid;
    @SerializedName("user_name")
    public String user_name;
    @SerializedName("user_fullname")
    public String user_fullname;
    @SerializedName("user_functions")
    public List<String> user_functions;
    @SerializedName("user_prefs")
    public UserPrefs user_prefs;

    @Override
    public String toString() {
        return "BaseResponseContent{" +
                "status='" + status + '\'' +
                ", statusDescription='" + statusDescription + '\'' +
                ", userMessage='" + userMessage + '\'' +
                ", user_session='" + user_session + '\'' +
                ", user_suid='" + user_suid + '\'' +
                ", user_name='" + user_name + '\'' +
                ", user_fullname='" + user_fullname + '\'' +
                ", user_functions='" + user_functions + '\'' +
                ", user_prefs='" + user_prefs + '\'' +
                '}';
    }
}
