package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

public class Border {
    public static final String FAIR_NAME = "fair_name";
    public static final String FAIR = "fair";
    public static final String BORDER_NAME = "border_name";
    public static final String BORDER = "border";


    @SerializedName(FAIR_NAME)
    private String fair_name;

    @SerializedName(FAIR)
    private String fair;

    @SerializedName(BORDER_NAME)
    private String border_name;

    @SerializedName(BORDER)
    private String border;

    public String getFairName() {
        return fair_name;
    }

    public String getFair() {
        return fair;
    }

    public String getBorderName() {
        return border_name;
    }

    public String getBorder() {
        return border;
    }


}
