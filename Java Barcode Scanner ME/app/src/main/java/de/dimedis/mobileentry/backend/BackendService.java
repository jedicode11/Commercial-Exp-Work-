package de.dimedis.mobileentry.backend;

import static de.dimedis.mobileentry.util.ConfigPrefHelper.setBorders;
import static de.dimedis.mobileentry.util.ConfigPrefHelper.setServerVersions;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dimedis.lmlib.SessionImpl;
import de.dimedis.mobileentry.BuildConfig;
import de.dimedis.mobileentry.Config;
import de.dimedis.mobileentry.ConfigPref;
import de.dimedis.mobileentry.MobileEntryApplication;
import de.dimedis.mobileentry.backend.response.BaseResponse;
import de.dimedis.mobileentry.backend.response.BaseResponseContent;
import de.dimedis.mobileentry.backend.response.BatchUploadResponse;
import de.dimedis.mobileentry.backend.response.Border;
import de.dimedis.mobileentry.backend.response.DownloadLanguagesResponse;
import de.dimedis.mobileentry.backend.response.DownloadLibrary;
import de.dimedis.mobileentry.backend.response.DownloadMyAvailableBordersResponse;
import de.dimedis.mobileentry.backend.response.DownloadOfflineConfigResponse;
import de.dimedis.mobileentry.backend.response.DownloadSettingsResponse;
import de.dimedis.mobileentry.backend.response.GetTicketHistoryResponse;
import de.dimedis.mobileentry.backend.response.GetVersionsResponse;
import de.dimedis.mobileentry.backend.response.GrabIdResponse;
import de.dimedis.mobileentry.backend.response.PerformCheckoutResponse;
import de.dimedis.mobileentry.backend.response.PerformEntryResponse;
import de.dimedis.mobileentry.backend.response.RecordEntryResponse;
import de.dimedis.mobileentry.backend.response.ResetDeviceResponse;
import de.dimedis.mobileentry.backend.response.ResponseError;
import de.dimedis.mobileentry.backend.response.SendHeartbeatResponse;
import de.dimedis.mobileentry.backend.response.ServerConnectResponse;
import de.dimedis.mobileentry.backend.response.ServerConnectResponseContent;
import de.dimedis.mobileentry.backend.response.StealIdResponse;
import de.dimedis.mobileentry.backend.response.UploadOfflineSessionsResponse;
import de.dimedis.mobileentry.backend.response.UserLoginByBarcodeResponse;
import de.dimedis.mobileentry.backend.response.UserLogoutResponse;
import de.dimedis.mobileentry.backend.response.Versions;
import de.dimedis.mobileentry.bbapi.Constants;
import de.dimedis.mobileentry.db.managers.DataBaseManager;
import de.dimedis.mobileentry.model.StatusManager;
import de.dimedis.mobileentry.service.HeartbeatService;
import de.dimedis.mobileentry.util.AppContext;
import de.dimedis.mobileentry.util.ConfigPrefHelper;
import de.dimedis.mobileentry.util.DataBaseUtil;
import de.dimedis.mobileentry.util.Logger;
import de.dimedis.mobileentry.util.PrefUtils;
import de.dimedis.mobileentry.util.SessionUtils;
import de.dimedis.mobileentry.util.dynamicres.DynamicString;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackendService extends Service {
    private static final String TAG = BackendService.class.getSimpleName();
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_SESSION_ALREADY_CLOSED = "session_already_closed";
    public static final String ACTION_SERVER_CONNECT = BuildConfig.APPLICATION_ID + ".ACTION_SERVER_CONNECT";
    public static final String ACTION_GRAB_ID = BuildConfig.APPLICATION_ID + ".ACTION_GRAB_ID";
    public static final String ACTION_STEAL_ID = BuildConfig.APPLICATION_ID + ".ACTION_STEAL_ID";
    public static final String ACTION_USER_LOGIN_BY_BARCODE = BuildConfig.APPLICATION_ID + ".ACTION_USER_LOGIN_BY_BARCODE";
    public static final String ACTION_USER_LOGIN_BY_USERNAME_AND_PASSWORD = BuildConfig.APPLICATION_ID + ".ACTION_USER_LOGIN_BY_USERNAME_AND_PASSWORD";
    public static final String ACTION_DOWNLOAD_SETTINGS = BuildConfig.APPLICATION_ID + ".ACTION_DOWNLOAD_SETTINGS";
    public static final String ACTION_DOWNLOAD_MYAVAILABLEBORDERS = BuildConfig.APPLICATION_ID + ".ACTION_DOWNLOAD_MYAVAILABLEBORDERS";
    public static final String ACTION_DOWNLOAD_OFFLINE_CONFIG = BuildConfig.APPLICATION_ID + ".ACTION_DOWNLOAD_OFFLINE_CONFIG";
    public static final String ACTION_GET_VERSIONS = BuildConfig.APPLICATION_ID + ".ACTION_GET_VERSIONS";
    public static final String ACTION_SEND_HEARTBEAT = BuildConfig.APPLICATION_ID + ".ACTION_SEND_HEARTBEAT";
    public static final String ACTION_GETTICKETHISTORY = BuildConfig.APPLICATION_ID + ".ACTION_GETTICKETHISTORY";
    public static final String ACTION_PERFORM_ENTRY = BuildConfig.APPLICATION_ID + ".ACTION_PERFORM_ENTRY";
    public static final String ACTION_PERFORM_CHECKOUT = BuildConfig.APPLICATION_ID + ".ACTION_PERFORM_CHECKOUT";
    public static final String ACTION_USER_LOGOUT = BuildConfig.APPLICATION_ID + ".ACTION_USER_LOGOUT";
    public static final String ACTION_RECORD_ENTRY = BuildConfig.APPLICATION_ID + ".ACTION_RECORD_ENTRY";
    public static final String ACTION_DOWNLOADLIBRARY = BuildConfig.APPLICATION_ID + ".ACTION_DOWNLOADLIBRARY";
    public static final String ACTION_BATCH_UPLOAD = BuildConfig.APPLICATION_ID + ".ACTION_BATCH_UPLOAD";
    public static final String ACTION_RESET_DEVICE = BuildConfig.APPLICATION_ID + ".ACTION_RESET_DEVICE";
    public static final String ACTION_DOWNLOADLANGUAGES = BuildConfig.APPLICATION_ID + ".ACTION_DOWNLOADLANGUAGES";

    public static final String EXTRA_REQUEST_ID = BuildConfig.APPLICATION_ID + ".EXTRA_REQUEST_ID";
    public static final String EXTRA_CUSTOMER_TOKEN = BuildConfig.APPLICATION_ID + ".EXTRA_CUSTOMER_TOKEN";
    public static final String EXTRA_DEVICE_ID = BuildConfig.APPLICATION_ID + ".EXTRA_DEVICE_ID";
    public static final String EXTRA_LANG = BuildConfig.APPLICATION_ID + ".EXTRA_LANG";
    public static final String EXTRA_COMM_KEY = BuildConfig.APPLICATION_ID + ".EXTRA_COMM_KEY";
    public static final String EXTRA_DEVICE_SUID = BuildConfig.APPLICATION_ID + ".EXTRA_DEVICE_SUID";
    public static final String EXTRA_LOGIN_BARCODE = BuildConfig.APPLICATION_ID + ".EXTRA_LOGIN_BARCODE";
    public static final String EXTRA_USER_NAME = BuildConfig.APPLICATION_ID + ".EXTRA_USER_NAME";
    public static final String EXTRA_USER_PASSWORD = BuildConfig.APPLICATION_ID + ".EXTRA_USER_PASSWORD";
    public static final String EXTRA_USER_SUID = BuildConfig.APPLICATION_ID + ".EXTRA_USER_SUID";
    public static final String EXTRA_LOCAL_RECORDS = BuildConfig.APPLICATION_ID + ".EXTRA_LOCAL_RECORDS";
    public static final String EXTRA_SYSTEM_HEALTH = BuildConfig.APPLICATION_ID + ".EXTRA_SYSTEM_HEALTH";
    public static final String EXTRA_VERSIONS = BuildConfig.APPLICATION_ID + ".EXTRA_VERSIONS";
    public static final String EXTRA_USER_SESSION = BuildConfig.APPLICATION_ID + ".EXTRA_USER_SESSION";
    public static final String EXTRA_FAIR = BuildConfig.APPLICATION_ID + ".EXTRA_FAIR";
    public static final String EXTRA_BORDER = BuildConfig.APPLICATION_ID + ".EXTRA_BORDER";
    public static final String EXTRA_TICKET_CODE = BuildConfig.APPLICATION_ID + ".EXTRA_TICKET_CODE";
    public static final String EXTRA_USER_PREFS = BuildConfig.APPLICATION_ID + ".EXTRA_USER_PREFS";
    public static final String EXTRA_DATA = BuildConfig.APPLICATION_ID + ".EXTRA_DATA";
    public static final String EXTRA_IDS_TO_REMOVE = "EXTRA_IDS_TO_REMOVE";
    public static final String ERROR_CODE_INVALID_BORDER = "INVALID_BORDER";
    public static final String ERROR_CODE_COMM_KEY_INVALID = "COMM_KEY_INVALID";
    private static Disposable mUploadSessionSubscription = null;

    private boolean mRedelivery;
    public static BackendApi sBackendApi;
    private static long sNextRequestId;
    private static int countTimeouts = 5; // was 0

    static {
        setupRestAdapter();
    }

    public static void setupRestAdapter() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY);
        logging.redactHeader("Authorization");
        logging.redactHeader("Cookie");

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(chain -> chain.proceed(chain.request().newBuilder().addHeader("Authorization", "Basic " + Base64.encodeToString((Config.USERNAME + ":" + Config.PASSWORD).getBytes(), Base64.NO_WRAP)).build()))
                .addInterceptor(logging)
                .readTimeout(PrefUtils.getOfflineDetectTimeout(), TimeUnit.MILLISECONDS)
                .build();

        Retrofit restAdapter;

        Gson gson = new GsonBuilder().setLenient().create();

        String rpcUrl = PrefUtils.getRpcUrl();
        rpcUrl = !TextUtils.isEmpty(rpcUrl) ? rpcUrl : Config.DISPATCHER_URL;
        if (!rpcUrl.endsWith("/")) {
            rpcUrl += "/";
        }
        if (BuildConfig.DEBUG) {
            restAdapter = new Retrofit.Builder()
                    .baseUrl(rpcUrl)
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        } else {
            restAdapter = new Retrofit.Builder().baseUrl(rpcUrl).addCallAdapterFactory(RxJava3CallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        }
        sBackendApi = restAdapter.create(BackendApi.class);
    }

    String nameService;

    public BackendService() {
        super();
        nameService = TAG;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @NonNull
    public static String getNextRequestId() {
        return Long.toString(sNextRequestId++);
    }

    public static void serverConnect(@NonNull String customerToken) {
        Context context = AppContext.get();
        Intent intent = new Intent(context, BackendService.class);
        intent.setAction(ACTION_SERVER_CONNECT);
        intent.putExtra(EXTRA_CUSTOMER_TOKEN, customerToken);
        context.startService(intent);
    }

    public static void grabId(@NonNull String deviceId, @NonNull String lang) {
        Context context = AppContext.get();
        Intent intent = new Intent(context, BackendService.class);
        intent.setAction(ACTION_GRAB_ID);
        intent.putExtra(EXTRA_DEVICE_ID, deviceId);
        intent.putExtra(EXTRA_LANG, lang);
        context.startService(intent);
    }

    public static void stealId(@NonNull String deviceId, @NonNull String lang) {
        Context context = AppContext.get();
        Intent intent = new Intent(context, BackendService.class);
        intent.setAction(ACTION_STEAL_ID);
        intent.putExtra(EXTRA_DEVICE_ID, deviceId);
        intent.putExtra(EXTRA_LANG, lang);
        context.startService(intent);
    }

    public static void downloadLanguages() {
        Context context = AppContext.get();
        Intent intent = createNewIntent(ACTION_DOWNLOADLANGUAGES);

        BaseArg.setArgToIntent(intent);
        context.startService(intent);
    }

    //downloadLibrary
    public static void downloadLibrary() {
        Context context = AppContext.get();
        Intent intent = new Intent(context, BackendService.class);
        intent.setAction(ACTION_DOWNLOADLIBRARY);
        BaseArg.setArgToIntent(intent);
        context.startService(intent);
    }

    public static void userLoginByBarcode(@NonNull String loginBarcode) {
        Context context = AppContext.get();
        Intent intent = createNewIntent(ACTION_USER_LOGIN_BY_BARCODE);

        BaseArg.setArgToIntent(intent);
        intent.putExtra(EXTRA_LOGIN_BARCODE, loginBarcode);
        context.startService(intent);
    }

    public static Intent createNewIntent(String action) {
        Context context = AppContext.get();
        Intent intent = new Intent(context, BackendService.class);
        intent.setAction(action);
        return intent;
    }

    //user_logout
    public static void userLogout() {
        Context context = AppContext.get();
        ConfigPref pref = MobileEntryApplication.getConfigPreferences();
        Intent intent = createNewIntent(ACTION_USER_LOGOUT);

        CommonArg.setArgToIntent(intent);
        String userPrefKeys = pref.userPrefs();
        intent.putExtra(EXTRA_USER_PREFS, userPrefKeys);
        AppContext.get().startService(intent);
    }

    //batch_upload
    public static void batchUpload(@NonNull String data, int[] ids) {
        Intent intent = createNewIntent(ACTION_BATCH_UPLOAD);
        CommonArg.setArgToIntent(intent);
        intent.putExtra(EXTRA_DATA, data);
        intent.putExtra(EXTRA_IDS_TO_REMOVE, ids);
        AppContext.get().startService(intent);
    }

    public static void downloadSettings() {
        Intent intent = createNewIntent(ACTION_DOWNLOAD_SETTINGS);
        BaseArg.setArgToIntent(intent);
        AppContext.get().startService(intent);
    }

    //DownloadMyAvailableBordersResponseContent
    public static void downloadMyAvailableBorders() {
        Intent intent = createNewIntent(ACTION_DOWNLOAD_MYAVAILABLEBORDERS);
        BaseArg.setArgToIntent(intent);
        AppContext.get().startService(intent);
    }

    public static void getVersions() {
        Intent intent = createNewIntent(ACTION_GET_VERSIONS);
        BaseArg.setArgToIntent(intent);
        AppContext.get().startService(intent);
    }

    //reset_device
    public static void resetDevice() {
        Intent intent = createNewIntent(ACTION_RESET_DEVICE);
        BaseArg.setArgToIntent(intent);
        AppContext.get().startService(intent);
    }

    public static void downloadOfflineConfig() {
        Intent intent = createNewIntent(ACTION_DOWNLOAD_OFFLINE_CONFIG);
        BaseArg.setArgToIntent(intent);
        AppContext.get().startService(intent);
    }

    static public String getJsonSystemHealth(Context context) {
        SystemHealth systemHealth = new SystemHealth(context);
        return new Gson().toJson(systemHealth);
    }

    static public String getJsonVersions() {
        Versions versions = ConfigPrefHelper.getVersions();
        return new Gson().toJson(versions);
    }

    public static void sendHeartbeat() {
        Intent intent = createNewIntent(ACTION_SEND_HEARTBEAT);
        BaseArg.setArgToIntent(intent);
        //system_health
        intent.putExtra(EXTRA_SYSTEM_HEALTH, getJsonSystemHealth(AppContext.get()));
        //current Versions
        intent.putExtra(EXTRA_VERSIONS, getJsonVersions());
        ConfigPref pref = MobileEntryApplication.getConfigPreferences();
        intent.putExtra(EXTRA_USER_SUID, pref.userSuid());
        intent.putExtra(EXTRA_USER_NAME, pref.userName());
        //local_records
        intent.putExtra(EXTRA_LOCAL_RECORDS, Boolean.toString(pref.localRecords()));
        AppContext.get().startService(intent);
    }


    //GetTicketHistoryResponse
    public static void getTicketHistory(@NonNull String ticket) {
        Intent intent = createNewIntent(ACTION_GETTICKETHISTORY);
        CommonArg.setArgToIntent(intent);
        intent.putExtra(EXTRA_TICKET_CODE, ticket);
        AppContext.get().startService(intent);
    }

    public static void userLoginByUsernameAndPassword(@NonNull String user_name, @NonNull String user_password) {
        Intent intent = createNewIntent(ACTION_USER_LOGIN_BY_USERNAME_AND_PASSWORD);
        BaseArg.setArgToIntent(intent);
        intent.putExtra(EXTRA_USER_NAME, user_name);
        intent.putExtra(EXTRA_USER_PASSWORD, user_password);
        AppContext.get().startService(intent);
    }

    public static void performEntry(@NonNull String requestId, @NonNull String ticketCode) {
        Intent intent = createNewIntent(ACTION_PERFORM_ENTRY);
        intent.putExtra(EXTRA_REQUEST_ID, requestId);
        intent.putExtra(EXTRA_TICKET_CODE, ticketCode);
        CommonArg.setArgToIntent(intent);
        AppContext.get().startService(intent);
        HeartbeatService.scheduleService();
    }

    public static void performCheckout(@NonNull String requestId, @NonNull String ticketCode) {
        Intent intent = createNewIntent(ACTION_PERFORM_CHECKOUT);
        intent.putExtra(EXTRA_REQUEST_ID, requestId);
        intent.putExtra(EXTRA_TICKET_CODE, ticketCode);
        CommonArg.setArgToIntent(intent);
        AppContext.get().startService(intent);
    }

    public static void recordEntry(@NonNull String requestId, @NonNull String ticketCode) {
        Intent intent = createNewIntent(ACTION_RECORD_ENTRY);
        intent.putExtra(EXTRA_REQUEST_ID, requestId);
        intent.putExtra(EXTRA_TICKET_CODE, ticketCode);
        CommonArg.setArgToIntent(intent);
        AppContext.get().startService(intent);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        onHandleIntent(intent, startId);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
    }

    public void setIntentRedelivery(boolean enabled) {
        mRedelivery = enabled;
    }

    protected void onHandleIntent(Intent intent, int startId) {
        switch (intent.getAction()) {
            case ACTION_SERVER_CONNECT:
                doServerConnect(startId, intent.getStringExtra(EXTRA_CUSTOMER_TOKEN));
                break;
            case ACTION_GRAB_ID:
                doGrabId(startId, intent.getStringExtra(EXTRA_DEVICE_ID), intent.getStringExtra(EXTRA_LANG));
                break;
            case ACTION_STEAL_ID:
                doStealId(startId, intent.getStringExtra(EXTRA_DEVICE_ID), intent.getStringExtra(EXTRA_LANG));
                break;
            case ACTION_USER_LOGIN_BY_BARCODE:
                doUserLoginByBarcode(startId, new BaseArg(intent), intent.getStringExtra(EXTRA_LOGIN_BARCODE));
                break;
            case ACTION_USER_LOGIN_BY_USERNAME_AND_PASSWORD:
                doUserLoginByUsernameAndPassword(startId, new BaseArg(intent), intent.getStringExtra(EXTRA_USER_NAME), intent.getStringExtra(EXTRA_USER_PASSWORD));
                break;
            case ACTION_DOWNLOAD_MYAVAILABLEBORDERS:
                doDownloadMyAvailableBorders(startId, new BaseArg(intent));
                break;
            case ACTION_DOWNLOAD_OFFLINE_CONFIG:
                doDownloadOfflineConfig(startId, new BaseArg(intent));
                break;
            case ACTION_DOWNLOAD_SETTINGS:
                doDownloadSettings(startId, new BaseArg(intent));
                break;

            case ACTION_GET_VERSIONS:
                doGetVersions(startId, new BaseArg(intent));
                break;

            case ACTION_RESET_DEVICE:
                doResetDevice(startId, new BaseArg(intent));
                break;
            case ACTION_SEND_HEARTBEAT:
                doSendHeartbeat(startId, new BaseArg(intent), intent.getStringExtra(EXTRA_SYSTEM_HEALTH), intent.getStringExtra(EXTRA_VERSIONS), intent.getStringExtra(EXTRA_USER_NAME), intent.getStringExtra(EXTRA_USER_SUID), intent.getStringExtra(EXTRA_LOCAL_RECORDS));
                break;

            case ACTION_GETTICKETHISTORY:
                doGetTicketHistory(startId, new CommonArg(intent), intent.getStringExtra(EXTRA_TICKET_CODE));
                break;

            case ACTION_PERFORM_ENTRY:
                doPerformEntry(startId, intent.getStringExtra(EXTRA_REQUEST_ID), new CommonArg(intent), intent.getStringExtra(EXTRA_TICKET_CODE));
                break;

            case ACTION_PERFORM_CHECKOUT:
                doPerformCheckout(startId, intent.getStringExtra(EXTRA_REQUEST_ID), new CommonArg(intent), intent.getStringExtra(EXTRA_TICKET_CODE));
                break;

            case ACTION_RECORD_ENTRY:
                doRecordEntry(startId, intent.getStringExtra(EXTRA_REQUEST_ID), new CommonArg(intent), intent.getStringExtra(EXTRA_TICKET_CODE));
                break;

            case ACTION_USER_LOGOUT:
                doUserLogout(startId, new CommonArg(intent), intent.getStringExtra(EXTRA_USER_PREFS));
                break;

            case ACTION_BATCH_UPLOAD:
                doBatchUpload(startId, new CommonArg(intent), intent.getStringExtra(EXTRA_DATA), intent.getIntArrayExtra(EXTRA_IDS_TO_REMOVE));
                break;

            case ACTION_DOWNLOADLIBRARY:
                doDownloadLibrary(startId, new BaseArg(intent));
                break;

            case ACTION_DOWNLOADLANGUAGES:
                doDownloadLanguages(startId, new BaseArg(intent));
                break;
        }
    }

    private void doServerConnect(int startId, @NonNull final String customerToken) {

        new DoRequestToServer<ServerConnectResponse>(this, startId, ServerConnectResponse.class) {

            @Override
            Observable<ServerConnectResponse> request() {
                return sBackendApi.serverConnect(customerToken);
            }

            @Override
            void postRequestOk(ServerConnectResponse serverConnectResponse) {
                ServerConnectResponseContent content = serverConnectResponse.content;
                if (!PrefUtils.getRpcUrl().equals(content.rpcUrl)) {
                    PrefUtils.setRpcUrl(content.rpcUrl);
                    BackendService.setupRestAdapter();
                }

                ConfigPref pref = MobileEntryApplication.getConfigPreferences();
                pref.setServerName(content.serverName);
                if (content.languages != null && content.languages.size() > 0) {
                    pref.setLanguagesContainer(new Gson().toJson(content.languages));
                } else {
                    Map<String, String> languages = new HashMap<>();
                    languages.put("en", "English");
                    languages.put("de", "Deutsch");
                    pref.setLanguagesContainer(new Gson().toJson(content.languages));
                }
            }
        }.doRequest();
    }

    private void doGrabId(int startId, @NonNull final String deviceId, @NonNull final String lang) {
        new DoRequestToServer<GrabIdResponse>(this, startId, GrabIdResponse.class) {
            @Override
            Observable<GrabIdResponse> request() {
                return sBackendApi.grabId(deviceId, lang);
            }

            @Override
            void postRequestOk(GrabIdResponse grabIdResponse) {
                // !refactoring
                MobileEntryApplication.getConfigPreferences().setCommKey(grabIdResponse.content.commKey);
                MobileEntryApplication.getConfigPreferences().setDeviceSuid(grabIdResponse.content.deviceSuid);
            }
        }.doRequest();
    }

    private void doStealId(int startId, @NonNull final String deviceId, @NonNull final String lang) {
        new DoRequestToServer<StealIdResponse>(this, startId, StealIdResponse.class) {
            @Override
            Observable<StealIdResponse> request() {
                return sBackendApi.stealId(deviceId, lang);
            }

            @Override
            void postRequestOk(StealIdResponse stealIdResponse) {
                MobileEntryApplication.getConfigPreferences().setCommKey(stealIdResponse.content.commKey);
                MobileEntryApplication.getConfigPreferences().setDeviceSuid(stealIdResponse.content.deviceSuid);
            }
        }.doRequest();
    }

    public static void uploadOfflineSessions(ArrayList<SessionImpl.SessionDB> sessions) {
        if (mUploadSessionSubscription != null && !mUploadSessionSubscription.isDisposed()) {
            Logger.w(TAG, "another upload is in the progress, unsubscribing");
            mUploadSessionSubscription.dispose();
        }

        Logger.i(TAG, sessions.toString());
        CommonArg arg = CommonArg.fromPreferences();
        String res = SessionUtils.buildOfflineSessionsRequestBody(sessions);
        Logger.w(TAG, "json: " + res);
        String active_user_session = "";
        if (PrefUtils.isLoginCompleted()) {
            active_user_session = MobileEntryApplication.getConfigPreferences().userSession();
        } else {
            // NOTE: do not provide this param if user is not logged in
        }

        sBackendApi.uploadOfflineSessions(arg.getLang(), arg.getCommKey(), arg.getDeviceSuid(), active_user_session, SessionUtils.buildOfflineSessionsRequestBody(sessions)).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Observer<UploadOfflineSessionsResponse>() {
            @Override
            public void onComplete() {
                unSubscribe();
            }

            @Override
            public void onError(Throwable e) {
                Logger.e(TAG, "uploadOfflineSessions ", e);
                unSubscribe();
            }

            private void unSubscribe() {
                if (mUploadSessionSubscription != null) {
                    mUploadSessionSubscription.dispose();
                    mUploadSessionSubscription = null;
                }
            }

            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                mUploadSessionSubscription = d;
            }

            @Override
            public void onNext(UploadOfflineSessionsResponse uploadOfflineSessionsResponse) {
                Logger.w(TAG, "response: " + uploadOfflineSessionsResponse.content);
                if (uploadOfflineSessionsResponse.error != null) {
                    if (isItInternalErrorAndWeNeedToWipeBadSession(uploadOfflineSessionsResponse.error)) {
                        Log.w(TAG, "isItInternalErrorAndWeNeedToWipeBadSession(uploadOfflineSessionsResponse.error");
                        onComplete();
                    } else {
                        Log.w(TAG, "onError(new Exception(uploadOfflineSessionsResponse.error.toString()));");
                        onError(new Exception(uploadOfflineSessionsResponse.error.toString()));
                    }
                    Log.w(TAG, "uploadOfflineSessionsResponse.error != null");
                    SessionUtils.logout(false, false, true);
                    DataBaseManager.getInstance().deleteOfflineUserSessions();
                    return;
                }
                if (!isStatusOk(uploadOfflineSessionsResponse)) {
                    Log.w(TAG, "status not ok");
                    return;
                }

                Log.w(TAG, "status: " + uploadOfflineSessionsResponse.content);
                switch (uploadOfflineSessionsResponse.content.status) {
                    case Constants.STATUS_REJECTED: {
                        Log.w(TAG, "rejected: " + uploadOfflineSessionsResponse.content);
                        SessionUtils.logout(false, false, true);
                        DataBaseManager.getInstance().deleteOfflineUserSessions();
                        break;
                    }
                    case Constants.STATUS_NO_ACTIVE_SESSION: {
                        DataBaseManager.getInstance().deleteOfflineUserSessions();
                        break;
                    }
                    case Constants.STATUS_ACCEPTED: {
                        SessionUtils.prolongSession(uploadOfflineSessionsResponse.content);
                        break;
                    }
                    default: {
                        onError(new Exception(uploadOfflineSessionsResponse.content.toString()));
                    }
                }
            }

            private boolean isItInternalErrorAndWeNeedToWipeBadSession(ResponseError error) {
                if (error == null || TextUtils.isEmpty(error.code)) {
                    return false;
                }
                if (error.code.equalsIgnoreCase(Constants.INTERNAL_ERROR)) {
                    if (!TextUtils.isEmpty(error.message)) {
                        processBadSession(error.message);
                    }
                    return true;
                }
                return false;
            }

            private void processBadSession(String message) {
                if (message.startsWith(Constants.BAD_SESSION_MARKER)) {
                    Log.e(TAG, "processBadSession:" + message);
                    String badSession = extractBadSessionId(message);
                    SessionUtils.logout(false, false, true);
                    DataBaseManager.getInstance().deleteOfflineUserSessions(badSession);
                }
            }

            private String extractBadSessionId(String message) {
                Pattern p = Pattern.compile("\"([0-9a-zA-Z]*-[0-9a-zA-Z]*-[0-9a-zA-Z]*-[0-9a-zA-Z]*)\"");
                Matcher m = p.matcher(message);
                if (m.find()) {
                    String sessionId = m.group(1);
                    Logger.w(TAG, "session id regex: " + sessionId);
                    return sessionId;
                }
                return null;
            }

            private boolean isStatusOk(UploadOfflineSessionsResponse uploadOfflineSessionsResponse) {
                return uploadOfflineSessionsResponse.content != null && uploadOfflineSessionsResponse.content.status != null && !TextUtils.isEmpty(uploadOfflineSessionsResponse.content.status);
            }
        });
    }

    static abstract class DoRequestToServer<T extends BaseResponse> {
        int COUNT_RETRY = 1;//3;
        Class<T> clazz;
        int startId;
        Service service;

        String getExLog() {
            return clazz.getSimpleName() + ": id:" + startId + " thr:" + Thread.currentThread().getName() + " | ";
        }

        DoRequestToServer(Service service, int startId, Class<T> clazz) {
            this.clazz = clazz;
            this.startId = startId;
            this.service = service;
        }

        abstract Observable<T> request();

        void postRequest(T t) {
        }

        void postRequestOk(T t) {
        }

        T getInstance() {
            T t = null;
            try {
                t = clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return t;
        }

        void doRequest() {
            doRequest(null);
        }

        void doRequest(final String requestId) {
            T t = null;
            request().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Observer<T>() {
                @Override
                public void onComplete() {
                    // DataBaseUtil.deleteTicketsById(ticketIdsToDeleteOnSuccess);
                    Log.i(TAG, getExLog() + "onCompleted");
                    service.stopSelf(startId);
                    countTimeouts = 0;
                }

                @Override
                public void onError(Throwable error) {
                    Log.e(TAG, getExLog() + "onError ", error);
                    Logger.e("Error", "error");
                    exception(error);
                    T t = getInstance();
                    if (t == null) {
                        return;
                    }
                    t.requestId = requestId;
                    t.result = BaseResponse.RESULT_ERROR;
                    t.throwable = error;//new RetrofitError( error );

                    if (t.error == null) {
                        t.error = new ResponseError();
                    }
                    if (t.error.message == null) {
                        t.error.message = (error != null) ? error.getMessage() : "";
                    }
                    if (isTimeoutError(error)) {
                        handleTimeoutError();
                    } else if (error instanceof UnknownHostException) {
                        handleUnknownHostError();
                    }
                    EventBus.getDefault().post(t);
                }

                private boolean isTimeoutError(Throwable error) {
                    return (error != null && error.getCause() instanceof InterruptedIOException &&
                            "timeout".equals(error.getCause().getMessage())) ||
                            (error instanceof SocketTimeoutException);
                }

                private void handleTimeoutError() {
                    countTimeouts++;
                    if (countTimeouts >= PrefUtils.getOfflineDetectCount()) {
                        // Only switch to LOCAL_SCAN if consecutive timeouts reach the desired count
                        StatusManager statusManager = StatusManager.getInstance();
                        statusManager.setStatus(StatusManager.Status.LOCAL_SCAN);
                        countTimeouts = 0; // Reset countTimeouts after switching to LOCAL_SCAN
                    }
                }

                private void handleUnknownHostError() {
                    StatusManager statusManager = StatusManager.getInstance();
                    statusManager.setStatus(StatusManager.Status.LOCAL_SCAN);
                    countTimeouts = 0;
                }


                @Override
                public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                }

                @Override
                public void onNext(T t) {
                    Log.i(TAG, getExLog() + "onNext t:" + t);
                    postRequest(t);
                    if (!t.isResultOk()) {
                        onError(t.throwable);
                    }
                    if (t.isResultOk() && (t.content instanceof BaseResponseContent) && ((BaseResponseContent) t.content).isStatusSuccess()) {
                        postRequestOk(t);
                    }
                    EventBus.getDefault().post(t);
                }
            });

        }

        void exception(Throwable error) {
        }
    }

    private void doUserLoginByBarcode(int startId, @NonNull final BaseArg arg, final @NonNull String loginBarcode) {
        new DoRequestToServer<UserLoginByBarcodeResponse>(this, startId, UserLoginByBarcodeResponse.class) {
            @Override
            Observable<UserLoginByBarcodeResponse> request() {
                return sBackendApi.userLoginByBarcodeBackend(arg.lang, arg.commKey, arg.deviceSuid, loginBarcode);
            }

            void postRequestOk(UserLoginByBarcodeResponse response) {
                SessionUtils.setUsersParam(response.content);
            }
        }.doRequest();
    }

    private void doUserLoginByUsernameAndPassword(int startId, @NonNull final BaseArg arg, @NonNull final String user_name, @NonNull final String user_password) {
        new DoRequestToServer<UserLoginByBarcodeResponse>(this, startId, UserLoginByBarcodeResponse.class) {

            @Override
            Observable<UserLoginByBarcodeResponse> request() {
                return sBackendApi.userLoginByUsernameAndPassword(arg.lang, arg.commKey, arg.deviceSuid, user_name, user_password);
            }

            @Override
            void postRequestOk(UserLoginByBarcodeResponse userLoginByBarcodeResponse) {
                SessionUtils.setUsersParam(userLoginByBarcodeResponse.content);
            }
        }.doRequest();
    }

    private void doDownloadSettings(int startId, @NonNull final BaseArg arg) {
        new DoRequestToServer<DownloadSettingsResponse>(this, startId, DownloadSettingsResponse.class) {
            @Override
            Observable<DownloadSettingsResponse> request() {
                return sBackendApi.downloadSettings(arg.lang, arg.commKey, arg.deviceSuid);
            }

            @Override
            void postRequestOk(DownloadSettingsResponse response) {
                PrefUtils.setHeartbeatIntervalIdle(response.content.heartbeatIntervalIdle);
                PrefUtils.setHeartbeatIntervalOnDuty(response.content.heartbeatIntervalOnDuty);
                PrefUtils.setOfflineDetectTimeout(response.content.offlineDetectTimeout);
                PrefUtils.setOfflineDetectCount(response.content.offlineDetectCount);
                PrefUtils.setScanOkSwitchDelay(response.content.scanOkSwitchDelay);
                PrefUtils.setScanDeniedSwitchDelay(response.content.scanDeniedSwitchDelay);
                PrefUtils.setScanCancelTimeout(response.content.scanCancelTimeout);
                PrefUtils.setScanDoubleDelay(response.content.scanDoubleScanDelay);
                MobileEntryApplication.getConfigPreferences().setHeartbeatInterval(response.content.heartbeatIntervalOnDuty);
            }
        }.doRequest();
    }

    private void doDownloadMyAvailableBorders(int startId, @NonNull final BaseArg arg) {
        new DoRequestToServer<DownloadMyAvailableBordersResponse>(this, startId, DownloadMyAvailableBordersResponse.class) {
            @Override
            Observable<DownloadMyAvailableBordersResponse> request() {
                return sBackendApi.downloadMyAvailableBorders(arg.lang, arg.commKey, arg.deviceSuid);
            }

            @Override
            void postRequestOk(DownloadMyAvailableBordersResponse downloadMyAvailableBordersResponse) {
                List<Border> borders = downloadMyAvailableBordersResponse.content.getListBorders();
                setBorders(downloadMyAvailableBordersResponse.content.getListBorders());
            }
        }.doRequest();
    }

    private void doDownloadLanguages(int startId, @NonNull final BaseArg arg) {
        new DoRequestToServer<DownloadLanguagesResponse>(this, startId, DownloadLanguagesResponse.class) {
            @Override
            Observable<DownloadLanguagesResponse> request() {
                return sBackendApi.downloadLanguages(arg.lang, arg.commKey, arg.deviceSuid);
            }

            @Override
            void postRequestOk(DownloadLanguagesResponse response) {
                //ConfigPrefHelper.getLanguges()
                ConfigPrefHelper.setLanguages(response.content.getLocalization());
                DynamicString.getInstance().init(response.content.getLocalization());
            }
        }.doRequest();
    }

    private void doDownloadOfflineConfig(final int startId, @NonNull final BaseArg arg) {
        sBackendApi.downloadOfflineConfig(arg.lang, arg.commKey, arg.deviceSuid).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Observer<JsonObject>() {
            @Override
            public void onComplete() {
                Log.i(TAG, "onCompleted");
                BackendService.this.stopSelf(startId);
            }

            void sendError() {
                DownloadOfflineConfigResponse docr = new DownloadOfflineConfigResponse();
                docr.result = BaseResponse.RESULT_ERROR;
                EventBus.getDefault().post(docr);
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "onError ", error);
                Logger.e("Error", "error");
                sendError();
            }

            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
            }

            @Override
            public void onNext(JsonObject response) {
                Log.i(TAG, "onNext response:" + response);
                try {
                    String conf = response.toString();
                    Log.w(TAG, "downloadOfflineConfig conf:" + conf.length() + " data:" + conf);
                    MobileEntryApplication.getConfigPreferences().setOfflineConfigServer(conf);

                    DownloadOfflineConfigResponse docr = new Gson().fromJson(conf, DownloadOfflineConfigResponse.class);
                    EventBus.getDefault().post(docr);
                } catch (Exception ex) {
                    Log.e(TAG, "downloadOfflineConfig:", ex);
                    sendError();
                }
            }
        });
    }

    private void doDownloadLibrary(final int startId, BaseArg baseArg) {
        sBackendApi.downloadLibrary(baseArg.lang, baseArg.commKey, baseArg.deviceSuid).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Observer<Response>() {
            @Override
            public void onComplete() {
                Log.i(TAG, "onCompleted");
                BackendService.this.stopSelf(startId);
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "onError ", error);
                Logger.e("Error", "error");
                EventBus.getDefault().post(new DownloadLibrary(null));
            }

            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
            }

            @Override
            public void onNext(Response response) {
                Log.i(TAG, "onNext response:" + response);

                FileOutputStream output = null;
                InputStream is = null;
                try {
                    is = response.raw().body().byteStream();
                    File pathCacheDir = getApplicationContext().getObbDir();
                    File newFile = File.createTempFile("Lib", "_temp.jar");
                    output = new FileOutputStream(newFile);

                    Log.e(TAG, "doDownloadLibrary pathCacheDir:" + pathCacheDir);
                    Log.e(TAG, "doDownloadLibrary libFile:" + newFile);

                    byte[] buff = new byte[1024 * 4];
                    int read = 0;
                    while ((read = is.read(buff)) != -1) {
                        output.write(buff, 0, read);
                        Log.w(TAG, "doDownloadLibrary output:" + output + " len:" + read);
                    }

                    EventBus.getDefault().post(new DownloadLibrary(newFile));

                } catch (Exception ex) {
                    Log.e(TAG, "doDownloadLibrary:", ex);
                    EventBus.getDefault().post(new DownloadLibrary(null));
                } finally {
                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    output = null;
                    is = null;
                }
            }
        });
    }

    private void doGetVersions(int startId, @NonNull final BaseArg arg) {
        new DoRequestToServer<GetVersionsResponse>(this, startId, GetVersionsResponse.class) {

            @Override
            Observable<GetVersionsResponse> request() {
                return sBackendApi.getVersionResponse(arg.lang, arg.commKey, arg.deviceSuid, arg.deviceType);
            }

            @Override
            void postRequestOk(GetVersionsResponse getVersionsResponse) {
                setServerVersions(getVersionsResponse.content.getVersions());
            }
        }.doRequest();
    }

    private void doResetDevice(int startId, @NonNull final BaseArg arg) {
        new DoRequestToServer<ResetDeviceResponse>(this, startId, ResetDeviceResponse.class) {

            @Override
            Observable<ResetDeviceResponse> request() {
                return sBackendApi.resetDevice(arg.lang, arg.commKey, arg.deviceSuid);
            }

            @Override
            void postRequestOk(ResetDeviceResponse resetDeviceResponse) {
            }
        }.doRequest();
    }

    private void doSendHeartbeat(int startId, final BaseArg baseArg, @NonNull final String system_health, @NonNull final String versions, @NonNull final String user_name, @NonNull final String user_suid, @NonNull final String local_records) {
        new DoRequestToServer<SendHeartbeatResponse>(this, startId, SendHeartbeatResponse.class) {
            @Override
            Observable<SendHeartbeatResponse> request() {
                return sBackendApi.sendHeartbeat(baseArg.lang, baseArg.commKey, baseArg.deviceSuid, system_health, versions, user_name, user_suid, local_records);
            }

            @Override
            void postRequestOk(SendHeartbeatResponse sendHeartbeatResponse) {
                super.postRequestOk(sendHeartbeatResponse);
                StatusManager.getInstance().setStatus(PrefUtils.isLocalScanEnabled() ? StatusManager.Status.LOCAL_SCAN : StatusManager.Status.ONLINE);
            }

            @Override
            void exception(Throwable error) {
                super.exception(error);
            }
        }.doRequest();
    }

    private void doGetTicketHistory(int startId, @NonNull final CommonArg cArg, @NonNull final String ticketCode) {
        new DoRequestToServer<GetTicketHistoryResponse>(this, startId, GetTicketHistoryResponse.class) {
            @Override
            Observable<GetTicketHistoryResponse> request() {
                return sBackendApi.getTicketHistory(cArg.lang, cArg.commKey, cArg.deviceSuid, cArg.getUserSession(), cArg.getUserSuid(), cArg.getUserName(), cArg.getFair(), cArg.getBorder(), ticketCode);
            }
        }.doRequest();
    }

    private void doPerformEntry(int startId, @NonNull final String requestId, @NonNull final CommonArg cArg, @NonNull final String ticketCode) {
        new DoRequestToServer<PerformEntryResponse>(this, startId, PerformEntryResponse.class) {
            @Override
            Observable<PerformEntryResponse> request() {
                Log.w(TAG, "performEntry> ticketCode:\"" + ticketCode + "\" " + "deviceSuid:\"" + cArg.deviceSuid + "\" " + "userSession:\"" + cArg.getUserSession() + "\" " + "userSuid:\"" + cArg.getUserSuid() + "\" " + "userName:\"" + cArg.getUserName() + "\" " + "fairIn:\"" + cArg.getFair() + "\" " + "border:\"" + cArg.getBorder() + "\" " + "lang:\"" + cArg.lang + "\" " +

                        "*commKey:\"" + cArg.commKey + "\" " + "*requestId:\"" + requestId);
                return sBackendApi.performEntry(requestId, cArg.lang, cArg.commKey, cArg.deviceSuid, cArg.getUserSession(), cArg.getUserSuid(), cArg.getUserName(), cArg.getFair(), cArg.getBorder(), ticketCode);
            }
        }.doRequest(requestId);
    }

    private void doPerformCheckout(int startId, @NonNull final String requestId, @NonNull final CommonArg cArg, @NonNull final String ticketCode) {
        new DoRequestToServer<PerformCheckoutResponse>(this, startId, PerformCheckoutResponse.class) {
            @Override
            Observable<PerformCheckoutResponse> request() {
                return sBackendApi.performCheckout(requestId, cArg.lang, cArg.commKey, cArg.deviceSuid, cArg.getUserSession(), cArg.getUserSuid(), cArg.getUserName(), cArg.getFair(), cArg.getBorder(), ticketCode);
            }
        }.doRequest(requestId);
    }

    //record_entry
    private void doRecordEntry(int startId, @NonNull final String requestId, @NonNull final CommonArg cArg, @NonNull final String ticketCode) {
        new DoRequestToServer<RecordEntryResponse>(this, startId, RecordEntryResponse.class) {
            @Override
            Observable<RecordEntryResponse> request() {
                return sBackendApi.recordEntry(requestId, cArg.lang, cArg.commKey, cArg.deviceSuid, cArg.getUserSession(), cArg.getUserSuid(), cArg.getUserName(), cArg.getFair(), cArg.getBorder(), ticketCode);
            }
        }.doRequest(requestId);
    }

    private void doUserLogout(int startId, @NonNull final CommonArg cArg, @NonNull final String userPrefs) {
        new DoRequestToServer<UserLogoutResponse>(this, startId, UserLogoutResponse.class) {
            @Override
            Observable<UserLogoutResponse> request() {
                return sBackendApi.userLogout(cArg.lang, cArg.commKey, cArg.deviceSuid, cArg.getUserSession(), cArg.getUserSuid(), cArg.getUserName(), cArg.getFair(), cArg.getBorder(), userPrefs);
            }

            @Override
            void postRequest(UserLogoutResponse userLogoutResponse) {
                super.postRequest(userLogoutResponse);
            }
        }.doRequest();
    }

    private void doBatchUpload(int startId, @NonNull final CommonArg cArg, @NonNull final String ticketsRawJsonArray, final int[] ticketIdsToDeleteOnSuccess) {
        new DoRequestToServer<BatchUploadResponse>(this, startId, BatchUploadResponse.class) {
            @Override
            Observable<BatchUploadResponse> request() {
                return sBackendApi.batchUpload2(cArg.lang, cArg.commKey, cArg.deviceSuid, cArg.getUserSession(), cArg.getUserSuid(), cArg.getUserName(), cArg.getFair(), cArg.getBorder(), ticketsRawJsonArray);
            }

            @Override
            void postRequest(BatchUploadResponse batchUploadResponse) {
                if (batchUploadResponse.isResultOk()) {
                    DataBaseUtil.deleteTicketsById(ticketIdsToDeleteOnSuccess);
                }
            }
        }.doRequest();
    }
}