package de.dimedis.mobileentry.backend

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import de.dimedis.mobileentry.ConfigPref
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_BATCH_UPLOAD
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_DOWNLOADLANGUAGES
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_DOWNLOADLIBRARY
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_DOWNLOAD_MYAVAILABLEBORDERS
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_DOWNLOAD_OFFLINE_CONFIG
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_DOWNLOAD_SETTINGS
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_GETTICKETHISTORY
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_GET_VERSIONS
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_GRAB_ID
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_PERFORM_CHECKOUT
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_PERFORM_ENTRY
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_RECORD_ENTRY
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_RESET_DEVICE
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_SEND_HEARTBEAT
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_SERVER_CONNECT
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_STEAL_ID
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_USER_LOGIN_BY_BARCODE
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_USER_LOGIN_BY_USERNAME_AND_PASSWORD
import de.dimedis.mobileentry.backend.BackendServiceConst.ACTION_USER_LOGOUT
import de.dimedis.mobileentry.backend.BackendServiceConst.EXTRA_CUSTOMER_TOKEN
import de.dimedis.mobileentry.backend.BackendServiceConst.EXTRA_DATA
import de.dimedis.mobileentry.backend.BackendServiceConst.EXTRA_DEVICE_ID
import de.dimedis.mobileentry.backend.BackendServiceConst.EXTRA_IDS_TO_REMOVE
import de.dimedis.mobileentry.backend.BackendServiceConst.EXTRA_LANG
import de.dimedis.mobileentry.backend.BackendServiceConst.EXTRA_LOCAL_RECORDS
import de.dimedis.mobileentry.backend.BackendServiceConst.EXTRA_LOGIN_BARCODE
import de.dimedis.mobileentry.backend.BackendServiceConst.EXTRA_REQUEST_ID
import de.dimedis.mobileentry.backend.BackendServiceConst.EXTRA_SYSTEM_HEALTH
import de.dimedis.mobileentry.backend.BackendServiceConst.EXTRA_TICKET_CODE
import de.dimedis.mobileentry.backend.BackendServiceConst.EXTRA_USER_NAME
import de.dimedis.mobileentry.backend.BackendServiceConst.EXTRA_USER_PASSWORD
import de.dimedis.mobileentry.backend.BackendServiceConst.EXTRA_USER_PREFS
import de.dimedis.mobileentry.backend.BackendServiceConst.EXTRA_USER_SUID
import de.dimedis.mobileentry.backend.BackendServiceConst.EXTRA_VERSIONS
import de.dimedis.mobileentry.service.HeartbeatService
import de.dimedis.mobileentry.util.AppContext
import de.dimedis.mobileentry.util.ConfigPrefHelper

object BackendServiceUtil {
    val TAG = BackendServiceConst::class.java.simpleName


    private var sNextRequestId: Long = 0

    fun getNextRequestId(): String {
        return (sNextRequestId++).toString()
    }

    fun serverConnect(customerToken: String) {
        val context = AppContext.get()
        val intent = Intent(context, BackendService::class.java)
        intent.action = ACTION_SERVER_CONNECT
        intent.putExtra(EXTRA_CUSTOMER_TOKEN, customerToken)
        context.startService(intent)
    }

    fun grabId(deviceId: String, lang: String) {
        val context = AppContext.get()
        val intent = Intent(context, BackendService::class.java)
        intent.action = ACTION_GRAB_ID
        intent.putExtra(EXTRA_DEVICE_ID, deviceId)
        intent.putExtra(EXTRA_LANG, lang)
        context.startService(intent)
    }

    fun stealId(deviceId: String, lang: String) {
        val context = AppContext.get()
        val intent = Intent(context, BackendService::class.java)
        intent.action = ACTION_STEAL_ID
        intent.putExtra(EXTRA_DEVICE_ID, deviceId)
        intent.putExtra(EXTRA_LANG, lang)
        context.startService(intent)
    }

    fun downloadLanguages() {
        val context = AppContext.get()
        val intent: Intent = createNewIntent(ACTION_DOWNLOADLANGUAGES)
        BaseArg.setArgToIntent(intent)
        context.startService(intent)
    }

    //downloadLibrary
    fun downloadLibrary() {
        val context = AppContext.get()
        val intent = Intent(context, BackendService::class.java)
        intent.action = ACTION_DOWNLOADLIBRARY
        BaseArg.setArgToIntent(intent)
        context.startService(intent)
    }

    fun userLoginByBarcode(loginBarcode: String) {
        val context = AppContext.get()
        val intent: Intent = createNewIntent(ACTION_USER_LOGIN_BY_BARCODE)
        BaseArg.setArgToIntent(intent)
        intent.putExtra(EXTRA_LOGIN_BARCODE, loginBarcode)
        context.startService(intent)
    }

    fun createNewIntent(action: String?): Intent {
        val context = AppContext.get()
        val intent = Intent(context, BackendService::class.java)
        intent.action = action
        return intent
    }

    //user_logout
    fun userLogout() {
        val context = AppContext.get()
        val intent: Intent = createNewIntent(ACTION_USER_LOGOUT)
        CommonArg.setArgToIntent(intent)
        val userPrefKeys = ConfigPref.userPrefs
        intent.putExtra(EXTRA_USER_PREFS, userPrefKeys)
        AppContext.get().startService(intent)
    }

    //batch_upload
    fun batchUpload(data: String, ids: IntArray?) {
        val intent: Intent = createNewIntent(ACTION_BATCH_UPLOAD)
        CommonArg.setArgToIntent(intent)
        intent.putExtra(EXTRA_DATA, data)
        intent.putExtra(EXTRA_IDS_TO_REMOVE, ids)
        AppContext.get().startService(intent)
    }

    fun downloadSettings() {
        val intent: Intent = createNewIntent(ACTION_DOWNLOAD_SETTINGS)
        BaseArg.setArgToIntent(intent)
        AppContext.get().startService(intent)
    }

    //DownloadMyAvailableBordersResponseContent
    fun downloadMyAvailableBorders() {
        val intent: Intent = createNewIntent(ACTION_DOWNLOAD_MYAVAILABLEBORDERS)
        BaseArg.setArgToIntent(intent)
        AppContext.get().startService(intent)
    }

    fun getVersions() {
        val intent: Intent = createNewIntent(ACTION_GET_VERSIONS)
        BaseArg.setArgToIntent(intent)
        AppContext.get().startService(intent)
    }

    //reset_device
    fun resetDevice() {
        val intent: Intent = createNewIntent(ACTION_RESET_DEVICE)
        BaseArg.setArgToIntent(intent)
        AppContext.get().startService(intent)
    }

    fun downloadOfflineConfig() {
        val intent: Intent = createNewIntent(ACTION_DOWNLOAD_OFFLINE_CONFIG)
        BaseArg.setArgToIntent(intent)
        AppContext.get().startService(intent)
    }

    fun getJsonSystemHealth(context: Context?): String {
        val systemHealth = context?.let { SystemHealth(it) }
        return Gson().toJson(systemHealth)
    }

    fun getJsonVersions(): String {
        val versions = ConfigPrefHelper.getVersions()
        return Gson().toJson(versions)
    }

    fun sendHeartbeat() {
        val intent: Intent = createNewIntent(ACTION_SEND_HEARTBEAT)
        BaseArg.setArgToIntent(intent)
        //system_health
        intent.putExtra(EXTRA_SYSTEM_HEALTH, getJsonSystemHealth(AppContext.get()))
        //current Versions
        intent.putExtra(EXTRA_VERSIONS, getJsonVersions())
        intent.putExtra(EXTRA_USER_SUID, ConfigPref.userSuid)
        intent.putExtra(EXTRA_USER_NAME, ConfigPref.userName)
        //local_records
        intent.putExtra(EXTRA_LOCAL_RECORDS, ConfigPref.localRecords.let { java.lang.Boolean.toString(it) })
        AppContext.get().startService(intent)
    }

    //GetTicketHistoryResponse
    fun getTicketHistory(ticket: String) {
        val intent: Intent = createNewIntent(ACTION_GETTICKETHISTORY)
        CommonArg.setArgToIntent(intent)
        intent.putExtra(EXTRA_TICKET_CODE, ticket)
        AppContext.get().startService(intent)
    }

    fun userLoginByUsernameAndPassword(user_name: String, user_password: String) {
        val intent: Intent = createNewIntent(ACTION_USER_LOGIN_BY_USERNAME_AND_PASSWORD)
        BaseArg.setArgToIntent(intent)
        intent.putExtra(EXTRA_USER_NAME, user_name)
        intent.putExtra(EXTRA_USER_PASSWORD, user_password)
        AppContext.get().startService(intent)
    }

    fun performEntry(requestId: String, ticketCode: String) {
        val intent: Intent = createNewIntent(ACTION_PERFORM_ENTRY)
        intent.putExtra(EXTRA_REQUEST_ID, requestId)
        intent.putExtra(EXTRA_TICKET_CODE, ticketCode)
        CommonArg.setArgToIntent(intent)
        AppContext.get().startService(intent)
        HeartbeatService.scheduleService()
    }

    fun performCheckout(requestId: String, ticketCode: String) {
        val intent: Intent = createNewIntent(ACTION_PERFORM_CHECKOUT)
        intent.putExtra(EXTRA_REQUEST_ID, requestId)
        intent.putExtra(EXTRA_TICKET_CODE, ticketCode)
        CommonArg.setArgToIntent(intent)
        AppContext.get().startService(intent)
    }

    fun recordEntry(requestId: String, ticketCode: String) {
        val intent: Intent = createNewIntent(ACTION_RECORD_ENTRY)
        intent.putExtra(EXTRA_REQUEST_ID, requestId)
        intent.putExtra(EXTRA_TICKET_CODE, ticketCode)
        CommonArg.setArgToIntent(intent)
        AppContext.get().startService(intent)
    }
}