package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserLoginResponseContent extends BaseResponseContent {

    public static final String USER_SESSION = "user_session";
    public static final String USER_SUID = "user_suid";
    public static final String USER_FUNCTIONS = "user_functions";

    public static final String USER_PREFS = "user_prefs";
    public static final String USER_FULLNAME = "user_fullname";
    public static final String USER_NAME = "user_name";


    @SerializedName(USER_SESSION)
    private String user_session;

    @SerializedName(USER_SUID)
    private String user_suid;


    @SerializedName(USER_FUNCTIONS)
    private List<String> user_functions;


    @SerializedName(USER_PREFS)
    private UserPrefs mUser_prefs;

    @SerializedName(USER_FULLNAME)
    private String user_fullname;

    @SerializedName(USER_NAME)
    private String user_name;

    public String getUserSession() {
        return user_session;
    }

    public String getUserSuid() {
        return user_suid;
    }

    public List<String> getListUserFunctions() {
        return user_functions;
    }


    public UserPrefs getUserPrefs() {
        return mUser_prefs;
    }


    public String getUserFullname() {
        return user_fullname;
    }

    public String getUserName() {
        return user_name;
    }
}

