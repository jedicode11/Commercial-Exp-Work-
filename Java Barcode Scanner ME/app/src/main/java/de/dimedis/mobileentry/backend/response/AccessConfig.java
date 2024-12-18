package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

public class AccessConfig {
    public static final String ARBITRARY_DATA = "Arbitrary data";


    @SerializedName(ARBITRARY_DATA)
    private String arbitrary_data;

    public String getArbitraryData() {
        return arbitrary_data;
    }

}
