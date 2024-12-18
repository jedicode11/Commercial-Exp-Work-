package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class DownloadLanguagesResponseContent extends BaseResponseContent {
    public static final String LOCALIZATION = "localization";


    @SerializedName(LOCALIZATION)
    private HashMap<String, Localize> mLang;//mLocalization

    public HashMap<String, Localize> getLocalization() {
        return mLang;
    }
}
