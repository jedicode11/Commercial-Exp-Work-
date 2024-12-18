package de.dimedis.mobileentry.fragments.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class Utils {
    static final String DEFVIERSION = "1.0.1";

    public static String getVersionName(Context context, String defVersion) {
        return getVersionName(context, defVersion, false);
    }

    public static String getVersionName(Context context, String defVersion, boolean isVersionCode) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            defVersion = packageInfo.versionName + (isVersionCode ? " (" + packageInfo.versionCode + ")" : ""); // Added space before opening brackets
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return defVersion;
    }

    public static String getVersionName(Context context) {
        return getVersionName(context, DEFVIERSION);
    }

    public static String getVersionName(Context context, boolean isVersionCode) {
        return getVersionName(context, DEFVIERSION, isVersionCode);
    }
}
