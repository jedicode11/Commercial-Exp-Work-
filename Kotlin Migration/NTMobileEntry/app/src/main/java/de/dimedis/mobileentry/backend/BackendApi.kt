package de.dimedis.mobileentry.backend

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import de.dimedis.mobileentry.backend.response.*
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Streaming

interface BackendApi {

    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/server_connect")
    fun serverConnect(
        @Field(BackendApiConst.CUSTOMER_TOKEN) customerToken: String?
    ): Observable<ServerConnectResponse>

    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/grab_id")
    fun grabId(
        @Field(BackendApiConst.DEVICE_ID) deviceId: String?,
        @Field(BackendApiConst.LANG) lang: String?
    ): Observable<GrabIdResponse>

    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/steal_id")
    fun stealId(
        @Field(BackendApiConst.DEVICE_ID) deviceId: String?,
        @Field(BackendApiConst.LANG) lang: String?
    ): Observable<StealIdResponse>

    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/user_login_by_barcode")
    fun userLoginByBarcodeBackend(
        @Field(BackendApiConst.LANG) lang: String?,
        @Field(BackendApiConst.COMM_KEY) commKey: String?,
        @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?,
        @Field(BackendApiConst.LOGIN_BARCODE) loginBarcode: String?
    ): Observable<UserLoginByBarcodeResponse>

    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/user_login_by_username_and_password")
    fun userLoginByUsernameAndPassword(
        @Field(BackendApiConst.LANG) lang: String?,
        @Field(BackendApiConst.COMM_KEY) commKey: String?,
        @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?,
        @Field(BackendApiConst.USER_NAME) userName: String?,
        @Field(BackendApiConst.USER_PASSWORD) userPassword: String?
    ): Observable<UserLoginByBarcodeResponse>

    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/download_settings")
    fun downloadSettings(
    @Field(BackendApiConst.LANG) lang: String?,
    @Field(BackendApiConst.COMM_KEY) commKey: String?,
    @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?
    ): Observable<DownloadSettingsResponse>

    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/download_my_available_borders")
    fun downloadMyAvailableBorders(
        @Field(BackendApiConst.LANG) lang: String?,
        @Field(BackendApiConst.COMM_KEY) commKey: String?,
        @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?
    ): Observable<DownloadMyAvailableBordersResponse>

    //Response
    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/download_offline_config")
    fun downloadOfflineConfig(
        @Field(BackendApiConst.LANG) lang: String?,
        @Field(BackendApiConst.COMM_KEY) commKey: String?,
        @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?
    ): Observable<JsonObject>

    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/get_versions")
    fun getVersionResponse(
        @Field(BackendApiConst.LANG) lang: String?,
        @Field(BackendApiConst.COMM_KEY) commKey: String?,
        @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?,
        @Field(BackendApiConst.DEVICE_TYPE) deviceType: String?
    ): Observable<GetVersionsResponse>

    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/reset_device")
    fun resetDevice(
        @Field(BackendApiConst.LANG) lang: String?,
        @Field(BackendApiConst.COMM_KEY) commKey: String?,
        @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?
    ): Observable<ResetDeviceResponse>

    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/send_heartbeat")
    fun sendHeartbeat(
        @Field(BackendApiConst.LANG) lang: String?,
        @Field(BackendApiConst.COMM_KEY) commKey: String?,
        @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?,
        @Field(BackendApiConst.SYSTEM_HEALTH) systemHealth: String?,
        @Field(BackendApiConst.VERSIONS) versions: String?,
        @Field(BackendApiConst.USER_NAME) userName: String?,
        @Field(BackendApiConst.USER_SUID) userSuid: String?,
        @Field(BackendApiConst.LOCAL_RECORDS) localRecords: String?
    ): Observable<SendHeartbeatResponse>

    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/get_ticket_history")
    fun getTicketHistory(
        @Field(BackendApiConst.LANG) lang: String?,
        @Field(BackendApiConst.COMM_KEY) commKey: String?,
        @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?,
        @Field(BackendApiConst.USER_SESSION) userSession: String?,
        @Field(BackendApiConst.USER_SUID) userSuid: String?,
        @Field(BackendApiConst.USER_NAME) userName: String?,
        @Field(BackendApiConst.FAIR) fair: String?,
        @Field(BackendApiConst.BORDER) border: String?,
        @Field(BackendApiConst.TICKET_CODE) ticketCode: String?
    ): Observable<GetTicketHistoryResponse>

    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/perform_entry")
    fun performEntry(
        @Field(BackendApiConst.REQUEST_ID) requestId: String?,
        @Field(BackendApiConst.LANG) lang: String?,
        @Field(BackendApiConst.COMM_KEY) commKey: String?,
        @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?,
        @Field(BackendApiConst.USER_SESSION) userSession: String?,
        @Field(BackendApiConst.USER_SUID) userSuid: String?,
        @Field(BackendApiConst.USER_NAME) userName: String?,
        @Field(BackendApiConst.FAIR) fair: String?,
        @Field(BackendApiConst.BORDER) border: String?,
        @Field(BackendApiConst.TICKET_CODE) ticketCode: String?
    ): Observable<PerformEntryResponse>

    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/perform_checkout")
    fun performCheckout(
        @Field(BackendApiConst.REQUEST_ID) requestId: String?,
        @Field(BackendApiConst.LANG) lang: String?,
        @Field(BackendApiConst.COMM_KEY) commKey: String?,
        @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?,
        @Field(BackendApiConst.USER_SESSION) userSession: String?,
        @Field(BackendApiConst.USER_SUID) userSuid: String?,
        @Field(BackendApiConst.USER_NAME) userName: String?,
        @Field(BackendApiConst.FAIR) fair: String?,
        @Field(BackendApiConst.BORDER) border: String?,
        @Field(BackendApiConst.TICKET_CODE) ticketCode: String?
    ): Observable<PerformCheckoutResponse>

    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/record_entry")
    fun recordEntry(
        @Field(BackendApiConst.REQUEST_ID) requestId: String?,
        @Field(BackendApiConst.LANG) lang: String?,
        @Field(BackendApiConst.COMM_KEY) commKey: String?,
        @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?,
        @Field(BackendApiConst.USER_SESSION) userSession: String?,
        @Field(BackendApiConst.USER_SUID) userSuid: String?,
        @Field(BackendApiConst.USER_NAME) userName: String?,
        @Field(BackendApiConst.FAIR) fair: String?,
        @Field(BackendApiConst.BORDER) border: String?,
        @Field(BackendApiConst.TICKET_CODE) ticketCode: String?
    ): Observable<RecordEntryResponse>

//    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/user_logout")
    fun userLogout(
        @Field(BackendApiConst.LANG) lang: String?,
        @Field(BackendApiConst.COMM_KEY) commKey: String?,
        @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?,
        @Field(BackendApiConst.USER_SESSION) userSession: String?,
        @Field(BackendApiConst.USER_SUID) userSuid: String?,
        @Field(BackendApiConst.USER_NAME) userName: String?,
        @Field(BackendApiConst.FAIR) fair: String?,
        @Field(BackendApiConst.BORDER) border: String?,
        @Field(BackendApiConst.USER_PREFS) userPrefs: String?
    ): Observable<UserLogoutResponse>

//    //batch_upload
    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/batch_upload")
    fun batchUpload2(
    @Field(BackendApiConst.LANG) lang: String?,
    @Field(BackendApiConst.COMM_KEY) commKey: String?,
    @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?,
    @Field(BackendApiConst.USER_SESSION) userSession: String?,
    @Field(BackendApiConst.USER_SUID) userSuid: String?,
    @Field(BackendApiConst.USER_NAME) userName: String?,
    @Field(BackendApiConst.FAIR) fair: String?,
    @Field(BackendApiConst.BORDER) border: String?,
    @Field(BackendApiConst.DATA) ticketCode: String?
    ): Observable<BatchUploadResponse>

    //upload_offline_sessions
    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/upload_offline_sessions")
    fun uploadOfflineSessions(
        @Field(BackendApiConst.LANG) lang: String?,
        @Field(BackendApiConst.COMM_KEY) commKey: String?,
        @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?,
        @Field(BackendApiConst.ACTIVE_USER_SESSION) activeUserSession: String?,
        @Field(BackendApiConst.DATA) data: String?
    ): Observable<UploadOfflineSessionsResponse>

//    //batch_upload
    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/void_ticket")
    fun voidTicket(
        @Field(BackendApiConst.LANG) lang: String?,
        @Field(BackendApiConst.COMM_KEY) commKey: String?,
        @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?,
        @Field(BackendApiConst.USER_SESSION) userSession: String?,
        @Field(BackendApiConst.USER_SUID) userSuid: String?,
        @Field(BackendApiConst.USER_NAME) userName: String?,
        @Field(BackendApiConst.FAIR) fair: String?,
        @Field(BackendApiConst.BORDER) border: String?,
        @Field(BackendApiConst.TICKET_CODE) ticketCode: String?
    ): Observable<VoidTicketResponse>

    //download_library
    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/download_library")
    fun downloadLibrary(
        @Field(BackendApiConst.LANG) lang: String?,
        @Field(BackendApiConst.COMM_KEY) commKey: String?,
        @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?
    ): Observable<Response<*>>

    //download_library
    @Streaming
    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/download_app")
    fun downloadApp(
        @Field(BackendApiConst.LANG) lang: String?,
        @Field(BackendApiConst.COMM_KEY) commKey: String?,
        @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?,
        @Field(BackendApiConst.DEVICE_TYPE) deviceType: String?
    ): Observable<Response<ResponseBody>>

    @FormUrlEncoded
    @POST(BackendApiConst.PATH + "/download_languages")
    fun downloadLanguages(
        @Field(BackendApiConst.LANG) lang: String?,
        @Field(BackendApiConst.COMM_KEY) commKey: String?,
        @Field(BackendApiConst.DEVICE_SUID) deviceSuid: String?
    ): Observable<DownloadLanguagesResponse>
}

data class LoginPostData(
    @SerializedName("UserId") var userID: String,
    @SerializedName("Password") var userPassword: String
)