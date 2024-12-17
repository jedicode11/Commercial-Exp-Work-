package com.example.myscannerapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import device.common.DecodeResult;
import device.common.DecodeStateCallback;
import device.common.ScanConst;
import device.sdk.ScanManager;

@SuppressWarnings("unused")
public class MigrateNextVarActivity extends AppCompatActivity {
    private static final String TAG = "tScanner";


    private ScanManager mScanner;
    private DecodeResult mDecodeResult;
    private boolean mKeyLock = false;

    private TextView mBarType = null;
    private TextView mResult = null;
    private CheckBox mAutoScanOption = null;
    private CheckBox mBeepOption = null;
    private Button mEnabledProp = null;
    private CheckBox mEventCheck = null;

    private AlertDialog mDialog = null;
    private int mBackupResultType = ScanConst.ResultType.DCD_RESULT_COPYPASTE;
    private Context mContext;
    private ProgressDialog mWaitDialog = null;
    private final Handler mHandler = new Handler();
    private static ScanResultReceiver mScanResultReceiver = null;

    public class ScanResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mScanner != null) {
                try {
                    if (ScanConst.INTENT_USERMSG.equals(intent.getAction())) {
                        mScanner.aDecodeGetResult(mDecodeResult.recycle());
                        mBarType.setText(mDecodeResult.symName);
                        mResult.setText(mDecodeResult.toString());
                    } else if (ScanConst.INTENT_EVENT.equals(intent.getAction())) {
                        boolean result = intent.getBooleanExtra(ScanConst.EXTRA_EVENT_DECODE_RESULT, false);
                        int decodeBytesLength = intent.getIntExtra(ScanConst.EXTRA_EVENT_DECODE_LENGTH, 0);
                        byte[] decodeBytesValue = intent.getByteArrayExtra(ScanConst.EXTRA_EVENT_DECODE_VALUE);
                        String decodeValue = new String(decodeBytesValue, 0, decodeBytesLength);
                        int decodeLength = decodeValue.length();
                        String symbolName = intent.getStringExtra(ScanConst.EXTRA_EVENT_SYMBOL_NAME);
                        byte symbolId = intent.getByteExtra(ScanConst.EXTRA_EVENT_SYMBOL_ID, (byte) 0);
                        int symbolType = intent.getIntExtra(ScanConst.EXTRA_EVENT_SYMBOL_TYPE, 0);
                        byte letter = intent.getByteExtra(ScanConst.EXTRA_EVENT_DECODE_LETTER, (byte) 0);
                        byte modifier = intent.getByteExtra(ScanConst.EXTRA_EVENT_DECODE_MODIFIER, (byte) 0);
                        int decodingTime = intent.getIntExtra(ScanConst.EXTRA_EVENT_DECODE_TIME, 0);
                        Log.d(TAG, "1. result: " + result);
                        Log.d(TAG, "2. bytes length: " + decodeBytesLength);
                        Log.d(TAG, "3. bytes value: " + Arrays.toString(decodeBytesValue));
                        Log.d(TAG, "4. decoding length: " + decodeLength);
                        Log.d(TAG, "5. decoding value: " + decodeValue);
                        Log.d(TAG, "6. symbol name: " + symbolName);
                        Log.d(TAG, "7. symbol id: " + symbolId);
                        Log.d(TAG, "8. symbol type: " + symbolType);
                        Log.d(TAG, "9. decoding letter: " + letter);
                        Log.d(TAG, "10.decoding modifier: " + modifier);
                        Log.d(TAG, "11.decoding time: " + decodingTime);
                        mBarType.setText(symbolName);
                        mResult.setText(decodeValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private DecodeStateCallback mStateCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mContext = this;
        if (!isPMModel()) {
            return;
        }

        mScanner = new ScanManager();
        mDecodeResult = new DecodeResult();
        mScanResultReceiver = new ScanResultReceiver();

        mStateCallback = new DecodeStateCallback(mHandler) {
            public void onChangedState(int state) {
                switch (state) {
                    case ScanConst.STATE_ON:
                    case ScanConst.STATE_TURNING_ON:
                        if (getEnableDialog().isShowing()) {
                            getEnableDialog().dismiss();
                        }
                        break;
                    case ScanConst.STATE_OFF:
                    case ScanConst.STATE_TURNING_OFF:
                        if (!getEnableDialog().isShowing()) {
                            getEnableDialog().show();
                        }
                        break;
                }
            }

            ;
        };


        mBarType = findViewById(R.id.textview_bar_type);
        mResult = findViewById(R.id.textview_scan_result);

        mAutoScanOption = findViewById(R.id.check_autoscan);
        mAutoScanOption.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mScanner != null) {
                if (isChecked) {
                    mScanner.aDecodeSetTriggerMode(ScanConst.TriggerMode.DCD_TRIGGER_MODE_AUTO);
                } else {
                    mScanner.aDecodeSetTriggerMode(ScanConst.TriggerMode.DCD_TRIGGER_MODE_ONESHOT);
                }
            }
        });

        mEventCheck = findViewById(R.id.check_event);
        mEventCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mScanner != null) {
                if (isChecked) {
                    mScanner.aDecodeSetResultType(ScanConst.ResultType.DCD_RESULT_EVENT);
                } else {
                    mScanner.aDecodeSetResultType(ScanConst.ResultType.DCD_RESULT_USERMSG);
                }
            }
        });

        mBeepOption = findViewById(R.id.check_beep);
        mBeepOption.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mScanner != null) {
                if (isChecked) {
                    mScanner.aDecodeSetBeepEnable(1);
                } else {
                    mScanner.aDecodeSetBeepEnable(0);
                }
            }
        });

        findViewById(R.id.button_scan_on).setOnClickListener(v -> {
            if (mScanner != null) {
                mScanner.aDecodeSetTriggerOn(1);
            }
        });

        findViewById(R.id.button_scan_off).setOnClickListener(v -> {
            if (mScanner != null) {
                if (mAutoScanOption.isChecked()) {
                    mScanner.aDecodeSetTriggerMode(ScanConst.TriggerMode.DCD_TRIGGER_MODE_ONESHOT);
                }

                mScanner.aDecodeSetTriggerOn(0);

                if (mAutoScanOption.isChecked()) {
                    mScanner.aDecodeSetTriggerMode(ScanConst.TriggerMode.DCD_TRIGGER_MODE_AUTO);
                }
            }
        });

        findViewById(R.id.button_enalbe_upc).setOnClickListener(v -> {
            if (mScanner != null) {
                mScanner.aDecodeSymSetEnable(ScanConst.SymbologyID.DCD_SYM_UPCA, 1);
            }
        });

        findViewById(R.id.button_disalbe_upc).setOnClickListener(v -> {
            if (mScanner != null) {
                mScanner.aDecodeSymSetEnable(ScanConst.SymbologyID.DCD_SYM_UPCA, 0);
            }
        });

        mEnabledProp = findViewById(R.id.button_prop_enalbe);
        mEnabledProp.setOnClickListener(v -> {
            if (mScanner != null) {
                int symID = ScanConst.SymbologyID.DCD_SYM_UPCA;
                int propCnt = mScanner.aDecodeSymGetLocalPropCount(symID);
                int propIndex = 0;

                for (int i = 0; i < propCnt; i++) {
                    String propName = mScanner.aDecodeSymGetLocalPropName(symID, i);
                    if (propName.equals("Send Check Character")) {
                        propIndex = i;
                        break;
                    }
                }

                if (!mKeyLock) {
                    mEnabledProp.setText(R.string.property_enable);
                    mKeyLock = true;
                    mScanner.aDecodeSymSetLocalPropEnable(symID, propIndex, 0);
                } else {
                    mEnabledProp.setText(R.string.property_disable);
                    mKeyLock = false;
                    mScanner.aDecodeSymSetLocalPropEnable(symID, propIndex, 1);
                }
            }
        });
    }

    private void initScanner() {
        if (mScanner != null) {
            mScanner.aRegisterDecodeStateCallback(mStateCallback);
            mBackupResultType = mScanner.aDecodeGetResultType();
            mScanner.aDecodeSetResultType(ScanConst.ResultType.DCD_RESULT_USERMSG);
            mEventCheck.setChecked(false);
            mAutoScanOption.setChecked(mScanner.aDecodeGetTriggerMode() == ScanConst.TriggerMode.DCD_TRIGGER_MODE_AUTO);
            mBeepOption.setChecked(mScanner.aDecodeGetBeepEnable() == 1);
        }
    }

    private Runnable mStartOnResume = () -> runOnUiThread(() -> {
        initScanner();
        if (mWaitDialog != null && mWaitDialog.isShowing()) {
            mWaitDialog.dismiss();
        }
    });

    private AlertDialog getEnableDialog() {
        if (mDialog == null) {
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setTitle(R.string.app_name);
            dialog.setMessage("Your scanner is disabled. Do you want to enable it?");

            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel),
                    (dialog1, which) -> finish());
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
                    (dialog12, which) -> {
                        Intent intent = new Intent(ScanConst.LAUNCH_SCAN_SETTING_ACITON);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog12.dismiss();
                    });
            dialog.setCancelable(false);
            mDialog = dialog;
        }
        return mDialog;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isPMModel()) {
            return;
        }

        mWaitDialog = ProgressDialog.show(mContext, "", getString(R.string.msg_wait), true);
        mHandler.postDelayed(mStartOnResume, 1000);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ScanConst.INTENT_USERMSG);
        filter.addAction(ScanConst.INTENT_EVENT);
        mContext.registerReceiver(mScanResultReceiver, filter);

        hasBBApi(mContext);

    }

    public static int hasBBApi = -1;

    public static boolean hasBBApi(Context mContext) {
        PackageManager pm = mContext.getPackageManager();
        try {
            Log.d(TAG, "========================== package: " + pm.getPackageInfo("kr.co.bluebird.android.bbapi.barcodelibconnector", PackageManager.GET_ACTIVITIES));
            hasBBApi = 1;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            hasBBApi = 0;
        }
        return hasBBApi > 0;
    }

    @Override
    protected void onPause() {
        if (!isPMModel()) {
            super.onPause();
            return;
        }

        if (mScanner != null) {
            mScanner.aDecodeSetResultType(mBackupResultType);
            mScanner.aUnregisterDecodeStateCallback(mStateCallback);
        }
        mContext.unregisterReceiver(mScanResultReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mScanner != null) {
            mScanner.aDecodeSetResultType(mBackupResultType);
        }
        mScanner = null;
        super.onDestroy();
    }

    private boolean isPMModel() {
        return Build.MODEL.startsWith(("PM"));
    }
}