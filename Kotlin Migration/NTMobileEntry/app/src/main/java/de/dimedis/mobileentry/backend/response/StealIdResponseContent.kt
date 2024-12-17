package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName

class StealIdResponseContent : BaseResponseContent() {

    @SerializedName("comm_key")
    var commKey: String? = null

    //device_name=FM-Mobile-Entry 800
    @SerializedName("device_name")
    var deviceName: String? = null


    //device_suid=dim.d.800
    @SerializedName("device_suid")
    var deviceSuid: String? = null
}