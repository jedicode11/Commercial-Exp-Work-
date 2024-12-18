package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DownloadMyAvailableBordersResponseContent extends BaseResponseContent {
    public static final String BORDERS = "borders";

    @SerializedName(BORDERS)
    private List<Border> listBorders;

    public List<Border> getListBorders() {
        return listBorders;
    }

    @Override
    public String toString() {
        return "DownloadMyAvailableBordersResponseContent{" +
                "listBorders=" + listBorders +
                '}';
    }
}
