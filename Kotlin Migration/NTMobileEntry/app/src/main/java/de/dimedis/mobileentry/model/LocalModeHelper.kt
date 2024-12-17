package de.dimedis.mobileentry.model

import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import com.fasterxml.jackson.databind.ObjectMapper
import dalvik.system.DexClassLoader
import de.dimedis.lmlib.LocalModeImpl
import de.dimedis.lmlib.myinterfaces.Access
import de.dimedis.lmlib.myinterfaces.LocalMode
import de.dimedis.mobileentry.ConfigPref
import de.dimedis.mobileentry.util.AppContext.get
import de.dimedis.mobileentry.util.CommonUtils
import de.dimedis.mobileentry.util.ConfigPrefHelper
import de.dimedis.mobileentry.util.Logger
import de.dimedis.mobileentry.util.Logger.w
import de.dimedis.mobileentry.util.PrefUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.jar.JarFile

object LocalModeHelper {
    private val TAG = LocalModeHelper::class.java.simpleName
    private const val LOCAL_MODE_CLASS_NAME = "de.dimedis.lmlib.impl.LocalModeImpl"
    private const val LM_LIB_NAME = "lmlib.jar"
    private var sLocalMode: LocalMode? = null

    fun performEntry(ticketCode: String): Access? {
        return performEntry(PerfData().initFromPref(), ticketCode)
    }

    fun performEntry(pref: PerfData, ticketCode: String): Access? {
        val lm: LocalMode = getLocalMode()
        try {
            val access: Access? = lm.performEntry(ticketCode,
                pref.deviceSuid,
                pref.userSession,
                pref.userSuid,
                pref.userName,
                pref.fair,
                pref.border,
                pref.lang)
            Log.w(TAG, "access: $access")
            return access
        } catch (e: Exception) {
            w(TAG, "Failed to perform entry", e)
        }
        return null
    }

    fun performCheckout(ticketCode: String): Access? {
        return performCheckout(PerfData().initFromPref(), ticketCode)
    }

    fun performCheckout(pref: PerfData, ticketCode: String): Access? {
        val lm: LocalMode = getLocalMode()
        try {
            return lm.performCheckout(ticketCode,
                pref.deviceSuid,
                pref.userSession,
                pref.userSuid,
                pref.userName,
                pref.fair,
                pref.border,
                pref.lang)
        } catch (e: java.lang.Exception) {
            w(TAG, "Failed to perform checkout", e)
        }
        return null
    }

    fun recordEntry(ticketCode: String): Access? {
        val lm = getLocalMode()
        val border = ConfigPrefHelper.getUsersBorder()
        if (border != null) {
            try {
                return lm.recordEntry(ticketCode, ConfigPref.deviceSuid, ConfigPref.userSession, ConfigPref.userSuid, ConfigPref.userName, border.fair, border.border, ConfigPref.currentLanguage)
            } catch (e: Exception) {
                w(TAG, "Failed to record entry", e)
            }
        }
        return null
    }

    fun getLocalMode(): LocalMode {
        if (sLocalMode == null) {
            sLocalMode = load()
        }
        return sLocalMode as LocalMode
    }

    fun getLocalLibVersionFromManifestFile(fullPathToJar: File): String? {
        try {
            val jar = JarFile(fullPathToJar)
            val manifest = jar.manifest
            val mattr = manifest.mainAttributes
            val version = mattr.getValue("Implementation-Version")
            println("\\nVersion$version:\\n")
            if (version == null) {
                throw IOException("unable to read jar version from manifest")
            }
            return version
        } catch (fex: FileNotFoundException) {
            Log.e(TAG, "unable to find lib file: " + fullPathToJar.absolutePath)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return null
    }

    private fun initDefConfig() {
        var `is`: InputStream? = null
        try {
            `is` = get().assets.open("offline-test-config.json")
            val conf = CommonUtils.inputStreamToString(`is`)
            ConfigPref.offlineConfig = (conf)
            Log.w(TAG, "initDefConfig conf size: " + (conf?.length ?: "null") + " data:" + conf)
        } catch (th: Throwable) {
            Log.e(TAG, "configLmLib... ", th)
        } finally {
            CommonUtils.closeQuietly(`is`)
        }
    }

    fun configLmLib(lm: LocalMode, config: String): Boolean {
        try {
            if (TextUtils.isEmpty(config)) {
                Logger.e(TAG, "Config for lib empty")
                return false
            }
            val mapper = ObjectMapper()
            val actualObj = mapper.readTree(config)
            val test = actualObj.has("content") && actualObj["content"].has("config")
            val isConfigured = lm.configure(actualObj)
            Log.w(TAG, "LocalModeHelper test: $test")
            Log.w(TAG, "configLmLib isConfigured: " + isConfigured + " conf size: " + config.length + " data:" + config)
            return isConfigured
        } catch (th: Throwable) {
            Log.e(TAG, "configLmLib... ", th)
        }
        return false
    }

    fun initWithNewPathLibrary(libFile: File): Boolean {
        try {
            Logger.e("### local lib initialization ###", "local lib init started with: $libFile")

            val path = ContextCompat.getCodeCacheDir(get()).absoluteFile
            if (path.isDirectory) {
                val files = path.listFiles()
                for (fileDel in files!!) {
                    val status = fileDel.delete()
                    Log.i(TAG, "fileDel:" + fileDel.name + " status:" + status)
                }
            }
            PrefUtils.setLibraryVersion(libFile)
            val lm = load(libFile) ?: return false
            var status = false
            val offlineConfigServer = ConfigPref.offlineConfigServer
            if (!TextUtils.isEmpty(offlineConfigServer)) {
                status = offlineConfigServer?.let { configLmLib(lm, it) } == true
                ConfigPref.offlineConfig = (offlineConfigServer)
            }
            ConfigPref.lmlibFilePath = (libFile.absolutePath)
            Log.i(TAG, "initWithNewPathLibrary  status:$status")
            sLocalMode = lm
            return true
        } catch (e: Throwable) {
            w(TAG, "Failed to load lmlib", e)
        }
        return false
    }

    private fun firstInitLibrary(): LocalMode? {
        try {
            val context = get()
            val destFile = File(context.cacheDir, LM_LIB_NAME)
            CommonUtils.copyFileFromAssets(LM_LIB_NAME, destFile)
            PrefUtils.setLibraryVersion(destFile)
            val lm = load(destFile)
            ConfigPref.lmlibFilePath = (destFile.absolutePath)
            // offlineConfigServer
            if (TextUtils.isEmpty(ConfigPref.offlineConfig)) {
                initDefConfig()
            }
            return lm
        } catch (e: Exception) {
            w(TAG, "Failed to load lmlib", e)
        }
        return null
    }

    private fun load(): LocalMode {
        val path: String? = ConfigPref.lmlibFilePath
        val lm: LocalMode
        if (TextUtils.isEmpty(path)) {
            lm = firstInitLibrary()!!
        } else {
            lm = path?.let { File(it) }?.let { load(it) }!!
            PrefUtils.setLibraryVersion(path)
        }
        val offlineConfig = ConfigPref.offlineConfig
        if (offlineConfig != null) {
            configLmLib(lm, offlineConfig)
        }
        return lm
    }

    private fun load(destFile: File): LocalMode? {
        try {
            val context = get()
            val classLoader = DexClassLoader(
                destFile.absolutePath,
                ContextCompat.getCodeCacheDir(context).absolutePath,
                null,
                context.classLoader
            )
            val clazz = classLoader.loadClass(LOCAL_MODE_CLASS_NAME) as Class<*>
            return LocalModeImpl(clazz, clazz.newInstance())
        } catch (e: java.lang.Exception) {
            w(TAG, "Failed to load lmlib", e)
        }
        return null
    }
}