package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName

class Versions {
    @SerializedName(LOCAL_CONFIG)
    var localConfig: String? = "1" //stub

    @SerializedName(APP)
    var app: String? = null

    @SerializedName(MY_AVAILABLE_BORDERS)
    var myAvailableBorders: String? = null

    @SerializedName(LANGUAGES)
    var languages: String? = null

    @SerializedName(LIBRARY)
    var library: String? = null

    @SerializedName(SETTINGS)
    var settings: String? = "0"
    override fun toString(): String {
        return "Versions{" +
                "local_config='" + localConfig + '\'' +
                ", app='" + app + '\'' +
                ", my_available_borders='" + myAvailableBorders + '\'' +
                ", languages='" + languages + '\'' +
                ", library='" + library + '\'' +
                ", settings='" + settings + '\'' +
                '}'
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Versions) return false
        if (localConfig != other.localConfig) return false
        if (app != other.app) return false
        if (myAvailableBorders != other.myAvailableBorders) return false
        if (languages != other.languages) return false
        return if (library != other.library) false else settings == other.settings
    }

    override fun hashCode(): Int {
        var result = if (localConfig != null) localConfig.hashCode() else 0
        result = 31 * result + if (app != null) app.hashCode() else 0
        result = 31 * result + if (myAvailableBorders != null) myAvailableBorders.hashCode() else 0
        result = 31 * result + if (languages != null) languages.hashCode() else 0
        result = 31 * result + if (library != null) library.hashCode() else 0
        result = 31 * result + if (settings != null) settings.hashCode() else 0
        return result
    }

    companion object {
        const val LOCAL_CONFIG = "local_config"
        const val APP = "app"
        const val MY_AVAILABLE_BORDERS = "my_available_borders"
        const val LANGUAGES = "languages"
        const val LIBRARY = "library"
        const val SETTINGS = "settings"
    }
}