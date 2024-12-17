package de.dimedis.mobileentry.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import java.lang.Exception
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.ExecutionException

import android.text.TextUtils
import kotlin.Throws
import android.os.AsyncTask
import kotlin.jvm.JvmOverloads
import android.os.Build
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Vibrator
import java.io.*

object CommonUtils {
    fun isInternetConnected(): Boolean {
        val connectivityManager = AppContext.get().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val network = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(network)
            actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            ) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(
                NetworkCapabilities.TRANSPORT_BLUETOOTH
            ))
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo
            nwInfo != null && nwInfo.isConnected
        }
    }

    fun isInternetAvailable(): Boolean {
        return try {
            getInetAddressByName("google.com").toString() != "" //equals
        } catch (e: Exception) {
            false
        }
    }

    fun getInetAddressByName(name: String): InetAddress? {
        val task: AsyncTask<String?, Void?, InetAddress?> =
            object : AsyncTask<String?, Void?, InetAddress?>() {
                protected override fun doInBackground(vararg params: String?): InetAddress? {
                    return try {
                        InetAddress.getByName(params[0])
                    } catch (e: UnknownHostException) {
                        null
                    }
                }
            }
        return try {
            task.execute(name).get()
        } catch (e: InterruptedException) {
            null
        } catch (e: ExecutionException) {
            null
        }
    }

    fun getDefaultHomePackage(): String {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = AppContext.get().packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return resolveInfo!!.activityInfo.packageName
    }

    fun getFirstHomePackage(): String? {
        val context = AppContext.get()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val activities =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in activities) {
            if (!TextUtils.equals(resolveInfo.activityInfo.packageName, context.packageName)) {
                return resolveInfo.activityInfo.packageName
            }
        }
        return null
    }

    @JvmOverloads
    fun startLauncher(packageName: String? = null) {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (!TextUtils.isEmpty(packageName)) {
            intent.setPackage(packageName)
        }
        AppContext.get().startActivity(intent)
    }

    fun setComponentEnabled(cls: Class<*>, isEnabled: Boolean) {
        val context = AppContext.get()
        context.packageManager.setComponentEnabledSetting(
            ComponentName(context, cls), if (isEnabled) PackageManager
                .COMPONENT_ENABLED_STATE_ENABLED else PackageManager
                .COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
    }

    @Throws(IOException::class)
    fun copyFileFromAssets(assetName: String, destFile: File) {
        var bis: BufferedInputStream? = null
        var os: OutputStream? = null
        try {
            bis = BufferedInputStream(AppContext.get().assets.open(assetName))
            os = BufferedOutputStream(FileOutputStream(destFile))
            copyStream(bis, os)
        } finally {
            closeQuietly(bis)
            closeQuietly(os)
        }
    }

    @Throws(IOException::class)
    fun inputStreamToString(`is`: InputStream): String? {
        var bis: BufferedInputStream? = null
        val os: OutputStream? = null
        var baos: ByteArrayOutputStream? = null
        try {
            bis = BufferedInputStream(`is`)
            baos = ByteArrayOutputStream()
            copyStream(bis, baos)
            return baos.toString()
        } catch (th: Throwable) {
            th.printStackTrace()
        } finally {
            closeQuietly(bis)
            closeQuietly(baos)
        }
        return null
    }

    @Throws(IOException::class)
    fun copyStream(`is`: InputStream, os: OutputStream) {
        val BUF_SIZE = 8 * 1024
        val buf = ByteArray(BUF_SIZE)
        var len: Int
        while (`is`.read(buf, 0, BUF_SIZE).also { len = it } > 0) {
            os.write(buf, 0, len)
        }
    }

    fun closeQuietly(closeable: Closeable?) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (ignored: IOException) {
            }
        }
    }

    fun vibrate() {
        val vibrator = AppContext.get().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(100)
        }
    }

    fun hasBBApi(): Boolean {
        return AppContext.hasBBApi()
    }
}