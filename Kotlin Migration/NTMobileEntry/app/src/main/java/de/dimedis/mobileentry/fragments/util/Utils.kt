package de.dimedis.mobileentry.fragments.util

import android.content.*

import android.content.pm.PackageManager

object Utils {
    const val DEFVIERSION = "1.0.1"
    fun getVersionName(context: Context?, defVersion: String?): String? {
        return context?.let { getVersionName(it, defVersion, false) }
    }

    fun getVersionName(context: Context, defVersion: String?, isVersionCode: Boolean): String? {
        var defVersion = defVersion
        try {
            val pinfo = context.packageManager.getPackageInfo(context.packageName, 0)
            defVersion =
                pinfo.versionName + if (isVersionCode) "(" + pinfo.versionCode + ")" else ""
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return defVersion
    }

    fun getVersionName(context: Context?): String? {
        return getVersionName(context, DEFVIERSION)
    }

    fun getVersionName(context: Context?, isVersionCode: Boolean): String? {
        return context?.let { getVersionName(it, DEFVIERSION, isVersionCode) }
    }
}