package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName

class GetVersionsResponseContent : BaseResponseContent() {
    @SerializedName(VERSIONS)
    private val mVersions: Versions? = null

    val versions: Versions?
        get() = mVersions

//    fun getVersions(): Versions? {
//        return mVersions
//    }

    override fun toString(): String {
        return """
           ${super.toString()}
           $mVersions
           """.trimIndent()
    }

    companion object {
        const val VERSIONS = "versions"
        const val STATUS = "status"
        const val __STUB__ = "__STUB__"
    }
}