package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

public class Keys {
    public static final String F1 = "f1";
    public static final String F2 = "f2";
    public static final String F3 = "f3";
    public static final String F4 = "f4";


    @SerializedName(F1)
    private String f1;

    @SerializedName(F2)
    private String f2;

    @SerializedName(F3)
    private String f3;

    @SerializedName(F4)
    private String f4;

    public String getF1() {
        return f1;
    }

    public String getF2() {
        return f2;
    }

    public String getF3() {
        return f3;
    }

    public String getF4() {
        return f4;
    }

    public void setF1(String f1) {
        this.f1 = f1;
    }

    public void setF2(String f2) {
        this.f2 = f2;
    }

    public void setF3(String f3) {
        this.f3 = f3;
    }

    public void setF4(String f4) {
        this.f4 = f4;
    }
}
