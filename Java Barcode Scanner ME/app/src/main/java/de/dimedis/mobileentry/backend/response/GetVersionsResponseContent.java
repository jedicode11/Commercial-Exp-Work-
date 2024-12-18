package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

public class GetVersionsResponseContent extends BaseResponseContent {
    public static final String VERSIONS = "versions";
    public static final String STATUS = "status";
    public static final String __STUB__ = "__STUB__";


    @SerializedName(VERSIONS)
    private Versions mVersions;

    public Versions getVersions() {
        return mVersions;
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + mVersions;
    }
}
