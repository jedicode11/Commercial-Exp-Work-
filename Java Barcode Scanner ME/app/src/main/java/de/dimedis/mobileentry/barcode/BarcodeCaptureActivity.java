package de.dimedis.mobileentry.barcode;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.CaptureActivity;

public final class BarcodeCaptureActivity extends CaptureActivity {
    static final String TAG = "BarcodeCaptureActivity";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            String dataStr;
            if ((dataStr = data.getStringExtra(Intents.Scan.RESULT)) != null) {
                Log.e(TAG, "bar code: " + dataStr);
            }
        } else {
            Log.e(TAG, "###");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
