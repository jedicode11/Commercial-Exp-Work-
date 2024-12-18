package de.dimedis.mobileentry.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;

public class AppContext {
    static final String TAG = "AppContext";
    private static Context sContext;
    public static int hasBBApi = -1;

    private AppContext() {
    }

    @NonNull
    public static Context get() {
        return sContext;
    }

    public static void init(@NonNull Context context) {
        sContext = context.getApplicationContext();
    }

    public static boolean hasBBApi() {
        hasBBApi = 1;
        return true;
    }
}
