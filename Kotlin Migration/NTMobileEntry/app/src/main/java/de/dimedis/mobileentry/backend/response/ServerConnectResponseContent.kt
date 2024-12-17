package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName

class ServerConnectResponseContent : BaseResponseContent() {
    @SerializedName("server_name")
    val serverName: String? = null

    @SerializedName("rpc_url")
    val rpcUrl: String? = null
    val languages: Map<String, String>? = null

    override fun toString(): String {
        return "ServerConnectResponseContent{" +
                "serverName='" + serverName + '\'' +
                ", rpcUrl='" + rpcUrl + '\'' +
                ", languages=" + languages +
                '}'
    }
}