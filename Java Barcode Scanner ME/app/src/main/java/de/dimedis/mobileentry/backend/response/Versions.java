package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Versions {
    public static final String LOCAL_CONFIG = "local_config";
    public static final String APP = "app";
    public static final String MY_AVAILABLE_BORDERS = "my_available_borders";
    public static final String LANGUAGES = "languages";
    public static final String LIBRARY = "library";
    public static final String SETTINGS = "settings";


    @SerializedName(LOCAL_CONFIG)
    private String local_config = "1";//stub

    @SerializedName(APP)
    private String app;

    @SerializedName(MY_AVAILABLE_BORDERS)
    private String my_available_borders;

    @SerializedName(LANGUAGES)
    private String languages;

    @SerializedName(LIBRARY)
    private String library;

    @SerializedName(SETTINGS)
    private String settings = "0";

    public String getLocalConfig() {
        return local_config;
    }

    public String getApp() {
        return app;
    }

    public String getMyAvailableBorders() {
        return my_available_borders;
    }

    public String getLanguages() {
        return languages;
    }

    public String getLibrary() {
        return library;
    }

    public String getSettings() {
        return settings;
    }

    public void setLocalConfig(String local_config) {
        this.local_config = local_config;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public void setMyAvailableBorders(String my_available_borders) {
        this.my_available_borders = my_available_borders;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "Versions{" +
                "local_config='" + local_config + '\'' +
                ", app='" + app + '\'' +
                ", my_available_borders='" + my_available_borders + '\'' +
                ", languages='" + languages + '\'' +
                ", library='" + library + '\'' +
                ", settings='" + settings + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Versions)) return false;

        Versions versions = (Versions) o;

        if (!Objects.equals(local_config, versions.local_config))
            return false;
        if (!Objects.equals(app, versions.app)) return false;
        if (!Objects.equals(my_available_borders, versions.my_available_borders))
            return false;
        if (!Objects.equals(languages, versions.languages))
            return false;
        if (!Objects.equals(library, versions.library))
            return false;
        return Objects.equals(settings, versions.settings);
    }

    @Override
    public int hashCode() {
        int result = local_config != null ? local_config.hashCode() : 0;
        result = 31 * result + (app != null ? app.hashCode() : 0);
        result = 31 * result + (my_available_borders != null ? my_available_borders.hashCode() : 0);
        result = 31 * result + (languages != null ? languages.hashCode() : 0);
        result = 31 * result + (library != null ? library.hashCode() : 0);
        result = 31 * result + (settings != null ? settings.hashCode() : 0);
        return result;
    }
}
