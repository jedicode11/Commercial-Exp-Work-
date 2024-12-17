package de.dimedis.mobileentry

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.text.TextUtils
import android.util.Log
import de.dimedis.mobileentry.model.StatusManager
import de.dimedis.mobileentry.util.AppContext.init
import de.dimedis.mobileentry.util.CommonUtils.isInternetConnected
import de.dimedis.mobileentry.util.DebugUtilsInterface
import de.dimedis.mobileentry.util.Logger
import de.dimedis.mobileentry.util.PrefUtils
import de.dimedis.mobileentry.util.dynamicres.DynamicString
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.calligraphy3.R
import io.github.inflationx.viewpump.ViewPump
import org.greenrobot.eventbus.EventBus

open class MobileEntryApplication : Application(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        const val TAG = "MobileEntryApplication"
//        var sConfigPref: ConfigPref? = null
        private var context: Context? = null
        var mDebugUtilsInterface: DebugUtilsInterface? = null
        fun isDemoMode(): Boolean {
            return mDebugUtilsInterface != null && mDebugUtilsInterface!!.isDemoMode()
        }

        fun isAdminMode(): Boolean {
            return mDebugUtilsInterface != null && mDebugUtilsInterface!!.isAdminMode()
        }

        fun getDemoConf(): DebugUtilsInterface? {
            return mDebugUtilsInterface
        }

        fun getAppContext(): Context? {
            return context
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        init(this)
//        PrefUtils.init()  // probably is is not necessary anymore ???

        EventBus.builder().logNoSubscriberMessages(false).installDefaultEventBus()

        ViewPump.init(ViewPump.builder().addInterceptor(
                    CalligraphyInterceptor(CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build())
                )
                .build()
        )
        ConfigPref.let { DynamicString.init(this, it) }
        PrefUtils.registerOnPrefChangeListener(this)
        updateStatusBarOverlay()
        registerConnectivityManager()
    }

    private val networkCallback: NetworkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Logger.i(TAG, "Connectivity changed, is Internet connected: " + isInternetConnected())
            StatusManager.getInstance()!!.updateStatus()
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Logger.i(TAG, "Connectivity changed, is Internet connected: " + isInternetConnected())
            StatusManager.getInstance()!!.updateStatus()
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities)
        }
    }

    open fun registerConnectivityManager() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String) {
        Log.i(TAG, "onSharedPreferenceChanged key:$key")
        if (TextUtils.equals(key, PrefUtils.PREF_IS_KIOSK_MODE_ENABLED)) {
            Log.i(TAG, "onSharedPreferenceChanged...")
            updateStatusBarOverlay()
        }
    }

    private fun updateStatusBarOverlay() {}
}