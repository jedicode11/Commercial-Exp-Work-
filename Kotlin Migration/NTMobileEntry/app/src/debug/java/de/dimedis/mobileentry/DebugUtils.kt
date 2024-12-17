package de.dimedis.mobileentry

import android.content.Context
import de.dimedis.mobileentry.util.CommonUtils.hasBBApi
import de.dimedis.mobileentry.util.DebugUtilsInterface
import android.os.Build
import android.widget.Toast
import de.dimedis.mobileentry.util.DebugUtilsInterface.Reinit
import de.dimedis.mobileentry.util.CommonUtils
import java.lang.IllegalStateException

class DebugUtils internal constructor(var mContext: Context) : DebugUtilsInterface {
    // "ea378e347b3fdc1ff83462888a407957#dim.u.cava.2015110513-40-02.71049.1550.1&fmme_margot";
    // "e1f0a50c526191dbbeea258316fecf61#dim.u.cava.2015110513-40-34.57148.2072.1&fmme_dirk";
    override fun isAdminMode(): Boolean {
        return TEST_ADMIN_MODE
    }

    override fun getDemoAccBarCode(): String? {
        if (Build.SERIAL.equals(YAHOR_NEXUS7_SERIAL_NUMBER, ignoreCase = true)) {
//            return "f21cd22594dd75fc2d58059c3d50385f#dim.u.cava.2015111113-52-09.9568.31603.1&fmme_phillipp";
            return "68ba185d101e27c699ac241978b1bbf6#dim.u.cava.2015111113-52-33.54096.31863.1&fmme_klaus"
        }
        if (Build.SERIAL.equals(ELEPHAN_SERIAL_NUMBER, ignoreCase = true)) {
            // return "f21cd22594dd75fc2d58059c3d50385f#dim.u.cava.2015111113-52-09.9568.31603.1&fmme_phillipp";
            return "68ba185d101e27c699ac241978b1bbf6#dim.u.cava.2015111113-52-33.54096.31863.1&fmme_klaus"
        }
        throw IllegalStateException("Debug mode, please add your device mapping, unsupported device: " + Build.SERIAL)
    }

    override fun getDemoInitializeBarcode(): String? {
        return DEMO_INITIALIZE_BARCODE
    }

    override fun isDemoMode(): Boolean {
        return DEMO_MODE
    }

    override fun isDemoModeOn(): Boolean {
        return TEST_ADMIN_MODE_COUNT < 0
    }

    override fun getDefaultToken(): String? {
        return TEST_DEFAULT_TOKEN
    }

    var mToast: Toast? = null
    override fun onTitleClick(reinit: Reinit?) {
        if (isAdminMode()) {
            if (TEST_ADMIN_MODE_COUNT >= 0) {
                if (mToast != null) mToast!!.cancel()
                mToast =
                    Toast.makeText(mContext, "Admin: $TEST_ADMIN_MODE_COUNT", Toast.LENGTH_SHORT)
                --TEST_ADMIN_MODE_COUNT
                mToast?.show()
            } else {
                if (null != reinit) {
                    if (TEST_ADMIN_MODE_COUNT == -1) {
                        reinit.reinit()
                        TEST_ADMIN_MODE_COUNT--
                    } else {
                        reinit.next(TEST_ADMIN_MODE_COUNT)
                    }
                }
            }
        }
    }

    companion object {
        val TEST_ADMIN_MODE = BuildConfig.DEBUG
        private const val YAHOR_NEXUS7_SERIAL_NUMBER = "06d9e1e4"
        private const val ELEPHAN_SERIAL_NUMBER = "0123456789ABCDEF"
        private const val BLUEBIRD_DEVICE_SERIAL_NUMBER = "BP30ASSOFBA077"
        var TEST_ADMIN_MODE_COUNT = 3
        const val TEST_DEFAULT_TOKEN = "Fe2rf"
        val DEMO_MODE = !hasBBApi()
        const val DEMO_INITIALIZE_BARCODE = TEST_DEFAULT_TOKEN + "@" + Config.DISPATCHER_URL
        private var ISTANCE_DEBUGUTILS: DebugUtils? = null
        @JvmStatic
        fun init(context: Context) {
            if (ISTANCE_DEBUGUTILS == null) ISTANCE_DEBUGUTILS = DebugUtils(context)
        }

        @JvmStatic
        val instance: DebugUtilsInterface?
            get() = ISTANCE_DEBUGUTILS
    }
}