package de.dimedis.mobileentry;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import de.dimedis.mobileentry.model.StatusManager;
import de.dimedis.mobileentry.service.HeartbeatService;
import de.dimedis.mobileentry.util.AppContext;
import de.dimedis.mobileentry.util.CommonUtils;
import de.dimedis.mobileentry.util.DebugUtilsInterface;
import de.dimedis.mobileentry.util.Logger;
import de.dimedis.mobileentry.util.PrefUtils;
import de.dimedis.mobileentry.util.dynamicres.DynamicString;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.calligraphy3.R;
import io.github.inflationx.viewpump.ViewPump;

public class MobileEntryApplication extends Application implements OnSharedPreferenceChangeListener {
    static final String TAG = "MobileEntryApplication";

    static ConfigPref sConfigPref;

    private static Context context;

    static DebugUtilsInterface mDebugUtilsInterface;

    public static boolean isDemoMode() {
        return mDebugUtilsInterface != null && mDebugUtilsInterface.isDemoMode();
    }

    public static boolean isAdminMode() {
        return mDebugUtilsInterface != null && mDebugUtilsInterface.isAdminMode();
    }

    public static DebugUtilsInterface getDemoConf() {
        return mDebugUtilsInterface;
    }

    public static Context getAppContext() {
        return MobileEntryApplication.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MobileEntryApplication.context = getApplicationContext();
        sConfigPref = new ConfigPref_(MobileEntryApplication.context);

        AppContext.init(this);
        PrefUtils.init();
        EventBus.builder().logNoSubscriberMessages(false).installDefaultEventBus();

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        DynamicString.init(this, getConfigPreferences());
        PrefUtils.registerOnPrefChangeListener(this);
        updateStatusBarOverlay();
        registerConnectivityManager();
    }

    @Override
    public void onTerminate() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        connectivityManager.unregisterNetworkCallback(networkCallback);
        super.onTerminate();
    }

    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            Logger.i(TAG, "Connectivity changed, is Internet connected: " + CommonUtils.isInternetConnected());
            StatusManager.getInstance().updateStatus();
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            Logger.i(TAG, "Connectivity changed, is Internet connected: " + CommonUtils.isInternetConnected());
            StatusManager.getInstance().updateStatus();
        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);

            boolean hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);

            boolean canCommunicateWithServer = CommonUtils.isInternetAvailable();

            if (hasInternet && canCommunicateWithServer) {
                Logger.i(TAG, "Connected network has internet connectivity and can communicate with the server");
                StatusManager.getInstance().setStatus(StatusManager.Status.ONLINE);
            } else {
                Logger.i(TAG, "Connected network does not have internet connectivity or cannot communicate with the server");
                StatusManager.getInstance().setStatus(StatusManager.Status.OFFLINE);
            }
            CommonUtils.isInternetConnected(); // added to listen
            HeartbeatService.scheduleService();
        }
    };

    private void registerConnectivityManager() {
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        connectivityManager.requestNetwork(networkRequest, networkCallback);
    }

    public static ConfigPref getConfigPreferences() {
        return sConfigPref;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        Log.i(TAG, "onSharedPreferenceChanged key:" + key);
        Log.i(TAG, "onSharedPreferenceChanged...");
        updateStatusBarOverlay();
    }

    private void updateStatusBarOverlay() {
    }
}
