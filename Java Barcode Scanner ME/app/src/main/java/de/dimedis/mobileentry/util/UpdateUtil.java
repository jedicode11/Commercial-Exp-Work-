package de.dimedis.mobileentry.util;

import static de.dimedis.mobileentry.util.ConfigPrefHelper.setServerVersions;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import de.dimedis.mobileentry.MobileEntryApplication;
import de.dimedis.mobileentry.backend.BackendService;
import de.dimedis.mobileentry.backend.BaseArg;
import de.dimedis.mobileentry.backend.response.DownloadLanguagesResponse;
import de.dimedis.mobileentry.backend.response.DownloadLibrary;
import de.dimedis.mobileentry.backend.response.DownloadMyAvailableBordersResponse;
import de.dimedis.mobileentry.backend.response.DownloadSettingsResponse;
import de.dimedis.mobileentry.backend.response.DownloadSettingsResponseContent;
import de.dimedis.mobileentry.backend.response.GetVersionsResponse;
import de.dimedis.mobileentry.backend.response.Versions;
import de.dimedis.mobileentry.model.LocalModeHelper;
import de.dimedis.mobileentry.util.dynamicres.DynamicString;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class UpdateUtil extends DownloadSettingsResponseContent {

    public static final String TAG = "### update util ####";
    private static Versions versionsFromServer = null;
    private static ICompleteOperationCallback callback;
    private static Disposable mAppDownloadingSubscription = null;

    private static boolean testVersions(Versions versions, Versions versionsServer) {
        boolean isNeedUpdateLanguages = !versionsServer.getLanguages().equalsIgnoreCase(versions.getLanguages());
        boolean isNeedUpdateBorders = !versionsServer.getMyAvailableBorders().equalsIgnoreCase(versions.getMyAvailableBorders());
        boolean isNeedUpdateLocalConfig = !versionsServer.getLocalConfig().equalsIgnoreCase(versions.getLocalConfig());
        boolean isNeedUpdateSettings = !versionsServer.getSettings().equalsIgnoreCase(versions.getSettings());
        /*
        1) get_versions
        and if versions are different, do a selective call of the RPC call to download the appropriate resources:
        2a) download_settings
        2b) download_my_available_borders
        2c) download_offline_config
        2d) download_languages
         */
        Logger.e(TAG, String.format("update lang: %s, update border: %s, update localConfig: %s, update settings: %s", isNeedUpdateLanguages + "", isNeedUpdateBorders + "", isNeedUpdateLocalConfig + "", isNeedUpdateSettings));
        return isNeedUpdateBorders || isNeedUpdateLanguages || isNeedUpdateLocalConfig || isNeedUpdateSettings;
    }

    private static boolean isNeedUpdateLanguages(Versions versions, Versions versionsServer) {
        return !versionsServer.getLanguages().equalsIgnoreCase(versions.getLanguages());
    }

    private static boolean isNeedUpdateBorders(Versions versions, Versions versionsServer) {
        return !versionsServer.getMyAvailableBorders().equalsIgnoreCase(versions.getMyAvailableBorders());
    }

    private static boolean isNeedUpdateSettings(Versions versions, Versions versionsServer) {
        return !versionsServer.getSettings().equalsIgnoreCase(versions.getSettings());
    }

    private static boolean isNeedUpdateLocalConfig(Versions versions, Versions versionsServer) {
        return !versionsServer.getLocalConfig().equalsIgnoreCase(versions.getLocalConfig());
    }

    public static Disposable sSubscription = null;

    public static void updateSettings() {
        updateSettings(null);
    }

    public static void updateSettings(ICompleteOperationCallback callback) {
        if (sSubscription == null || sSubscription.isDisposed()) {
            // do work
            Logger.w(UpdateUtil.class.getSimpleName(), "Starting update procedure");
        } else {
            // current update is in progress
            Logger.w(UpdateUtil.class.getSimpleName(), "Update procedure already started");
            return;
        }

        setCallback(callback);

        BaseArg baseArg = new BaseArg();
        BackendService.sBackendApi.getVersionResponse(baseArg.getLang(), baseArg.getCommKey(), baseArg.getDeviceSuid(), baseArg.getDeviceType()).observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(new Observer<GetVersionsResponse>() {
            @Override
            public void onComplete() {
                Logger.i(TAG, "versions loaded, checking, if there are update for us");
                if (testVersions(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                    Logger.i(TAG, "versions loaded, downloading settings");
                    if (isNeedUpdateSettings(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                        Logger.i(TAG, "loading SETTINGS");
                        loadSettings();
                    } else {
                        if (isNeedUpdateBorders(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                            Logger.i(TAG, "loading BORDERS");
                            loadBorders();
                        } else {
                            if (isNeedUpdateLocalConfig(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                                Logger.i(TAG, "loading OFFLINE CONFIG");
                                loadOfflineConfig();
                            } else {
                                if (isNeedUpdateLanguages(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                                    Logger.i(TAG, "loading LANGUAGES");
                                    loadLanguages();
                                } else {
                                    runCallback();
                                    sSubscription.dispose();
                                    sSubscription = null;
                                }
                            }
                        }
                    }
                } else {
                    Logger.i(TAG, "versions loaded, update is not needed");
                    runCallback();
                    sSubscription.dispose();
                    sSubscription = null;
                }
            }

            @Override
            public void onError(Throwable e) {
                Logger.i(TAG, "ERROR LOADING VERSIONS");
                e.printStackTrace();
                handleError();
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                sSubscription = d;
            }

            @Override
            public void onNext(GetVersionsResponse getVersionsResponse) {
                if (getVersionsResponse != null && getVersionsResponse.isResultOk()) {
                    versionsFromServer = getVersionsResponse.content.getVersions();
                    setServerVersions(versionsFromServer);
                }
            }
        });
    }

    private static void handleError() {
        if (sSubscription != null && !sSubscription.isDisposed()) {
            sSubscription.dispose();
        }
        sSubscription = null;
        runCallback();
    }

    private static void runCallback() {
        if (getCallback() != null) {
            getCallback().onOperationComplete();
        }
    }

    private static void loadSettings() {
        BaseArg baseArg = new BaseArg();
        BackendService.sBackendApi.downloadSettings(baseArg.getLang(), baseArg.getCommKey(), baseArg.getDeviceSuid()).observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(new Observer<DownloadSettingsResponse>() {
            @Override
            public void onComplete() {
                if (isNeedUpdateBorders(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                    Logger.i(TAG, "loading BORDERS");
                    loadBorders();
                } else {
                    if (isNeedUpdateLocalConfig(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                        Logger.i(TAG, "loading OFFLINE CONFIG");
                        loadOfflineConfig();
                    } else {
                        if (isNeedUpdateLanguages(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                            Logger.i(TAG, "loading LANGUAGES");
                            loadLanguages();
                        } else {
                            runCallback();
                            sSubscription.dispose();
                            sSubscription = null;
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                Logger.i(TAG, "error loading BORDERS");
                e.printStackTrace();
                handleError();
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                sSubscription = d;
            }

            @Override
            public void onNext(DownloadSettingsResponse downloadSettingsResponse) {
                if (downloadSettingsResponse != null && downloadSettingsResponse.isResultOk()) {

                    PrefUtils.setHeartbeatIntervalIdle(downloadSettingsResponse.content.heartbeatIntervalIdle);
                    PrefUtils.setHeartbeatIntervalOnDuty(downloadSettingsResponse.content.heartbeatIntervalOnDuty);
                    PrefUtils.setOfflineDetectTimeout(downloadSettingsResponse.content.offlineDetectTimeout);
                    PrefUtils.setOfflineDetectCount(downloadSettingsResponse.content.offlineDetectCount);
                    PrefUtils.setScanOkSwitchDelay(downloadSettingsResponse.content.scanOkSwitchDelay);
                    PrefUtils.setScanDeniedSwitchDelay(downloadSettingsResponse.content.scanDeniedSwitchDelay);
                    PrefUtils.setScanCancelTimeout(downloadSettingsResponse.content.scanCancelTimeout);
                    PrefUtils.setScanDoubleDelay(downloadSettingsResponse.content.scanDoubleScanDelay);

                    Versions versions = ConfigPrefHelper.getVersions();
                    versions.setSettings(versionsFromServer.getSettings());
                    ConfigPrefHelper.setVersions(versions);
                    MobileEntryApplication.getConfigPreferences().setHeartbeatInterval(downloadSettingsResponse.content.heartbeatIntervalOnDuty);
                }
            }
        });
    }

    private static void loadBorders() {
        BaseArg baseArg = new BaseArg();
        BackendService.sBackendApi.downloadMyAvailableBorders(baseArg.getLang(), baseArg.getCommKey(), baseArg.getDeviceSuid()).observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(new Observer<DownloadMyAvailableBordersResponse>() {
            @Override
            public void onComplete() {
                if (isNeedUpdateLocalConfig(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                    Logger.i(TAG, "loading OFFLINE CONFIG");
                    loadOfflineConfig();
                } else {
                    if (isNeedUpdateLanguages(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                        Logger.i(TAG, "loading LANGUAGES");
                        loadLanguages();
                    } else {
                        runCallback();
                        sSubscription.dispose();
                        sSubscription = null;
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                Logger.i(TAG, "error loading BORDERS");
                e.printStackTrace();
                handleError();
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                sSubscription = d;
            }

            @Override
            public void onNext(DownloadMyAvailableBordersResponse downloadMyAvailableBordersResponse) {
                if (downloadMyAvailableBordersResponse != null && downloadMyAvailableBordersResponse.isResultOk()) {
                    Versions localVersions = ConfigPrefHelper.getVersions();
                    boolean isNeedUpdateBorders = !versionsFromServer.getMyAvailableBorders().equalsIgnoreCase(localVersions.getMyAvailableBorders());
                    if (isNeedUpdateBorders) {
                        try {
                            MobileEntryApplication.getConfigPreferences().setBordersContainer(new Gson().toJson(downloadMyAvailableBordersResponse.content.getListBorders()));
                            Versions versions = ConfigPrefHelper.getVersions();
                            versions.setMyAvailableBorders(versionsFromServer.getMyAvailableBorders());
                            ConfigPrefHelper.setVersions(versions);
                        } catch (Exception ex) {
                            Logger.e("update borders on heartbeat", "unable to update borders! " + ex);
                        }
                    }
                }
            }
        });
    }

    public static void loadOfflineConfig() {
        BaseArg baseArg = new BaseArg();
        Observable<JsonObject> offlineConfig = BackendService.sBackendApi.downloadOfflineConfig(baseArg.getLang(), baseArg.getCommKey(), baseArg.getDeviceSuid()).observeOn(Schedulers.io()).subscribeOn(Schedulers.io());

        offlineConfig.subscribe(new Observer<JsonObject>() {
            @Override
            public void onComplete() {
                EventBus.getDefault().post(new LocationFragmentEvent(true));
                if (isNeedUpdateLanguages(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                    Logger.i(TAG, "loading LANGUAGES");
                    loadLanguages();
                } else {
                    runCallback();
                    sSubscription.dispose();
                    sSubscription = null;
                }
            }

            @Override
            public void onError(Throwable e) {
                EventBus.getDefault().post(new LocationFragmentEvent(false));
                Logger.i(TAG, "ERROR LOADING OFFLINE CONFIG");
                e.printStackTrace();
                handleError();
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                sSubscription = d;
            }

            @Override
            public void onNext(JsonObject response) {
                if (response != null) {
                    Versions localVersions = ConfigPrefHelper.getVersions();
                    boolean isNeedUpdateConfig = true;
                    if (versionsFromServer != null) {
                        isNeedUpdateConfig = !versionsFromServer.getLocalConfig().equalsIgnoreCase(localVersions.getLocalConfig());
                    }
                    if (isNeedUpdateConfig) {
                        try {
                            String conf = response.toString();
                            Logger.i(TAG, "downloadOfflineConfig conf:" + conf.length() + " data:" + conf);
                            MobileEntryApplication.getConfigPreferences().setOfflineConfigServer(conf);

                            LocalModeHelper.configLmLib(LocalModeHelper.getLocalMode(), conf);

                            Versions versions = ConfigPrefHelper.getVersions();
                            versions.setLocalConfig(versionsFromServer.getLocalConfig());
                            ConfigPrefHelper.setVersions(versions);
                        } catch (Exception ex) {
                            Logger.e("update languages on heartbeat", "unable to update languages! " + ex);
                        }
                    }
                }
            }
        });
    }

    public static class LocationFragmentEvent {
        public boolean isLoaded = false;

        public LocationFragmentEvent(boolean isLoaded) {
            this.isLoaded = isLoaded;
        }
    }

    private static void loadLanguages() {
        BaseArg baseArg = new BaseArg();
        BackendService.sBackendApi.downloadLanguages(baseArg.getLang(), baseArg.getCommKey(), baseArg.getDeviceSuid()).observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(new Observer<DownloadLanguagesResponse>() {
            @Override
            public void onComplete() {
                Logger.i(TAG, "UPDATE IS DONE");
                runCallback();
                sSubscription.dispose();
                sSubscription = null;
            }

            @Override
            public void onError(Throwable e) {
                Logger.i(TAG, "ERROR LOADING OFFLINE CONFIG");
                e.printStackTrace();
                handleError();
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                sSubscription = d;
            }

            @Override
            public void onNext(DownloadLanguagesResponse downloadLanguagesResponse) {
                if (downloadLanguagesResponse != null && downloadLanguagesResponse.isResultOk()) {
                    Versions localVersions = ConfigPrefHelper.getVersions();
                    boolean isNeedUpdateLanguages = !versionsFromServer.getLanguages().equalsIgnoreCase(localVersions.getLanguages());

                    if (isNeedUpdateLanguages) {
                        try {
                            ConfigPrefHelper.setLanguages(downloadLanguagesResponse.content.getLocalization());
                            DynamicString.getInstance().init(downloadLanguagesResponse.content.getLocalization());
                            Versions versions = ConfigPrefHelper.getVersions();
                            versions.setLanguages(versionsFromServer.getLanguages());
                            ConfigPrefHelper.setVersions(versions);
                            MobileEntryApplication.getConfigPreferences().setIsLanguagesInit(true);
                            DynamicString.getInstance().setLang(MobileEntryApplication.getConfigPreferences().currentLanguage());
                        } catch (Exception ex) {
                            Logger.e("update languages on heartbeat", "unable to update languages! " + ex);
                        }
                    }
                }
            }
        });
    }

    private static void updateLocalLibrary() {
        BaseArg baseArg = new BaseArg();
        BackendService.sBackendApi.downloadLibrary(baseArg.getLang(), baseArg.getCommKey(), baseArg.getDeviceSuid())
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Observer<Response>() {
                    @Override
                    public void onComplete() {
                        Logger.e(TAG, "lib is updated");
                    }

                    @Override
                    public void onError(Throwable error) {
                        Logger.e("Error", "error");
                        EventBus.getDefault().post(new DownloadLibrary(null));
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(Response response) {
                        Logger.e(TAG, "onNext response:" + response);
                        FileOutputStream output = null;
                        try {
//                    File pathCacheDir = AppContext.get().getCacheDir();
//                    File pathCacheDir = AppContext.get().getObbDir();
                            File pathCacheDir = AppContext.get().getFilesDir();
                            File newFile = File.createTempFile("Lib", "_temp.jar");
                            output = new FileOutputStream(newFile);

                            Logger.e(TAG, "doDownloadLibrary pathCacheDir:" + pathCacheDir);
                            Logger.e(TAG, "doDownloadLibrary libFile:" + newFile);

                            Response<String> res = (Response<String>) response;
                            String body = res.body();
                            output.write(body.getBytes(StandardCharsets.UTF_8));
                            Logger.w(TAG, "doDownloadLibrary output:" + output + " len:" + body.length());

                            updateLibWithNewLocalFile(newFile);
                        } catch (Exception ex) {
                            Logger.e(TAG, "doDownloadLibrary:", ex);
                            EventBus.getDefault().post(new DownloadLibrary(null));
                        } finally {
                            if (output != null) {
                                try {
                                    output.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            output = null;
                        }
                    }
                });
    }

    private static void updateLibWithNewLocalFile(File newFile) {
        if (LocalModeHelper.initWithNewPathLibrary(newFile)) {
            ConfigPrefHelper.setVersions(ConfigPrefHelper.getVersionsFromServer());
        } else {
            newFile.delete();
            Logger.e(TAG, "Library init failed ...");
        }
    }

    public static void setCallback(ICompleteOperationCallback callback) {
        UpdateUtil.callback = callback;
    }

    public static ICompleteOperationCallback getCallback() {
        return callback;
    }

    public static void loadApp() {
        BaseArg baseArg = new BaseArg();

        if (mAppDownloadingSubscription != null && !mAppDownloadingSubscription.isDisposed()) {
            return;
        }

        BackendService.sBackendApi.downloadApp(baseArg.getLang(), baseArg.getCommKey(), baseArg.getDeviceSuid(), baseArg.getDeviceType()).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Observer<Response<ResponseBody>>() {
            @Override
            public void onComplete() {
                Log.i(TAG, "onCompleted");
                if (mAppDownloadingSubscription != null) {
                    mAppDownloadingSubscription.dispose();
                    mAppDownloadingSubscription = null;
                }
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "onError ", error);
                Logger.e("Error", "error");
//                        EventBus.getDefault().post(new DownloadLibrary(null));
                if (mAppDownloadingSubscription != null) {
                    mAppDownloadingSubscription.dispose();
                    mAppDownloadingSubscription = null;
                }
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mAppDownloadingSubscription = d;
            }

            @Override
            public void onNext(Response<ResponseBody> response) {
                Log.i(TAG, "onNext response:" + response);
                FileOutputStream output = null;
                try {
                    File newFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), System.currentTimeMillis() + "_temp.apk");
                    output = new FileOutputStream(newFile);
                    Log.e(TAG, "doDownload app File:" + newFile);
                    if (response.body().contentType().type().equals("text")) {
                        Log.e(TAG, "apk download failed:" + response.body().string());
                        return;
                    }
                    output.write(response.body().bytes());
                    output.flush();

                    Uri apkURI = FileProvider.getUriForFile(MobileEntryApplication.getAppContext(), MobileEntryApplication.getAppContext().getPackageName() + ".provider", newFile.getAbsoluteFile());

                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    install.setData(apkURI);
                    try {
                        AppContext.get().startActivity(install);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        Log.e("TAG", "Error in opening the file!");
                    }

                } catch (Exception ex) {
                    Log.e(TAG, "doDownloadApp:", ex);
                } finally {
                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    output = null;
                }
            }
        });
    }
}
