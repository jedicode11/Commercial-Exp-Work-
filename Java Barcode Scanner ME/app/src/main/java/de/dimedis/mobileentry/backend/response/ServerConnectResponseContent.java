package de.dimedis.mobileentry.backend.response;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class ServerConnectResponseContent extends BaseResponseContent {
    @SerializedName("server_name")
    public String serverName;
    @SerializedName("rpc_url")
    public String rpcUrl;
    public Map<String, String> languages;

    @Override
    public String toString() {
        return "ServerConnectResponseContent{" +
                "serverName='" + serverName + '\'' +
                ", rpcUrl='" + rpcUrl + '\'' +
                ", languages=" + languages +
                '}';
    }
}
