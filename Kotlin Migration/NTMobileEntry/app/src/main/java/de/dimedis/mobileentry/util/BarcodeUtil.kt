package de.dimedis.mobileentry.util

import android.app.Activity
import com.google.zxing.integration.android.IntentIntegrator
import de.dimedis.mobileentry.barcode.BarcodeCaptureActivity

object BarcodeUtil {
    fun openCamReader(activity: Activity) {
        val integrator = IntentIntegrator(activity)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scan a barcode")
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(true)
        integrator.captureActivity = BarcodeCaptureActivity::class.java
        integrator.setOrientationLocked(true)
        integrator.initiateScan()
    }

    fun removeTroubleCharacters(barcode: String): String {
        var barcode = barcode
        barcode = removeNonAscii(barcode)
        barcode = removeNonPrintable(barcode)
        barcode = removeSomeControlChar(barcode)
        barcode = removeFullControlChar(barcode)
        return barcode
    }

    private fun removeNonAscii(str: String): String {
        return str.replace("[^\\x00-\\x7F]".toRegex(), "")
    }

    private fun removeNonPrintable(str: String): String { // All Control Char
        return str.replace("[\\p{C}]".toRegex(), "")
    }

    private fun removeSomeControlChar(str: String): String { // Some Control Char
        return str.replace("[\\p{Cntrl}\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]".toRegex(), "")
    }

    private fun removeFullControlChar(str: String): String {
        return removeNonPrintable(str).replace("[\\r\\n\\t]".toRegex(), "")
    }
}