package de.dimedis.mobileentry.backend

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import de.dimedis.lmlib.SessionImpl.SessionDB
import de.dimedis.mobileentry.ConfigPref
import de.dimedis.mobileentry.UtilMethods
import de.dimedis.mobileentry.backend.ApiService.setupRestAdapter
import de.dimedis.mobileentry.backend.BackendServiceConst.EXTRA_USER_PASSWORD
import de.dimedis.mobileentry.backend.BackendServiceConst.TAG
import de.dimedis.mobileentry.backend.BackendServiceConst.countTimeouts
import de.dimedis.mobileentry.backend.BackendServiceConst.mUploadSessionSubscription
import de.dimedis.mobileentry.backend.BackendServiceConst.sBackendApi
import de.dimedis.mobileentry.backend.response.*
import de.dimedis.mobileentry.bbapi.Constants
import de.dimedis.mobileentry.db.managers.DataBaseManager
import de.dimedis.mobileentry.model.StatusManager
import de.dimedis.mobileentry.util.ConfigPrefHelper.setBorders
import de.dimedis.mobileentry.util.ConfigPrefHelper.setLanguages
import de.dimedis.mobileentry.util.ConfigPrefHelper.setServerVersions
import de.dimedis.mobileentry.util.DataBaseUtil.deleteTicketsById
import de.dimedis.mobileentry.util.Logger.e
import de.dimedis.mobileentry.util.Logger.i
import de.dimedis.mobileentry.util.Logger.w
import de.dimedis.mobileentry.util.PrefUtils
import de.dimedis.mobileentry.util.PrefUtils.isLoginCompleted
import de.dimedis.mobileentry.util.SessionUtils
import de.dimedis.mobileentry.util.SessionUtils.buildOfflineSessionsRequestBody
import de.dimedis.mobileentry.util.SessionUtils.logout
import de.dimedis.mobileentry.util.SessionUtils.setUsersParam
import de.dimedis.mobileentry.util.dynamicres.DynamicString
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import retrofit2.Response
import java.io.*
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.regex.Pattern


open class BackendService : IntentService("BackendService") {

    var nameService: String = TAG

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val result = super.onStartCommand(intent, flags, startId)
        onHandleIntent(intent, startId)
        return result
    }


    override fun onHandleIntent(intent: Intent?) {
        i(TAG, "======= onHandleIntent (intent).")
    }

    protected fun onHandleIntent(intent: Intent, startId: Int) {
        when (intent.action) {
            BackendServiceConst.ACTION_SERVER_CONNECT -> doServerConnect(startId, intent.getStringExtra(BackendServiceConst.EXTRA_CUSTOMER_TOKEN)!!)
            BackendServiceConst.ACTION_GRAB_ID -> doGrabId(startId, intent.getStringExtra(BackendServiceConst.EXTRA_DEVICE_ID)!!, intent.getStringExtra(BackendServiceConst.EXTRA_LANG)!!)
            BackendServiceConst.ACTION_STEAL_ID -> doStealId(startId, intent.getStringExtra(BackendServiceConst.EXTRA_DEVICE_ID)!!, intent.getStringExtra(BackendServiceConst.EXTRA_LANG)!!)
            BackendServiceConst.ACTION_USER_LOGIN_BY_BARCODE -> doUserLoginByBarcode(startId, BaseArg(intent), intent.getStringExtra(BackendServiceConst.EXTRA_LOGIN_BARCODE)!!)
            BackendServiceConst.ACTION_USER_LOGIN_BY_USERNAME_AND_PASSWORD -> doUserLoginByUsernameAndPassword(startId, BaseArg(intent), intent.getStringExtra(BackendServiceConst.EXTRA_USER_NAME)!!, intent.getStringExtra(EXTRA_USER_PASSWORD)!!)
            BackendServiceConst.ACTION_DOWNLOAD_MYAVAILABLEBORDERS -> doDownloadMyAvailableBorders(startId, BaseArg(intent))
            BackendServiceConst.ACTION_DOWNLOAD_OFFLINE_CONFIG -> doDownloadOfflineConfig(startId, BaseArg(intent))
            BackendServiceConst.ACTION_DOWNLOAD_SETTINGS -> doDownloadSettings(startId, BaseArg(intent))
            BackendServiceConst.ACTION_GET_VERSIONS -> doGetVersions(startId, BaseArg(intent))
            BackendServiceConst.ACTION_RESET_DEVICE -> doResetDevice(startId, BaseArg(intent))
            BackendServiceConst.ACTION_SEND_HEARTBEAT -> doSendHeartbeat(startId, BaseArg(intent), intent.getStringExtra(BackendServiceConst.EXTRA_SYSTEM_HEALTH)!!, intent.getStringExtra(BackendServiceConst.EXTRA_VERSIONS)!!, intent.getStringExtra(BackendServiceConst.EXTRA_USER_NAME)!!, intent.getStringExtra(BackendServiceConst.EXTRA_USER_SUID)!!, intent.getStringExtra(BackendServiceConst.EXTRA_LOCAL_RECORDS)!!)
            BackendServiceConst.ACTION_GETTICKETHISTORY -> doGetTicketHistory(startId, CommonArg(intent), intent.getStringExtra(BackendServiceConst.EXTRA_TICKET_CODE)!!)
            BackendServiceConst.ACTION_PERFORM_ENTRY -> doPerformEntry(startId, intent.getStringExtra(BackendServiceConst.EXTRA_REQUEST_ID)!!, CommonArg(intent), intent.getStringExtra(BackendServiceConst.EXTRA_TICKET_CODE)!!)
            BackendServiceConst.ACTION_PERFORM_CHECKOUT -> doPerformCheckout(startId, intent.getStringExtra(BackendServiceConst.EXTRA_REQUEST_ID)!!, CommonArg(intent), intent.getStringExtra(BackendServiceConst.EXTRA_TICKET_CODE)!!)
            BackendServiceConst.ACTION_RECORD_ENTRY -> doRecordEntry(startId, intent.getStringExtra(BackendServiceConst.EXTRA_REQUEST_ID)!!, CommonArg(intent), intent.getStringExtra(BackendServiceConst.EXTRA_TICKET_CODE)!!)
            BackendServiceConst.ACTION_USER_LOGOUT -> doUserLogout(startId, CommonArg(intent), intent.getStringExtra(BackendServiceConst.EXTRA_USER_PREFS)!!)
            BackendServiceConst.ACTION_BATCH_UPLOAD -> intent.getIntArrayExtra(BackendServiceConst.EXTRA_IDS_TO_REMOVE)?.let {
                    doBatchUpload(startId, CommonArg(intent), intent.getStringExtra(BackendServiceConst.EXTRA_DATA)!!, it) }
            BackendServiceConst.ACTION_DOWNLOADLIBRARY -> doDownloadLibrary(startId, BaseArg(intent))
            BackendServiceConst.ACTION_DOWNLOADLANGUAGES -> doDownloadLanguages(startId, BaseArg(intent))
        }
    }



    private fun doServerConnect(startId: Int, customerToken: String) {
//        val observable = request()?
//        observable.subscribeOn(Schedulers.io())?.observeOn(Schedulers.io())
//            ?.subscribe(object : Observer<T> {
//                override fun onComplete() {
//
//                }
        val observable = ApiService.backendApi().serverConnect("GhPqKkbxSyIHKkM7")
        observable.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe({ res ->
                val content = res.content
                if (PrefUtils.getRpcUrl() != content?.rpcUrl) {
                    PrefUtils.setRpcUrl(content?.rpcUrl)
                    if (PrefUtils.getRpcUrl()?.endsWith("/") == false) {
                        PrefUtils.setRpcUrl(content?.rpcUrl  + "/")
                    }
                    ApiService.setupRestAdapter()
                }
                ConfigPref.serverName = (content?.serverName)
                if (content?.languages != null && content.languages.isNotEmpty()) {
                    ConfigPref.languages = (Gson().toJson(content.languages))
                } else {
                    val languages: MutableMap<String, String> = HashMap()
                    languages["en"] = "English"
                    languages["de"] = "Deutsch"
                    ConfigPref.languages = (Gson().toJson(content?.languages))
                }
                ConfigPref.serverName = res.content?.serverName

                UtilMethods.hideLoading()

                EventBus.getDefault().post(res)

            }, { error -> UtilMethods.hideLoading()
                UtilMethods.showLongToast(this.applicationContext, error.message.toString())
            })

//        object : DoRequestToServer<ServerConnectResponse>(
//            this, startId, ServerConnectResponse::class.java) {
//            override fun request(): Observable<ServerConnectResponse>? {
//                return sBackendApi?.serverConnect(customerToken)
//            }
//
//            override fun postRequestOk(serverConnectResponse: ServerConnectResponse) {
//                val content = serverConnectResponse.content
//                if (PrefUtils.getRpcUrl() != content?.rpcUrl) {
//                    PrefUtils.setRpcUrl(content?.rpcUrl)
//                    setupRestAdapter()
//                }
//                val pref: ConfigPref? = MobileEntryApplication.configPreferences
//                pref?.serverName = (content?.serverName)
//                if (content?.languages != null && content.languages.isNotEmpty()) {
//                    pref?.languages = (Gson().toJson(content.languages))
//                } else {
//                    val languages: MutableMap<String, String> = HashMap()
//                    languages["en"] = "English"
//                    languages["de"] = "Deutsch"
//                    pref?.languages = (Gson().toJson(content?.languages))
//                }
//            }
//        }.doRequest()
    }


    private fun doServerConnect2(startId: Int, customerToken: String) {
        object : DoRequestToServer<ServerConnectResponse>(this, startId, ServerConnectResponse::class.java) {
            override fun request(): Observable<ServerConnectResponse>? {
                return sBackendApi?.serverConnect(customerToken)
            }

            override fun postRequestOk(serverConnectResponse: ServerConnectResponse) {
                val content = serverConnectResponse.content
                if (PrefUtils.getRpcUrl() != content?.rpcUrl) {
                    PrefUtils.setRpcUrl(content?.rpcUrl)
                    setupRestAdapter()
                }
                ConfigPref.serverName = (content?.serverName)
                if (content?.languages != null && content.languages.isNotEmpty()) {
                    ConfigPref.languages = (Gson().toJson(content.languages))
                } else {
                    val languages: MutableMap<String, String> = HashMap()
                    languages["en"] = "English"
                    languages["de"] = "Deutsch"
                    ConfigPref.languages = (Gson().toJson(content?.languages))
                }
            }
        }.doRequest()
    }

    internal abstract class DoRequestToServer<T : BaseResponse<*>>(var service: Service, var startId: Int, var clazz: Class<*>) {

        var COUNT_RETRY = 1 //3;
        fun getExLog(): String {
            return clazz.simpleName + ": id:" + startId + " thr:" + Thread.currentThread().name + " | "
        }

        abstract fun request(): Observable<T>?
        open fun postRequest(t: T) {}
        open fun postRequestOk(t: T) {}
        fun getInstance(): T? {
            var t: T? = null
            try {
                t = clazz.newInstance() as T?
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return t
        }

        @JvmOverloads
        fun doRequest(requestId: String? = null) {
            val t: T? = null
            request()?.subscribeOn(Schedulers.io())?.observeOn(Schedulers.io())
                ?.subscribe(object : Observer<T> {
                    override fun onComplete() {
                        // DataBaseUtil.deleteTicketsById(ticketIdsToDeleteOnSuccess);
                        Log.i(TAG, getExLog() + "onCompleted")
                        service.stopSelf(startId)
                        countTimeouts = 0
                    }

                    override fun onError(error: Throwable) {
                        Log.e(TAG, getExLog() + "onError ", error)
                        e("Error", "error")
                        exception(error)
                        val t = getInstance() ?: return
                        t.requestId = requestId
                        t.result = BaseResponse.RESULT_ERROR
                        t.throwable = error
                        if (t.error == null) t.error = ResponseError()
                        if (t.error?.message == null) {
                            if (error != null) t.error?.message =
                                error.message else t.error!!.message = ""
                        }
                        if (error != null && error.cause is InterruptedIOException && error.cause?.message == "timeout") {
                            countTimeouts++
                            if (countTimeouts >= PrefUtils.getOfflineDetectCount()) {
                                val statusManager: StatusManager? = StatusManager.getInstance()
                                statusManager?.setStatus(StatusManager.Status.LOCAL_SCAN)
                                countTimeouts = 0
                            }
                        } else if (error != null && error.cause is SocketTimeoutException) {
                            countTimeouts++
                            if (countTimeouts >= PrefUtils.getOfflineDetectCount()) {
                                val statusManager: StatusManager? = StatusManager.getInstance()
                                statusManager?.setStatus(StatusManager.Status.LOCAL_SCAN)
                                countTimeouts = 0
                            }
                        } else if (error != null && error is UnknownHostException) {
                            val statusManager: StatusManager? = StatusManager.getInstance()
                            statusManager?.setStatus(StatusManager.Status.LOCAL_SCAN)
                            countTimeouts = 0
                        } // new fix logic when manually switched off Wi-Fi
                        EventBus.getDefault().post(t)
                    }

                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(t: T) {
                        Log.i(TAG, getExLog() + "onNext t:" + t)
                        postRequest(t)
                        if (!t.isResultOk) {
                            t.throwable?.let { onError(it) }
                        }
                        if (t.isResultOk && t.content is BaseResponseContent && (t.content as BaseResponseContent).isStatusSuccess()) {
                            postRequestOk(t)
                        }
                        EventBus.getDefault().post(t)
                    }
                })
        }

        open fun exception(error: Throwable?) {
        }
    }

//    steal_id device_id=800&lang=en
    private fun doGrabId(startId: Int, deviceId: String, lang: String) {
        object : DoRequestToServer<GrabIdResponse>(this, startId, GrabIdResponse::class.java) {
            override fun request(): Observable<GrabIdResponse>? {
                return sBackendApi?.grabId(deviceId, lang)
            }
            override fun postRequestOk(grabIdResponse: GrabIdResponse) {
                ConfigPref.commKey = grabIdResponse.content?.commKey
                ConfigPref.deviceSuid = grabIdResponse.content?.commKey
            }
        }.doRequest()
    }

//    steal_id device_id=800&lang=en
    private fun doStealId(startId: Int, deviceId: String, lang: String) {
        object : DoRequestToServer<StealIdResponse>(this, startId, StealIdResponse::class.java) {
            override fun request(): Observable<StealIdResponse>? {
                return sBackendApi?.stealId(deviceId, lang)
            }
            override fun postRequestOk(stealIdResponse: StealIdResponse) {
                ConfigPref.commKey = stealIdResponse.content?.commKey
                ConfigPref.deviceSuid = stealIdResponse.content?.deviceSuid
            }
        }.doRequest()
    }

    fun uploadOfflineSessions(sessions: ArrayList<SessionDB?>) {
        if (mUploadSessionSubscription != null && mUploadSessionSubscription!!.isDisposed) {
            w(TAG, "another upload is in the progress, unsubscribing")
            mUploadSessionSubscription!!.dispose()
        }
        i(TAG, sessions.toString())
        val arg = CommonArg.fromPreferences()
        val res = buildOfflineSessionsRequestBody(sessions)
        w(TAG, "json: $res")
        var active_user_session = ""
        if (isLoginCompleted()) {
            active_user_session = ConfigPref.userSession.toString()
        } else {
        }

        sBackendApi?.uploadOfflineSessions(arg.lang, arg.commKey, arg.deviceSuid, active_user_session, buildOfflineSessionsRequestBody(sessions))?.subscribeOn(Schedulers.io())?.observeOn(Schedulers.io())?.subscribe(object : Observer<UploadOfflineSessionsResponse> {
                override fun onComplete() {
                    unSubscribe()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace();
                    e(TAG, "uploadOfflineSessions ", e)
                    unSubscribe()
                }

                private fun unSubscribe() {
                    if (mUploadSessionSubscription != null) {
                        mUploadSessionSubscription?.dispose()
                        mUploadSessionSubscription = null
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mUploadSessionSubscription = d
                }

                override fun onNext(uploadOfflineSessionsResponse: UploadOfflineSessionsResponse) {
                    w(TAG, "response: " + uploadOfflineSessionsResponse.content)
                    if (uploadOfflineSessionsResponse.error != null) {
                        if (isItInternalErrorAndWeNeedToWipeBadSession(uploadOfflineSessionsResponse.error!!)) {
                            Log.w(TAG, "isItInternalErrorAndWeNeedToWipeBadSession(uploadOfflineSessionsResponse.error")
                            onComplete()
                        } else {
                            Log.w(TAG, "onError(new Exception(uploadOfflineSessionsResponse.error.toString()));")
                            onError(java.lang.Exception(uploadOfflineSessionsResponse.error.toString()))
                        }
                        Log.w(TAG, "uploadOfflineSessionsResponse.error != null")
                        logout(forceOfflineLogout = false, shouldResetDeviceState = false, isSessionInvalid = true)
                        DataBaseManager.instance()!!.deleteOfflineUserSessions()
                        return
                    }
                    if (!isStatusOk(uploadOfflineSessionsResponse)) {
                        Log.w(TAG, "status not ok")
                        return
                    }
                    Log.w(TAG, "status: " + uploadOfflineSessionsResponse.content)
                    when (uploadOfflineSessionsResponse.content?.status) {
                        Constants.STATUS_REJECTED -> {
                            Log.w(TAG, "rejected: " + uploadOfflineSessionsResponse.content)
                            logout(forceOfflineLogout = false, shouldResetDeviceState = false, isSessionInvalid = true)
                            DataBaseManager.instance()?.deleteOfflineUserSessions()
                        }
                        Constants.STATUS_NO_ACTIVE_SESSION -> {
                            DataBaseManager.instance()?.deleteOfflineUserSessions()
                        }
                        Constants.STATUS_ACCEPTED -> {
                            SessionUtils.prolongSession(uploadOfflineSessionsResponse.content!!)
                        }
                        else -> {
                            onError(java.lang.Exception(uploadOfflineSessionsResponse.content.toString()))
                        }
                    }
                }

                private fun isItInternalErrorAndWeNeedToWipeBadSession(error: ResponseError): Boolean {
                    if (TextUtils.isEmpty(error.code)) {
                        return false
                    }
                    if (error.code.equals(Constants.INTERNAL_ERROR, ignoreCase = true)) {
                        if (!TextUtils.isEmpty(error.message)) {
                            processBadSession(error.message)
                        }
                        return true
                    }
                    return false
                }

                private fun processBadSession(message: String?) {
                    if (message!!.startsWith(Constants.BAD_SESSION_MARKER)) {
                        Log.e(TAG, "processBadSession:$message")
                        val badSession = extractBadSessionId(message)
                        logout(forceOfflineLogout = false, shouldResetDeviceState = false, isSessionInvalid = true)
                        DataBaseManager.instance()?.deleteOfflineUserSessions(badSession!!)
                    }
                }

                private fun extractBadSessionId(message: String?): String? {
                    val p =
                        Pattern.compile("\"([0-9a-zA-Z]*-[0-9a-zA-Z]*-[0-9a-zA-Z]*-[0-9a-zA-Z]*)\"")
                    val m = p.matcher(message.toString())
                    if (m.find()) {
                        val sessionId = m.group(1)
                        w(TAG, "session id regex: $sessionId")
                        return sessionId
                    }
                    return null
                }

                private fun isStatusOk(uploadOfflineSessionsResponse: UploadOfflineSessionsResponse): Boolean {
                    return uploadOfflineSessionsResponse.content != null && uploadOfflineSessionsResponse.content?.status != null && !TextUtils.isEmpty(uploadOfflineSessionsResponse.content?.status)
                }
            })
    }

    private fun doUserLoginByBarcode(startId: Int, arg: BaseArg, loginBarcode: String) {
        object : DoRequestToServer<UserLoginByBarcodeResponse>(this, startId, UserLoginByBarcodeResponse::class.java) {
            override fun request(): Observable<UserLoginByBarcodeResponse> {
                return sBackendApi!!.userLoginByBarcodeBackend(arg.lang, arg.commKey, arg.deviceSuid, loginBarcode)
            }

            override fun postRequestOk(response: UserLoginByBarcodeResponse) {
                setUsersParam(response.content!!)
            }
        }.doRequest()
    }

    private fun doUserLoginByUsernameAndPassword(startId: Int, arg: BaseArg, user_name: String, user_password: String) {
        object : DoRequestToServer<UserLoginByBarcodeResponse>(this, startId, UserLoginByBarcodeResponse::class.java) {
            override fun request(): Observable<UserLoginByBarcodeResponse> {
                return sBackendApi!!.userLoginByUsernameAndPassword(arg.lang, arg.commKey,
                    arg.deviceSuid, user_name, user_password)
            }

            override fun postRequestOk(userLoginByBarcodeResponse: UserLoginByBarcodeResponse) {
                setUsersParam(userLoginByBarcodeResponse.content!!)
            }
        }.doRequest()
    }

    private fun doDownloadSettings(startId: Int, arg: BaseArg) {
        object : DoRequestToServer<DownloadSettingsResponse>(this, startId, DownloadSettingsResponse::class.java) {
            override fun request(): Observable<DownloadSettingsResponse> {
                return sBackendApi!!.downloadSettings(arg.lang, arg.commKey, arg.deviceSuid)
            }

            override fun postRequestOk(response: DownloadSettingsResponse) {
                PrefUtils.setHeartbeatIntervalIdle(response.content!!.heartbeatIntervalIdle)
                PrefUtils.setHeartbeatIntervalOnDuty(response.content!!.heartbeatIntervalOnDuty)
                PrefUtils.setOfflineDetectTimeout(response.content!!.offlineDetectTimeout)
                PrefUtils.setOfflineDetectCount(response.content!!.offlineDetectCount)
                PrefUtils.setScanOkSwitchDelay(response.content!!.scanOkSwitchDelay)
                PrefUtils.setScanDeniedSwitchDelay(response.content!!.scanDeniedSwitchDelay)
                PrefUtils.setScanCancelTimeout(response.content!!.scanCancelTimeout)
                PrefUtils.setScanDoubleDelay(response.content!!.scanDoubleScanDelay)
                ConfigPref.heartbeatInterval = (response.content!!.heartbeatIntervalOnDuty)
            }
        }.doRequest()
    }

    private fun doDownloadMyAvailableBorders(startId: Int, arg: BaseArg) {
        object : DoRequestToServer<DownloadMyAvailableBordersResponse>(this, startId, DownloadMyAvailableBordersResponse::class.java) {
            override fun request(): Observable<DownloadMyAvailableBordersResponse> {
                return sBackendApi!!.downloadMyAvailableBorders(arg.lang, arg.commKey, arg.deviceSuid)
            }

            override fun postRequestOk(downloadMyAvailableBordersResponse: DownloadMyAvailableBordersResponse) {
                val borders = downloadMyAvailableBordersResponse.content!!.listBorders
                setBorders(downloadMyAvailableBordersResponse.content!!.listBorders)
            }
        }.doRequest()
    }

    private fun doDownloadLanguages(startId: Int, arg: BaseArg) {
        object : DoRequestToServer<DownloadLanguagesResponse>(this, startId, DownloadLanguagesResponse::class.java) {
            override fun request(): Observable<DownloadLanguagesResponse> {
                return sBackendApi!!.downloadLanguages(arg.lang, arg.commKey, arg.deviceSuid)
            }

            override fun postRequestOk(response: DownloadLanguagesResponse) {
                setLanguages(response.content!!.getLocalization())
                DynamicString.instance.init(response.content!!.getLocalization())
            }
        }.doRequest()
    }

    private fun doDownloadOfflineConfig(startId: Int, arg: BaseArg) {
        sBackendApi?.downloadOfflineConfig(arg.lang, arg.commKey, arg.deviceSuid)
            ?.subscribeOn(Schedulers.io())?.observeOn(Schedulers.io())
            ?.subscribe(object : Observer<JsonObject> {
                override fun onComplete() {
                    Log.i(TAG, "onCompleted")
                    this@BackendService.stopSelf(startId)
                }

                fun sendError() {
                    val docr = DownloadOfflineConfigResponse()
                    docr.result = BaseResponse.RESULT_ERROR
                    EventBus.getDefault().post(docr)
                }

                override fun onError(error: Throwable) {
                    Log.e(TAG, "onError ", error)
                    e("Error", "error")
                    sendError()
                }

                override fun onSubscribe(d: Disposable) {}
                override fun onNext(response: JsonObject) {
                    Log.i(TAG, "onNext response:$response")
                    try {
                        val conf = response.toString()
                        Log.w(TAG, "downloadOfflineConfig conf:" + conf.length + " data:" + conf) // BackendServiceConst
                        ConfigPref.offlineConfigServer = (conf)
                        val docr: DownloadOfflineConfigResponse = Gson().fromJson(conf,
                            DownloadOfflineConfigResponse::class.java)
                        EventBus.getDefault().post(docr)
                    } catch (ex: java.lang.Exception) {
                        Log.e(TAG, "downloadOfflineConfig:", ex)
                        sendError()
                    } finally {
                    }
                }
            })
    }

    private fun doDownloadLibrary(startId: Int, baseArg: BaseArg) {
         sBackendApi?.downloadLibrary(baseArg.lang, baseArg.commKey, baseArg.deviceSuid)?.subscribeOn(Schedulers.io())?.observeOn(Schedulers.io())?.subscribe(object : Observer<Response<*>> {
                override fun onComplete() {
                    Log.i(TAG, "onCompleted")
                    this@BackendService.stopSelf(startId)
                }

                override fun onError(error: Throwable) {
                    Log.e(TAG, "onError ", error)
                    e("Error", "error")
                    EventBus.getDefault().post(DownloadLibrary(null))
                }

                override fun onSubscribe(d: Disposable) {}
                override fun onNext(response: Response<*>) {
                    Log.i(TAG, "onNext response:$response")
                    var output: FileOutputStream? = null
                    var `is`: InputStream? = null
                    try {
                        `is` = response.raw().body!!.byteStream()
                        val pathCacheDir = applicationContext.cacheDir
                        val newFile = File.createTempFile("Lib", "_temp.jar")
                        output = FileOutputStream(newFile)
                        Log.e(TAG, "doDownloadLibrary pathCacheDir:$pathCacheDir")
                        Log.e(TAG, "doDownloadLibrary libFile:$newFile")
                        val buff = ByteArray(1024 * 4)
                        var read: Int
                        while (`is`.read(buff).also { read = it } != -1) {
                            output.write(buff, 0, read)
                            Log.w(TAG, "doDownloadLibrary output:$output len:$read")
                        }
                        EventBus.getDefault().post(DownloadLibrary(newFile))
                    } catch (ex: java.lang.Exception) {
                        Log.e(TAG, "doDownloadLibrary:", ex)
                        EventBus.getDefault().post(DownloadLibrary(null))
                    } finally {
                        if (output != null) {
                            try {
                                output.close()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                        if (`is` != null) {
                            try {
                                `is`.close()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                        output = null
                        `is` = null
                    }
                }
            })
    }

    private fun doGetVersions(startId: Int, arg: BaseArg) {
        object : DoRequestToServer<GetVersionsResponse>(this, startId, GetVersionsResponse::class.java) {
            override fun request(): Observable<GetVersionsResponse> {
                return sBackendApi!!.getVersionResponse(arg.lang, arg.commKey, arg.deviceSuid, arg.deviceType)
            }

            override fun postRequestOk(getVersionsResponse: GetVersionsResponse) {
                setServerVersions(getVersionsResponse.content!!.versions)
            }
        }.doRequest()
    }

    private fun doResetDevice(startId: Int, arg: BaseArg) {
        object : DoRequestToServer<ResetDeviceResponse>(this, startId, ResetDeviceResponse::class.java) {
            override fun request(): Observable<ResetDeviceResponse> {
                return sBackendApi!!.resetDevice(arg.lang, arg.commKey, arg.deviceSuid)
            }

            override fun postRequestOk(resetDeviceResponse: ResetDeviceResponse) {}
        }.doRequest()
    }

    private fun doSendHeartbeat(startId: Int, baseArg: BaseArg, system_health: String, versions: String,
        user_name: String, user_suid: String, local_records: String) {
        object : DoRequestToServer<SendHeartbeatResponse>(this, startId, SendHeartbeatResponse::class.java) {
            override fun request(): Observable<SendHeartbeatResponse> {
                return sBackendApi!!.sendHeartbeat(baseArg.lang, baseArg.commKey, baseArg.deviceSuid,
                    system_health, versions, user_name, user_suid, local_records)
            }

            override fun postRequestOk(sendHeartbeatResponse: SendHeartbeatResponse) {
                super.postRequestOk(sendHeartbeatResponse)
                StatusManager.getInstance()?.setStatus(if (PrefUtils.isLocalScanEnabled()) StatusManager.Status.LOCAL_SCAN else StatusManager.Status.ONLINE)
            }

            override fun exception(error: Throwable?) {
                super.exception(error)
            }
        }.doRequest()
    }

    private fun doGetTicketHistory(startId: Int, cArg: CommonArg, ticketCode: String) {
        object : DoRequestToServer<GetTicketHistoryResponse>(this, startId, GetTicketHistoryResponse::class.java) {
            override fun request(): Observable<GetTicketHistoryResponse> {
                return sBackendApi!!.getTicketHistory(cArg.lang, cArg.commKey, cArg.deviceSuid, cArg.userSession,
                    cArg.userSuid, cArg.userName, cArg.fair, cArg.border, ticketCode
                )
            }
        }.doRequest()
    }

    private fun doPerformEntry(startId: Int, requestId: String, cArg: CommonArg, ticketCode: String) {
        object : DoRequestToServer<PerformEntryResponse>(this, startId, PerformEntryResponse::class.java) {
            override fun request(): Observable<PerformEntryResponse> {
                Log.w(TAG, "performEntry> ticketCode:\"" + ticketCode + "\" " + "deviceSuid:\"" + cArg.deviceSuid + "\" " + "userSession:\"" + cArg.userSession + "\" " + "userSuid:\"" + cArg.userSuid + "\" " + "userName:\"" + cArg.userName + "\" " + "fair:\"" + cArg.fair + "\" " + "border:\"" + cArg.border + "\" " + "lang:\"" + cArg.lang + "\" " + "*commKey:\"" + cArg.commKey + "\" " + "*requestId:\"" + requestId)
                return sBackendApi!!.performEntry(requestId, cArg.lang, cArg.commKey, cArg.deviceSuid, cArg.userSession,cArg.userSuid,cArg.userName,cArg.fair,cArg.border,ticketCode)
            }
        }.doRequest(requestId)
    }

    private fun doPerformCheckout(startId: Int, requestId: String, cArg: CommonArg, ticketCode: String) {
        object : DoRequestToServer<PerformCheckoutResponse>(this, startId, PerformCheckoutResponse::class.java) {
            override fun request(): Observable<PerformCheckoutResponse> {
                return sBackendApi!!.performCheckout(requestId, cArg.lang, cArg.commKey, cArg.deviceSuid,
                    cArg.userSession, cArg.userSuid, cArg.userName, cArg.fair, cArg.border, ticketCode)
            }
        }.doRequest(requestId)
    }

    //record_entry
    private fun doRecordEntry(startId: Int, requestId: String, cArg: CommonArg, ticketCode: String) {
        object : DoRequestToServer<RecordEntryResponse>(this, startId, RecordEntryResponse::class.java) {
            override fun request(): Observable<RecordEntryResponse> {
                return sBackendApi!!.recordEntry(requestId, cArg.lang, cArg.commKey, cArg.deviceSuid, cArg.userSession,
                    cArg.userSuid, cArg.userName, cArg.fair, cArg.border, ticketCode)
            }
        }.doRequest(requestId)
    }

    private fun doUserLogout(startId: Int, cArg: CommonArg, userPrefs: String) {
        object :
            DoRequestToServer<UserLogoutResponse>(this, startId, UserLogoutResponse::class.java) {
            override fun request(): Observable<UserLogoutResponse> {
                return sBackendApi!!.userLogout(cArg.lang, cArg.commKey, cArg.deviceSuid, cArg.userSession,
                    cArg.userSuid, cArg.userName, cArg.fair, cArg.border, userPrefs)
            }

            override fun postRequest(userLogoutResponse: UserLogoutResponse) {
                super.postRequest(userLogoutResponse)
            }
        }.doRequest()
    }

    private fun doBatchUpload(startId: Int, cArg: CommonArg, ticketsRawJsonArray: String, ticketIdsToDeleteOnSuccess: IntArray) {
        object : DoRequestToServer<BatchUploadResponse>(this, startId, BatchUploadResponse::class.java) {
            override fun request(): Observable<BatchUploadResponse> {
                return sBackendApi!!.batchUpload2(cArg.lang, cArg.commKey, cArg.deviceSuid, cArg.userSession,
                    cArg.userSuid, cArg.userName, cArg.fair, cArg.border, ticketsRawJsonArray)
            }

            override fun postRequest(batchUploadResponse: BatchUploadResponse) {
                if (batchUploadResponse.isResultOk) {
                    deleteTicketsById(ticketIdsToDeleteOnSuccess)
                }
            }
        }.doRequest()
    }
}
