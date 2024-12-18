package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

public class UserPrefs {
    public static final String KEYS = "keys";

    public UserPrefs() {
        mKeys = new Keys();
    }

    @SerializedName(KEYS)
    private Keys mKeys;

    public Keys getKeys() {
        return mKeys;
    }
}
