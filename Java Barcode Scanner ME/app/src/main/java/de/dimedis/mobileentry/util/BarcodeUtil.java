package de.dimedis.mobileentry.util;

import android.app.Activity;

import com.google.zxing.integration.android.IntentIntegrator;

import de.dimedis.mobileentry.barcode.BarcodeCaptureActivity;

public class BarcodeUtil {
    public static void openCamReader(Activity activity) {
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.setCaptureActivity(BarcodeCaptureActivity.class);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    public static String removeTroubleCharacters(String barcode) {
        barcode = removeNonAscii(barcode);
        barcode = removeNonPrintable(barcode);
        barcode = removeSomeControlChar(barcode);
        barcode = removeFullControlChar(barcode);
        return barcode;
    }

    private static String removeNonAscii(String str) {
        return str.replaceAll("[^\\x00-\\x7F]", "");
    }

    private static String removeNonPrintable(String str) {
        return str.replaceAll("[\\p{C}]", "");
    }

    private static String removeSomeControlChar(String str) {
        return str.replaceAll("[\\p{Cntrl}\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "");
    }

    private static String removeFullControlChar(String str) {
        return removeNonPrintable(str).replaceAll("[\\r\\n\\t]", "");
    }
}
