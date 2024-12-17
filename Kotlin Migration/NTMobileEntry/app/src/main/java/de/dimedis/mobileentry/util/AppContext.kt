package de.dimedis.mobileentry.util

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

object AppContext {
    val TAG = "AppContext"
    lateinit var sContext: Context
    var hasBBApi = -1
    fun get(): Context {
        return sContext!!
    }

    fun init(context: Context) {
        sContext = context.applicationContext
    }

    fun hasBBApi(): Boolean {
        hasBBApi = 1
        // if value already read, do not touch package manager
        if (hasBBApi != -1) {
            return hasBBApi > 0
        }
        val pm = get().packageManager
        try {
            Log.d(TAG, "package: " + pm.getPackageInfo("kr.co.bluebird.android.bbapi.barcodelibconnector",
                    PackageManager.GET_ACTIVITIES))
            hasBBApi = 1
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            hasBBApi = 0
        }
        return hasBBApi > 0
    }
}