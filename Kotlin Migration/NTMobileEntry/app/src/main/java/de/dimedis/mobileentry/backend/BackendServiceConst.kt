package de.dimedis.mobileentry.backend

import de.dimedis.mobileentry.BuildConfig
import io.reactivex.rxjava3.disposables.Disposable

object BackendServiceConst {
    val TAG = BackendServiceConst::class.java.simpleName

    const val STATUS_SUCCESS = "success"
    const val STATUS_SESSION_ALREADY_CLOSED = "session_already_closed"
    const val ACTION_SERVER_CONNECT = BuildConfig.APPLICATION_ID + ".ACTION_SERVER_CONNECT"
    const val ACTION_GRAB_ID = BuildConfig.APPLICATION_ID + ".ACTION_GRAB_ID"
    const val ACTION_STEAL_ID = BuildConfig.APPLICATION_ID + ".ACTION_STEAL_ID"
    const val ACTION_USER_LOGIN_BY_BARCODE = BuildConfig.APPLICATION_ID + ".ACTION_USER_LOGIN_BY_BARCODE"
    const val ACTION_USER_LOGIN_BY_USERNAME_AND_PASSWORD = BuildConfig.APPLICATION_ID + ".ACTION_USER_LOGIN_BY_USERNAME_AND_PASSWORD"
    const val ACTION_DOWNLOAD_SETTINGS = BuildConfig.APPLICATION_ID + ".ACTION_DOWNLOAD_SETTINGS"
    const val ACTION_DOWNLOAD_MYAVAILABLEBORDERS = BuildConfig.APPLICATION_ID + ".ACTION_DOWNLOAD_MYAVAILABLEBORDERS"
    const val ACTION_DOWNLOAD_OFFLINE_CONFIG = BuildConfig.APPLICATION_ID + ".ACTION_DOWNLOAD_OFFLINE_CONFIG"
    const val ACTION_GET_VERSIONS = BuildConfig.APPLICATION_ID + ".ACTION_GET_VERSIONS"
    const val ACTION_SEND_HEARTBEAT = BuildConfig.APPLICATION_ID + ".ACTION_SEND_HEARTBEAT"
    const val ACTION_GETTICKETHISTORY = BuildConfig.APPLICATION_ID + ".ACTION_GETTICKETHISTORY"
    const val ACTION_PERFORM_ENTRY = BuildConfig.APPLICATION_ID + ".ACTION_PERFORM_ENTRY"
    const val ACTION_PERFORM_CHECKOUT = BuildConfig.APPLICATION_ID + ".ACTION_PERFORM_CHECKOUT"
    const val ACTION_USER_LOGOUT = BuildConfig.APPLICATION_ID + ".ACTION_USER_LOGOUT"
    const val ACTION_RECORD_ENTRY = BuildConfig.APPLICATION_ID + ".ACTION_RECORD_ENTRY"
    const val ACTION_DOWNLOADLIBRARY = BuildConfig.APPLICATION_ID + ".ACTION_DOWNLOADLIBRARY"
    const val ACTION_BATCH_UPLOAD = BuildConfig.APPLICATION_ID + ".ACTION_BATCH_UPLOAD"
    const val ACTION_RESET_DEVICE = BuildConfig.APPLICATION_ID + ".ACTION_RESET_DEVICE"
    const val ACTION_DOWNLOADLANGUAGES = BuildConfig.APPLICATION_ID + ".ACTION_DOWNLOADLANGUAGES"
    const val EXTRA_REQUEST_ID = BuildConfig.APPLICATION_ID + ".EXTRA_REQUEST_ID"
    const val EXTRA_CUSTOMER_TOKEN = BuildConfig.APPLICATION_ID + ".EXTRA_CUSTOMER_TOKEN"
    const val EXTRA_DEVICE_ID = BuildConfig.APPLICATION_ID + ".EXTRA_DEVICE_ID"
    const val EXTRA_LANG = BuildConfig.APPLICATION_ID + ".EXTRA_LANG"
    const val EXTRA_COMM_KEY = BuildConfig.APPLICATION_ID + ".EXTRA_COMM_KEY"
    const val EXTRA_DEVICE_SUID = BuildConfig.APPLICATION_ID + ".EXTRA_DEVICE_SUID"
    const val EXTRA_LOGIN_BARCODE = BuildConfig.APPLICATION_ID + ".EXTRA_LOGIN_BARCODE"
    const val EXTRA_USER_NAME = BuildConfig.APPLICATION_ID + ".EXTRA_USER_NAME"
    const val EXTRA_USER_PASSWORD = BuildConfig.APPLICATION_ID + ".EXTRA_USER_PASSWORD"
    const val EXTRA_USER_SUID = BuildConfig.APPLICATION_ID + ".EXTRA_USER_SUID"
    const val EXTRA_LOCAL_RECORDS = BuildConfig.APPLICATION_ID + ".EXTRA_LOCAL_RECORDS"
    const val EXTRA_SYSTEM_HEALTH = BuildConfig.APPLICATION_ID + ".EXTRA_SYSTEM_HEALTH"
    const val EXTRA_VERSIONS = BuildConfig.APPLICATION_ID + ".EXTRA_VERSIONS"
    const val EXTRA_USER_SESSION = BuildConfig.APPLICATION_ID + ".EXTRA_USER_SESSION"
    const val EXTRA_FAIR = BuildConfig.APPLICATION_ID + ".EXTRA_FAIR"
    const val EXTRA_BORDER = BuildConfig.APPLICATION_ID + ".EXTRA_BORDER"
    const val EXTRA_TICKET_CODE = BuildConfig.APPLICATION_ID + ".EXTRA_TICKET_CODE"
    const val EXTRA_USER_PREFS = BuildConfig.APPLICATION_ID + ".EXTRA_USER_PREFS"
    const val EXTRA_DATA = BuildConfig.APPLICATION_ID + ".EXTRA_DATA"
    const val EXTRA_IDS_TO_REMOVE = "EXTRA_IDS_TO_REMOVE"
    const val ERROR_CODE_INVALID_BORDER = "INVALID_BORDER"
    const val ERROR_CODE_COMM_KEY_INVALID = "COMM_KEY_INVALID"
    var mUploadSessionSubscription: Disposable? = null

    var sBackendApi: BackendApi? = null
    var countTimeouts = 0
}