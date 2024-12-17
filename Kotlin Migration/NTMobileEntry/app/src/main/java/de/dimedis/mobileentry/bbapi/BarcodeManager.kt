package de.dimedis.mobileentry.bbapi

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.*
import android.os.Handler
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.util.AppContext.get
import de.dimedis.mobileentry.util.BarcodeUtil.removeTroubleCharacters
import de.dimedis.mobileentry.util.Logger.i
import de.dimedis.mobileentry.util.PrefUtils
import de.dimedis.mobileentry.util.SoundPlayerUtil
import de.dimedis.mobileentry.util.SoundType
import device.common.DecodeResult
import device.common.DecodeStateCallback
import device.common.ScanConst.*
import device.sdk.ScanManager
import org.greenrobot.eventbus.EventBus

class BarcodeManager private constructor() {

    companion object {
        private val TAG = BarcodeManager::class.java.simpleName
        private const val OPEN_REQUEST_ID = 1
        private const val CLOSE_REQUEST_ID = 2
        private const val SET_TRIGGER_ON_REQUEST_ID = 3
        private const val SET_TRIGGER_OFF_REQUEST_ID = 4
        private const val DATA_WEDGE_REQUEST_ID = 5
        private const val SOUND_REQUEST_ID = 6
        private const val INVALID_HANDLE = -1

        @Volatile
        private var sInstance: BarcodeManager? = null
        private var mScanResultReceiver: ScanResultReceiver? = null
        val instance: BarcodeManager
            get() {
                if (sInstance == null) {
                    synchronized(BarcodeManager::class.java) {
                        if (sInstance == null) {
                            sInstance = BarcodeManager()
                            sInstance!!.init()
                        }
                    }
                }
                return sInstance!!
            }
    }

    private var mScanner: ScanManager? = null
    private var mDecodeResult: DecodeResult? = null
    private val mKeyLock = false
    private var mDialog: AlertDialog? = null
    private var mBackupResultType: Int = ResultType.DCD_RESULT_COPYPASTE
    private var mActivity: Activity? = null
    private val mWaitDialog: ProgressDialog? = null
    private val mHandler = Handler()

    inner class ScanResultReceiver : BroadcastReceiver() {
        fun register(activity: Activity) {
            val mFilter = IntentFilter()
            mFilter.addAction(Constants.ACTION_BARCODE_CALLBACK_REQUEST_SUCCESS)
            mFilter.addAction(Constants.ACTION_BARCODE_CALLBACK_REQUEST_FAILED)
            mFilter.addAction(Constants.ACTION_BARCODE_CALLBACK_DECODING_DATA)
            mFilter.addAction(Constants.ACTION_BARCODE_CALLBACK_GET_STATUS)
            mFilter.addAction(INTENT_USERMSG)
            mFilter.addAction(INTENT_EVENT)
            activity.registerReceiver(this, mFilter)
        }

        fun unregister(activity: Activity) {
            try {
                activity.unregisterReceiver(this)
            } catch (ignore: IllegalArgumentException) {
                // Receiver not registered.
            }
        }

        override fun onReceive(context: Context, intent: Intent) {

            val handle = intent.getIntExtra(Constants.EXTRA_HANDLE, INVALID_HANDLE)
            val requestId = intent.getIntExtra(Constants.EXTRA_INT_DATA3, 0)
            i(TAG, "onReceive requestId:" + requestId + " handle:" + handle + " action:" + intent.action)
            when (intent.action) {
                INTENT_USERMSG -> {
                    val isReadSuccess = intent.getBooleanExtra("EXTRA_USERMSG", false)
                    if (isReadSuccess) {
                        mScanner?.aDecodeGetResult(mDecodeResult?.recycle())
                        val symName: String? = mDecodeResult?.symName
                        var barcode: String? = mDecodeResult.toString()
                        barcode = removeTroubleCharacters(barcode.toString())
                        i(TAG, "Barcode is scanned, barcode = $barcode")
                        EventBus.getDefault().post(BarcodeScannedEvent(barcode))
                        SoundPlayerUtil.getInstance().playSoundAsync(SoundType.CODE_SCAN_OK_OR_MANUAL_ACCEPTED)
                    } else {
                        i(TAG, "SCAN FAIL !!")
                    }
                }
                INTENT_EVENT -> {
                    val result = intent.getBooleanExtra("EXTRA_EVENT_DECODE_RESULT", false)
                    val decodeBytesLength = intent.getIntExtra("EXTRA_EVENT_DECODE_LENGTH", 0)
                    val decodeBytesValue = intent.getByteArrayExtra("EXTRA_EVENT_DECODE_VALUE")
                    val decodeValue = String(decodeBytesValue!!, 0, decodeBytesLength)
                    val decodeLength = decodeValue.length
                    val symbolName = intent.getStringExtra("EXTRA_EVENT_SYMBOL_NAME")
                    val symbolId = intent.getByteExtra("EXTRA_EVENT_SYMBOL_ID", 0.toByte())
                    val symbolType = intent.getIntExtra("EXTRA_EVENT_SYMBOL_TYPE", 0)
                    val letter = intent.getByteExtra("EXTRA_EVENT_DECODE_LETTER", 0.toByte())
                    val modifier = intent.getByteExtra("EXTRA_EVENT_DECODE_MODIFIER", 0.toByte())
                    val decodingTime = intent.getIntExtra("EXTRA_EVENT_DECODE_TIME", 0)
                    i(TAG, "1. result: $result")
                    i(TAG, "2. bytes length: $decodeBytesLength")
                    i(TAG, "3. bytes value: $decodeBytesValue")
                    i(TAG, "4. decoding length: $decodeLength")
                    i(TAG, "5. decoding value: $decodeValue")
                    i(TAG, "6. symbol name: $symbolName")
                    i(TAG, "7. symbol id: $symbolId")
                    i(TAG, "8. symbol type: $symbolType")
                    i(TAG, "9. decoding letter: $letter")
                    i(TAG, "10.decoding modifier: $modifier")
                    i(TAG, "11.decoding time: $decodingTime")
                }
                Constants.ACTION_BARCODE_CALLBACK_REQUEST_SUCCESS -> if (requestId == OPEN_REQUEST_ID) {
                    i(TAG, "BarcodeManager is opened, handle = $handle")
                    mBarcodeHandle = handle
                    onOpened()
                }
                Constants.ACTION_BARCODE_CALLBACK_REQUEST_FAILED -> {
                    val error = intent.getIntExtra(Constants.EXTRA_INT_DATA2, 0)
                    i(TAG, "Request failed, request id = " + requestId + ", error = " + getErrorText(error))
                    if (requestId == OPEN_REQUEST_ID && error == Constants.ERROR_BARCODE_ERROR_ALREADY_OPENED) {
                        onOpened()
                    } else if (PrefUtils.getPermScanModeValue()) {
                        onTimeout()
                    }
                }
                Constants.ACTION_BARCODE_CALLBACK_DECODING_DATA -> {
                    val data = intent.getByteArrayExtra(Constants.EXTRA_BARCODE_DECODING_DATA)
                    if (data != null) {
                        var barcode: String? = String(data)
                        barcode = removeTroubleCharacters(barcode.toString())
                        i(TAG, "Barcode is scanned, barcode = $barcode")
                        EventBus.getDefault().post(BarcodeScannedEvent(barcode))
                        SoundPlayerUtil.getInstance().playSoundAsync(SoundType.CODE_SCAN_OK_OR_MANUAL_ACCEPTED)
                    }
                }
                Constants.ACTION_BARCODE_CALLBACK_GET_STATUS -> {
                    val status = intent.getIntExtra(Constants.EXTRA_INT_DATA2, 0)
                    i(TAG, "status = $status")
                }
            }
        }

        // private String[] STATUS_ARR = {STATUS_CLOSE, STATUS_OPEN, STATUS_TRIGGER_ON};
        private fun getErrorText(error: Int): String {
            when (error) {
                Constants.ERROR_FAILED -> return "ERROR_FAILED"
                Constants.ERROR_NOT_SUPPORTED -> return "ERROR_NOT_SUPPORTED"
                Constants.ERROR_NO_RESPONSE -> return "ERROR_NO_RESPONSE"
                Constants.ERROR_BATTERY_LOW -> return "ERROR_BATTERY_LOW"
                Constants.ERROR_BARCODE_DECODING_TIMEOUT -> return "ERROR_BARCODE_DECODING_TIMEOUT"
                Constants.ERROR_BARCODE_ERROR_USE_TIMEOUT -> return "ERROR_BARCODE_ERROR_USE_TIMEOUT"
                Constants.ERROR_BARCODE_ERROR_ALREADY_OPENED -> return "ERROR_BARCODE_ERROR_ALREADY_OPENED"
                Constants.ERROR_ERROR_CHECKSUM -> return "ERROR_ERROR_CHECKSUM"
                Constants.ERROR_ERROR_BARCODE_CAMERA_USED -> return "ERROR_ERROR_BARCODE_CAMERA_USED"
            }
            return "UNKNOWN_ERROR, id = $error"
        }
    }

    private var mStateCallback: DecodeStateCallback? = null
    private var mBarcodeHandle = INVALID_HANDLE
    fun init() {
        mScanner = ScanManager()
        mDecodeResult = DecodeResult()
        mScanResultReceiver = ScanResultReceiver()
        mStateCallback = object : DecodeStateCallback(mHandler) {
            override fun onChangedState(state: Int) {
                when (state) {
                    STATE_ON, STATE_TURNING_ON -> if (enableDialog!!.isShowing) {
                        enableDialog!!.dismiss()
                    }
                    STATE_OFF, STATE_TURNING_OFF -> if (!enableDialog!!.isShowing) {
                        enableDialog!!.show()
                    }
                }
            }
        }
    }

    private fun initScanner() {
        if (mScanner != null) {
            mScanner!!.aRegisterDecodeStateCallback(mStateCallback)
            mBackupResultType = mScanner!!.aDecodeGetResultType()
            mScanner!!.aDecodeSetResultType(ResultType.DCD_RESULT_USERMSG)
        }
    }

    private val mStartOnResume = Runnable {
        mActivity!!.runOnUiThread {
            initScanner()
            if (mWaitDialog != null && mWaitDialog.isShowing) {
                mWaitDialog.dismiss()
            }
        }
    }
    private val enableDialog: AlertDialog?
        get() {
            if (mDialog == null) {
                val dialog = AlertDialog.Builder(mActivity).create()
                dialog.setTitle(R.string.app_name)
                dialog.setMessage("Your scanner is disabled. Do you want to enable it?")
                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, mActivity!!.getString(android.R.string.cancel)
                ) { dialog1: DialogInterface?, which: Int -> mActivity!!.finish() }
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, mActivity!!.getString(android.R.string.ok)
                ) { dialog12: DialogInterface, which: Int ->
                    val intent = Intent(LAUNCH_SCAN_SETTING_ACITON)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    mActivity!!.startActivity(intent)
                    dialog12.dismiss()
                }
                dialog.setCancelable(false)
                mDialog = dialog
            }
            return mDialog
        }

    fun open(activity: Activity) {
        i(TAG, "Open BarcodeManager")
        mActivity = activity
        mHandler.postDelayed(mStartOnResume, 1000)
        mScanResultReceiver!!.register(activity)
        val intent = Intent(ENABLED_SCANNER_ACTION)
        intent.putExtra(EXTRA_ENABLED_SCANNER, 1)
        get().sendBroadcast(intent)
    }

    fun status(activity: Activity) {
        i(TAG, "Status BarcodeManager")
        mScanResultReceiver!!.register(activity)
        val intent = Intent(Constants.ACTION_BARCODE_GET_STATUS)
        intent.putExtra(Constants.EXTRA_HANDLE, mBarcodeHandle)
        intent.putExtra(Constants.EXTRA_INT_DATA3, 300)
        activity.sendBroadcast(intent)
    }

    fun close(activity: Activity) {
        i(TAG, "Close BarcodeManager")
        mActivity = activity
        mScanResultReceiver!!.unregister(activity)
    }

    fun setTriggerOn(activity: Activity?) {
        i(TAG, "Set trigger on")
        mScanner?.aDecodeSetTriggerOn(1)
    }

    fun setTriggerOff(activity: Activity) {
        i(TAG, "Set trigger off")
        val triggerDisableIntent = Intent(ENABLED_TRIGGER_ACTION)
        triggerDisableIntent.putExtra(EXTRA_ENABLED_TRIGGER, 0)
        activity.sendBroadcast(triggerDisableIntent)
    }

    fun setDataWedgeEnabled(isEnabled: Boolean) {
        i(TAG, "Set data wedge enabled: $isEnabled")
        val intent = Intent(Constants.ACTION_BARCODE_SET_PARAMETER)
        if (mBarcodeHandle != INVALID_HANDLE) {
            intent.putExtra(Constants.EXTRA_HANDLE, mBarcodeHandle)
        }
        intent.putExtra(Constants.EXTRA_INT_DATA2, BBAPI.BARCODE_MODE_DATA_WEDGE)
        intent.putExtra(Constants.EXTRA_STR_DATA1, if (isEnabled) "1" else "0")
        intent.putExtra(Constants.EXTRA_INT_DATA3, DATA_WEDGE_REQUEST_ID)
        get().sendBroadcast(intent)
    }

    fun setSoundEnabled(isEnabled: Boolean) {
        i(TAG, "Set sound enabled: $isEnabled")
        val intent = Intent(Constants.ACTION_BARCODE_SET_PARAMETER)
        if (mBarcodeHandle != INVALID_HANDLE) {
            intent.putExtra(Constants.EXTRA_HANDLE, mBarcodeHandle)
        }
        intent.putExtra(Constants.EXTRA_INT_DATA2, BBAPI.BARCODE_MODE_SOUND)
        intent.putExtra(Constants.EXTRA_STR_DATA1, if (isEnabled) "1" else "0")
        intent.putExtra(Constants.EXTRA_INT_DATA3, SOUND_REQUEST_ID)
        get().sendBroadcast(intent)
    }

    private fun onOpened() {
        setDataWedgeEnabled(false)
        setSoundEnabled(false)
    }

    private fun onTimeout() {
        EventBus.getDefault().post(BarcodeScannerTimeoutNoDecodingEvent())
    }

    class BarcodeScannedEvent(val barcode: String?)
    class BarcodeScannerTimeoutNoDecodingEvent
}