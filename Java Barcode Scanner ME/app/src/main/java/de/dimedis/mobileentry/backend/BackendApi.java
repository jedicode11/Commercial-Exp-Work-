package de.dimedis.mobileentry.backend;

import com.google.gson.JsonObject;

import de.dimedis.mobileentry.backend.response.BatchUploadResponse;
import de.dimedis.mobileentry.backend.response.DownloadLanguagesResponse;
import de.dimedis.mobileentry.backend.response.DownloadMyAvailableBordersResponse;
import de.dimedis.mobileentry.backend.response.DownloadSettingsResponse;
import de.dimedis.mobileentry.backend.response.GetTicketHistoryResponse;
import de.dimedis.mobileentry.backend.response.GetVersionsResponse;
import de.dimedis.mobileentry.backend.response.GrabIdResponse;
import de.dimedis.mobileentry.backend.response.PerformCheckoutResponse;
import de.dimedis.mobileentry.backend.response.PerformEntryResponse;
import de.dimedis.mobileentry.backend.response.RecordEntryResponse;
import de.dimedis.mobileentry.backend.response.ResetDeviceResponse;
import de.dimedis.mobileentry.backend.response.SendHeartbeatResponse;
import de.dimedis.mobileentry.backend.response.ServerConnectResponse;
import de.dimedis.mobileentry.backend.response.StealIdResponse;
import de.dimedis.mobileentry.backend.response.UploadOfflineSessionsResponse;
import de.dimedis.mobileentry.backend.response.UserLoginByBarcodeResponse;
import de.dimedis.mobileentry.backend.response.UserLogoutResponse;
import de.dimedis.mobileentry.backend.response.VoidTicketResponse;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

public interface BackendApi {
    String PATH = // "";//
            "Dim_fm::Ctrl::FairMateMobileEntry3";

    String REQUEST_ID = "request_id";
    String LANG = "lang";
    String COMM_KEY = "comm_key";
    String DEVICE_ID = "device_id";
    String DEVICE_SUID = "device_suid";
    String DEVICE_TYPE = "device_type";
    String USER_NAME = "user_name";
    String USER_PASSWORD = "user_password";
    String USER_SUID = "user_suid";
    String USER_SESSION = "user_session";
    String ACTIVE_USER_SESSION = "active_user_session";
    String USER_PREFS = "user_prefs";
    String LOCAL_RECORDS = "local_records";
    String VERSIONS = "versions";
    String SYSTEM_HEALTH = "system_health";
    String FAIR = "fair";
    String BORDER = "border";
    String TICKET_CODE = "ticket_code";
    String LOGIN_BARCODE = "login_barcode";
    String CUSTOMER_TOKEN = "customer_token";
    String DATA = "data";

    @FormUrlEncoded
    @POST(PATH + "/server_connect")
    Observable<ServerConnectResponse> serverConnect(
            @Field(CUSTOMER_TOKEN) String customerToken);

    @FormUrlEncoded
    @POST(PATH + "/grab_id")
    Observable<GrabIdResponse> grabId(
            @Field(DEVICE_ID) String deviceId,
            @Field(LANG) String lang);

    @FormUrlEncoded
    @POST(PATH + "/steal_id")
    Observable<StealIdResponse> stealId(
            @Field(DEVICE_ID) String deviceId,
            @Field(LANG) String lang);

    @FormUrlEncoded
    @POST(PATH + "/user_login_by_barcode")
    Observable<UserLoginByBarcodeResponse> userLoginByBarcodeBackend(
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid,
            @Field(LOGIN_BARCODE) String loginBarcode);

    @FormUrlEncoded
    @POST(PATH + "/user_login_by_username_and_password")
    Observable<UserLoginByBarcodeResponse> userLoginByUsernameAndPassword(
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid,
            @Field(USER_NAME) String userName,
            @Field(USER_PASSWORD) String userPassword);

    @FormUrlEncoded
    @POST(PATH + "/download_settings")
    Observable<DownloadSettingsResponse> downloadSettings(
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid);

    @FormUrlEncoded
    @POST(PATH + "/download_my_available_borders")
    Observable<DownloadMyAvailableBordersResponse> downloadMyAvailableBorders(
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid);

    //Response
    @FormUrlEncoded
    @POST(PATH + "/download_offline_config")
    Observable<JsonObject> downloadOfflineConfig(
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid);

    @FormUrlEncoded
    @POST(PATH + "/get_versions")
    Observable<GetVersionsResponse> getVersionResponse(
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid,
            @Field(DEVICE_TYPE) String deviceType);

    @FormUrlEncoded
    @POST(PATH + "/reset_device")
    Observable<ResetDeviceResponse> resetDevice(
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid);

    @FormUrlEncoded
    @POST(PATH + "/send_heartbeat")
    Observable<SendHeartbeatResponse> sendHeartbeat(
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid,
            @Field(SYSTEM_HEALTH) String systemHealth,
            @Field(VERSIONS) String versions,
            @Field(USER_NAME) String userName,
            @Field(USER_SUID) String userSuid,
            @Field(LOCAL_RECORDS) String localRecords);

    @FormUrlEncoded
    @POST(PATH + "/get_ticket_history")
    Observable<GetTicketHistoryResponse> getTicketHistory(
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid,
            @Field(USER_SESSION) String userSession,
            @Field(USER_SUID) String userSuid,
            @Field(USER_NAME) String userName,
            @Field(FAIR) String fair,
            @Field(BORDER) String border,
            @Field(TICKET_CODE) String ticketCode);

    @FormUrlEncoded
    @POST(PATH + "/perform_entry")
    Observable<PerformEntryResponse> performEntry(
            @Field(REQUEST_ID) String requestId,
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid,
            @Field(USER_SESSION) String userSession,
            @Field(USER_SUID) String userSuid,
            @Field(USER_NAME) String userName,
            @Field(FAIR) String fair,
            @Field(BORDER) String border,
            @Field(TICKET_CODE) String ticketCode);

    @FormUrlEncoded
    @POST(PATH + "/perform_checkout")
    Observable<PerformCheckoutResponse> performCheckout(
            @Field(REQUEST_ID) String requestId,
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid,
            @Field(USER_SESSION) String userSession,
            @Field(USER_SUID) String userSuid,
            @Field(USER_NAME) String userName,
            @Field(FAIR) String fair,
            @Field(BORDER) String border,
            @Field(TICKET_CODE) String ticketCode);

    @FormUrlEncoded
    @POST(PATH + "/record_entry")
    Observable<RecordEntryResponse> recordEntry(
            @Field(REQUEST_ID) String requestId,
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid,
            @Field(USER_SESSION) String userSession,
            @Field(USER_SUID) String userSuid,
            @Field(USER_NAME) String userName,
            @Field(FAIR) String fair,
            @Field(BORDER) String border,
            @Field(TICKET_CODE) String ticketCode);

    @FormUrlEncoded
    @POST(PATH + "/user_logout")
    Observable<UserLogoutResponse> userLogout(
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid,
            @Field(USER_SESSION) String userSession,
            @Field(USER_SUID) String userSuid,
            @Field(USER_NAME) String userName,
            @Field(FAIR) String fair,
            @Field(BORDER) String border,
            @Field(USER_PREFS) String userPrefs);

    //batch_upload
    @FormUrlEncoded
    @POST(PATH + "/batch_upload")
    Observable<BatchUploadResponse> batchUpload2(
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid,
            @Field(USER_SESSION) String userSession,
            @Field(USER_SUID) String userSuid,
            @Field(USER_NAME) String userName,
            @Field(FAIR) String fair,
            @Field(BORDER) String border,
            @Field(DATA) String ticketCode);

    //upload_offline_sessions
    @FormUrlEncoded
    @POST(PATH + "/upload_offline_sessions")
    Observable<UploadOfflineSessionsResponse> uploadOfflineSessions(
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid,
            @Field(ACTIVE_USER_SESSION) String activeUserSession,
            @Field(DATA) String data);

    //batch_upload
    @FormUrlEncoded
    @POST(PATH + "/void_ticket")
    Observable<VoidTicketResponse> voidTicket(
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid,
            @Field(USER_SESSION) String userSession,
            @Field(USER_SUID) String userSuid,
            @Field(USER_NAME) String userName,
            @Field(FAIR) String fair,
            @Field(BORDER) String border,
            @Field(TICKET_CODE) String ticketCode);

    //download_library
    @FormUrlEncoded
    @POST(PATH + "/download_library")
    Observable<Response> downloadLibrary(
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid);

    //download_library
    @Streaming
    @FormUrlEncoded
    @POST(PATH + "/download_app")
    Observable<Response<ResponseBody>> downloadApp(
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid,
            @Field(DEVICE_TYPE) String deviceType);

    @FormUrlEncoded
    @POST(PATH + "/download_languages")
    Observable<DownloadLanguagesResponse> downloadLanguages(
            @Field(LANG) String lang,
            @Field(COMM_KEY) String commKey,
            @Field(DEVICE_SUID) String deviceSuid);
}
