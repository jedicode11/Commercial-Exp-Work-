package de.dimedis.mobileentry.util

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.JsonObject
import de.dimedis.mobileentry.ConfigPref
import de.dimedis.mobileentry.ConfigPref.offlineConfig
import de.dimedis.mobileentry.MobileEntryApplication
import de.dimedis.mobileentry.backend.BackendServiceConst
import de.dimedis.mobileentry.backend.BaseArg
import de.dimedis.mobileentry.backend.response.*
import de.dimedis.mobileentry.model.LocalModeHelper
import de.dimedis.mobileentry.util.dynamicres.DynamicString
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

object UpdateUtil : DownloadSettingsResponseContent() {
    const val TAG = "### update util ####"
    private var versionsFromServer: Versions? = null
    private var callback: ICompleteOperationCallback? = null
    private var mAppDownloadingSubscription: Disposable? = null
    private fun testVersions(versions: Versions, versionsServer: Versions?): Boolean {
        val isNeedUpdateLanguages = !versionsServer!!.languages.equals(versions.languages, ignoreCase = true)
        val isNeedUpdateBorders = !versionsServer.myAvailableBorders.equals(versions.myAvailableBorders, ignoreCase = true)
        val isNeedUpdateLocalConfig = !versionsServer.localConfig.equals(versions.localConfig, ignoreCase = true)
        val isNeedUpdateSettings = !versionsServer.settings.equals(versions.settings, ignoreCase = true)
        /*
        1) get_versions
        and if versions are different, do a selective call of the RPC call to download the appropriate resources:
        2a) download_settings
        2b) download_my_available_borders
        2c) download_offline_config
        2d) download_languages
         */
        Logger.e(TAG, String.format("update lang: %s, update border: %s, update localConfig: %s, update settings: %s",
                isNeedUpdateLanguages.toString() + "", isNeedUpdateBorders.toString() + "",
                isNeedUpdateLocalConfig.toString() + "", isNeedUpdateSettings))
        return isNeedUpdateBorders || isNeedUpdateLanguages || isNeedUpdateLocalConfig || isNeedUpdateSettings
    }

    private fun isNeedUpdateLanguages(versions: Versions, versionsServer: Versions?): Boolean {
        return !versionsServer!!.languages.equals(versions.languages, ignoreCase = true)
    }

    private fun isNeedUpdateBorders(versions: Versions, versionsServer: Versions?): Boolean {
        return !versionsServer!!.myAvailableBorders.equals(versions.myAvailableBorders, ignoreCase = true
        )
    }

    private fun isNeedUpdateSettings(versions: Versions, versionsServer: Versions?): Boolean {
        return !versionsServer!!.settings.equals(versions.settings, ignoreCase = true)
    }

    private fun isNeedUpdateLocalConfig(versions: Versions, versionsServer: Versions?): Boolean {
        return !versionsServer!!.localConfig.equals(versions.localConfig, ignoreCase = true)
    }

    var sSubscription: Disposable? = null
    @JvmOverloads
    fun updateSettings(callback: ICompleteOperationCallback? = null) {
        if (sSubscription == null || sSubscription!!.isDisposed) {
            // do work
            Logger.w(UpdateUtil::class.java.simpleName, "Starting update procedure")
        } else {
            // current update is in progress
            Logger.w(UpdateUtil::class.java.simpleName, "Update procedure already started")
            return
        }
        setCallback(callback)
        val baseArg = BaseArg()
        BackendServiceConst.sBackendApi?.getVersionResponse(baseArg.lang, baseArg.commKey, baseArg.deviceSuid, baseArg.deviceType
        )?.observeOn(Schedulers.io()
        )?.subscribeOn(Schedulers.io())?.subscribe(object : Observer<GetVersionsResponse> {
            override fun onComplete() {
                Logger.i(TAG, "versions loaded, checking, if there are update for us")
                if (testVersions(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                    Logger.i(TAG, "versions loaded, downloading settings")
                    if (isNeedUpdateSettings(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                        Logger.i(TAG, "loading SETTINGS")
                        loadSettings()
                    } else {
                        if (isNeedUpdateBorders(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                            Logger.i(TAG, "loading BORDERS")
                            loadBorders()
                        } else {
                            if (isNeedUpdateLocalConfig(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                                Logger.i(TAG, "loading OFFLINE CONFIG")
                                loadOfflineConfig()
                            } else {
                                if (isNeedUpdateLanguages(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                                    Logger.i(TAG, "loading LANGUAGES")
                                    loadLanguages()
                                } else {
                                    runCallback()
                                    sSubscription?.dispose()
                                    sSubscription = null
                                }
                            }
                        }
                    }
                } else {
                    Logger.i(TAG, "versions loaded, update is not needed")
                    runCallback()
                    sSubscription!!.dispose()
                    sSubscription = null
                }
            }

            override fun onError(e: Throwable) {
                Logger.i(TAG, "ERROR LOADING VERSIONS")
                e.printStackTrace()
                handleError()
            }

            override fun onSubscribe(d: Disposable) {
                sSubscription = d
            }

            override fun onNext(getVersionsResponse: GetVersionsResponse) {
                if (getVersionsResponse.isResultOk) {
                    versionsFromServer = getVersionsResponse.content?.versions
                    ConfigPrefHelper.setServerVersions(versionsFromServer)
                }
            }
        })
    }

    private fun handleError() {
        if (sSubscription != null && !sSubscription!!.isDisposed) {
            sSubscription!!.dispose()
        }
        sSubscription = null
        runCallback()
    }

    private fun runCallback() {
        if (getCallback() != null) {
            getCallback()!!.onOperationComplete()
        }
    }

    private fun loadSettings() {
        val baseArg = BaseArg()
        BackendServiceConst.sBackendApi?.downloadSettings(baseArg.lang, baseArg.commKey, baseArg.deviceSuid
        )?.observeOn(Schedulers.io()
        )?.subscribeOn(Schedulers.io())?.subscribe(object : Observer<DownloadSettingsResponse> {
            override fun onComplete() {
                if (isNeedUpdateBorders(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                    Logger.i(TAG, "loading BORDERS")
                    loadBorders()
                } else {
                    if (isNeedUpdateLocalConfig(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                        Logger.i(TAG, "loading OFFLINE CONFIG")
                        loadOfflineConfig()
                    } else {
                        if (isNeedUpdateLanguages(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                            Logger.i(TAG, "loading LANGUAGES")
                            loadLanguages()
                        } else {
                            runCallback()
                            sSubscription!!.dispose()
                            sSubscription = null
                        }
                    }
                }
            }

            override fun onError(e: Throwable) {
                Logger.i(TAG, "error loading BORDERS")
                e.printStackTrace()
                handleError()
            }

            override fun onSubscribe(d: Disposable) {
                sSubscription = d
            }

            override fun onNext(downloadSettingsResponse: DownloadSettingsResponse) {
                if (downloadSettingsResponse.isResultOk) {
                    downloadSettingsResponse.content?.let { PrefUtils.setHeartbeatIntervalIdle(it.heartbeatIntervalIdle) }
                    downloadSettingsResponse.content?.let { PrefUtils.setHeartbeatIntervalOnDuty(it.heartbeatIntervalOnDuty) }
                    downloadSettingsResponse.content?.let { PrefUtils.setOfflineDetectTimeout(it.offlineDetectTimeout) }
                    downloadSettingsResponse.content?.let { PrefUtils.setOfflineDetectCount(it.offlineDetectCount) }
                    downloadSettingsResponse.content?.let { PrefUtils.setScanOkSwitchDelay(it.scanOkSwitchDelay) }
                    downloadSettingsResponse.content?.let { PrefUtils.setScanDeniedSwitchDelay(it.scanDeniedSwitchDelay) }
                    downloadSettingsResponse.content?.let { PrefUtils.setScanCancelTimeout(it.scanCancelTimeout) }
                    downloadSettingsResponse.content?.let { PrefUtils.setScanDoubleDelay(it.scanDoubleScanDelay) }
                    val versions = ConfigPrefHelper.getVersions()
                    versions.settings = versionsFromServer?.settings
                    ConfigPrefHelper.setVersions(versions)
                    downloadSettingsResponse.content?.let { ConfigPref.heartbeatInterval = (it.heartbeatIntervalOnDuty) }
                }
            }
        })
    }

    private fun loadBorders() {
        val baseArg = BaseArg()
        BackendServiceConst.sBackendApi?.downloadMyAvailableBorders(baseArg.lang, baseArg.commKey, baseArg.deviceSuid
        )?.observeOn(Schedulers.io()
        )?.subscribeOn(Schedulers.io())?.subscribe(object : Observer<DownloadMyAvailableBordersResponse> {
                override fun onComplete() {
                    if (isNeedUpdateLocalConfig(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                        Logger.i(TAG, "loading OFFLINE CONFIG")
                        loadOfflineConfig()
                    } else {
                        if (isNeedUpdateLanguages(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                            Logger.i(TAG, "loading LANGUAGES")
                            loadLanguages()
                        } else {
                            runCallback()
                            sSubscription!!.dispose()
                            sSubscription = null
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    Logger.i(TAG, "error loading BORDERS")
                    e.printStackTrace()
                    handleError()
                }

                override fun onSubscribe(d: Disposable) {
                    sSubscription = d
                }

                override fun onNext(downloadMyAvailableBordersResponse: DownloadMyAvailableBordersResponse) {
                    if (downloadMyAvailableBordersResponse.isResultOk) {
                        val localVersions = ConfigPrefHelper.getVersions()
                        val isNeedUpdateBorders = versionsFromServer?.myAvailableBorders.equals(localVersions.myAvailableBorders, ignoreCase = true)
                        if (isNeedUpdateBorders) {
                            try {
                                ConfigPref.bordersContainer = (Gson().toJson(downloadMyAvailableBordersResponse.content?.listBorders))
                                val versions = ConfigPrefHelper.getVersions()
                                versions.myAvailableBorders = versionsFromServer!!.myAvailableBorders
                                ConfigPrefHelper.setVersions(versions)
                            } catch (ex: Exception) {
                                Logger.e("update borders on heartbeat", "unable to update borders! $ex")
                            }
                        }
                    }
                }
            })
    }

    fun loadOfflineConfig() {
        val baseArg = BaseArg()
//       val offlineConfig Observer<JsonObject> //Yasen
        val offlineConfig: Unit? = BackendServiceConst.sBackendApi?.downloadOfflineConfig(baseArg.lang, baseArg.commKey, baseArg.deviceSuid)?.observeOn(Schedulers.io())?.subscribeOn(Schedulers.io())
            ?.subscribe(object : Observer<JsonObject> {
//                ?: offlineConfig.subscribe(object : Observer<JsonObject> { //Yasen
            override fun onComplete() {
                EventBus.getDefault().post(LocationFragmentEvent(true))
                if (isNeedUpdateLanguages(ConfigPrefHelper.getVersions(), versionsFromServer)) {
                    Logger.i(TAG, "loading LANGUAGES")
                    loadLanguages()
                } else {
                    runCallback()
                    sSubscription!!.dispose()
                    sSubscription = null
                }
            }

            override fun onError(e: Throwable) {
                EventBus.getDefault().post(LocationFragmentEvent(false))
                Logger.i(TAG, "ERROR LOADING OFFLINE CONFIG")
                e.printStackTrace()
                handleError()
            }

            override fun onSubscribe(d: Disposable) {
                sSubscription = d
            }

            override fun onNext(response: JsonObject) {
                val localVersions = ConfigPrefHelper.getVersions()
                var isNeedUpdateConfig = true
                if (versionsFromServer != null) {
                    isNeedUpdateConfig = versionsFromServer?.localConfig.equals(localVersions.localConfig, ignoreCase = true)
                }
                if (isNeedUpdateConfig) {
                    try {
                        val conf = response.toString()
                        Logger.i(TAG, "downloadOfflineConfig conf:" + conf.length + " data:" + conf)
                        ConfigPref.offlineConfigServer = (conf)
                        LocalModeHelper.configLmLib(LocalModeHelper.getLocalMode(), conf)
                        val versions = ConfigPrefHelper.getVersions()
                        versions.localConfig = versionsFromServer?.localConfig
                        ConfigPrefHelper.setVersions(versions)
                    } catch (ex: Exception) {
                        Logger.e("update languages on heartbeat", "unable to update languages! $ex")
                    }
                }
            }
        })
    }

    private fun loadLanguages() {
        val baseArg = BaseArg()
        BackendServiceConst.sBackendApi?.downloadLanguages(baseArg.lang, baseArg.commKey, baseArg.deviceSuid
        )?.observeOn(Schedulers.io()
        )?.subscribeOn(Schedulers.io())?.subscribe(object : Observer<DownloadLanguagesResponse> {
            override fun onComplete() {
                Logger.i(TAG, "UPDATE IS DONE")
                runCallback()
                sSubscription!!.dispose()
                sSubscription = null
            }

            override fun onError(e: Throwable) {
                Logger.i(TAG, "ERROR LOADING OFFLINE CONFIG")
                e.printStackTrace()
                handleError()
            }

            override fun onSubscribe(d: Disposable) {
                sSubscription = d
            }

            override fun onNext(downloadLanguagesResponse: DownloadLanguagesResponse) {
                if (downloadLanguagesResponse.isResultOk) {
                    val localVersions = ConfigPrefHelper.getVersions()
                    val isNeedUpdateLanguages = versionsFromServer?.languages.equals(localVersions.languages, ignoreCase = true)
                    if (isNeedUpdateLanguages) {
                        try {
                            ConfigPrefHelper.setLanguages(downloadLanguagesResponse.content?.getLocalization())
                            DynamicString.instance.init(downloadLanguagesResponse.content?.getLocalization())
                            val versions = ConfigPrefHelper.getVersions()
                            versions.languages = versionsFromServer?.languages
                            ConfigPrefHelper.setVersions(versions)
                            ConfigPref.isLanguagesInit = (true)
                            DynamicString.instance.setLang(ConfigPref.currentLanguage)
                        } catch (ex: Exception) {
                            Logger.e("update languages on heartbeat", "unable to update languages! $ex")
                        }
                    }
                }
            }
        })
    }

    private fun updateLocalLibrary() {
        val baseArg = BaseArg()
        BackendServiceConst.sBackendApi?.downloadLibrary(baseArg.lang, baseArg.commKey, baseArg.deviceSuid
        )?.subscribeOn(Schedulers.io()
        )?.observeOn(Schedulers.io())?.subscribe(object : Observer<Response<*>> {
            override fun onComplete() {
                Logger.e(TAG, "lib is updated")
            }

            override fun onError(error: Throwable) {
                Logger.e("Error", "error")
                EventBus.getDefault().post(DownloadLibrary(null))
            }

            override fun onSubscribe(d: Disposable) {}
            override fun onNext(response: Response<*>) {
                Logger.e(TAG, "onNext response:$response")
                var output: FileOutputStream? = null
                try {
                    val pathCacheDir = AppContext.get().cacheDir
                    val newFile = File.createTempFile("Lib", "_temp.jar")
                    output = FileOutputStream(newFile)
                    Logger.e(TAG, "doDownloadLibrary pathCacheDir:$pathCacheDir")
                    Logger.e(TAG, "doDownloadLibrary libFile:$newFile")
                    val res = response as Response<String>
                    val body = res.body()
                    output.write(body!!.toByteArray(StandardCharsets.UTF_8))
                    Logger.w(TAG, "doDownloadLibrary output:" + output + " len:" + body.length)
                    updateLibWithNewLocalFile(newFile)
                } catch (ex: Exception) {
                    Logger.e(TAG, "doDownloadLibrary:", ex)
                    EventBus.getDefault().post(DownloadLibrary(null))
                } finally {
                    if (output != null) {
                        try {
                            output.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    output = null
                }
            }
        })
    }

    private fun updateLibWithNewLocalFile(newFile: File) {
        if (LocalModeHelper.initWithNewPathLibrary(newFile)) {
            ConfigPrefHelper.setVersions(ConfigPrefHelper.getVersionsFromServer())
        } else {
            newFile.delete()
            Logger.e(TAG, "Library init failed ...")
        }
    }

    fun setCallback(callback: ICompleteOperationCallback?) {
        UpdateUtil.callback = callback
    }

    fun getCallback(): ICompleteOperationCallback? {
        return callback
    }

    fun loadApp() {
        val baseArg = BaseArg()
        if (mAppDownloadingSubscription != null && !mAppDownloadingSubscription!!.isDisposed) {
            return
        }
        BackendServiceConst.sBackendApi?.downloadApp(baseArg.lang, baseArg.commKey, baseArg.deviceSuid, baseArg.deviceType
        )?.subscribeOn(
            Schedulers.io()
        )?.observeOn(Schedulers.io())?.subscribe(object : Observer<Response<ResponseBody>> {
            override fun onComplete() {
                Log.i(TAG, "onCompleted")
                if (mAppDownloadingSubscription != null) {
                    mAppDownloadingSubscription!!.dispose()
                    mAppDownloadingSubscription = null
                }
            }

            override fun onError(error: Throwable) {
                Log.e(TAG, "onError ", error)
                Logger.e("Error", "error")
                if (mAppDownloadingSubscription != null) {
                    mAppDownloadingSubscription!!.dispose()
                    mAppDownloadingSubscription = null
                }
            }

            override fun onSubscribe(d: Disposable) {
                mAppDownloadingSubscription = d
            }

            override fun onNext(response: Response<ResponseBody>) {
                Log.i(TAG, "onNext response:$response")
                var output: FileOutputStream? = null
                try {
                    val newFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath,
                        System.currentTimeMillis().toString() + "_temp.apk")
                    output = FileOutputStream(newFile)
                    Log.e(TAG, "doDownload app File:$newFile")
                    if (response.body()!!.contentType()!!.type == "text") {
                        Log.e(TAG, "apk download failed:" + response.body()!!.string())
                        return
                    }
                    output.write(response.body()!!.bytes())
                    output.flush()
                    val apkURI = MobileEntryApplication.getAppContext()?.let {
                        FileProvider.getUriForFile(it, MobileEntryApplication.getAppContext()?.packageName + ".provider", newFile.absoluteFile)
                    }
                    val install = Intent(Intent.ACTION_VIEW)
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                    install.data = apkURI
                    try {
                        AppContext.get().startActivity(install)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                        Log.e("TAG", "Error in opening the file!")
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, "doDownloadApp:", ex)
                } finally {
                    if (output != null) {
                        try {
                            output.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    output = null
                }
            }
        })
    }

    class LocationFragmentEvent(isLoaded: Boolean) {
        var isLoaded = false

        init {
            this.isLoaded = isLoaded
        }
    }
}