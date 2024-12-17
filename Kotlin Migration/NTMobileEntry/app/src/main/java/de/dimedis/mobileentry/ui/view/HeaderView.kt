package de.dimedis.mobileentry.ui.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.stetho.common.Util
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.model.StatusManager
import de.dimedis.mobileentry.ui.view.HeaderView.OnMenuButtonClickedListener
import de.dimedis.mobileentry.util.BarcodeUtil
import de.dimedis.mobileentry.util.CommonUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HeaderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle:
Int = 0) : RelativeLayout(context, attrs, defStyle) {

    private val mCameraReaderToolBarButton: ImageButton
    private val mStatusTextView: TextView
    private val mBorderNameTextView: TextView
    private val mMenuButton: ImageButton
    private var mDeviceId: String? = null
    private var mBorderName: String? = null
    private var mOnMenuButtonClickedListener: OnMenuButtonClickedListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_header, this, true)
        setBackgroundResource(R.color.grey_light)
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.HeaderView, defStyle, 0)
        val showStatus = styledAttrs.getBoolean(R.styleable.HeaderView_hv_showStatus, true)
        val showBorderName = styledAttrs.getBoolean(R.styleable.HeaderView_hv_showBorderName, true)
        val showMenuButton = styledAttrs.getBoolean(R.styleable.HeaderView_hv_showMenuButton, true)
        styledAttrs.recycle()
        mStatusTextView = findViewById(R.id.tv_status)
        mStatusTextView.visibility = if (showStatus) VISIBLE else GONE
        mBorderNameTextView = findViewById(R.id.tv_border_name)
        mBorderNameTextView.visibility = if (showBorderName) VISIBLE else GONE
        mMenuButton = findViewById(R.id.btn_menu)
        mMenuButton.visibility = if (showMenuButton) VISIBLE else GONE
        mMenuButton.setOnClickListener {
            if (mOnMenuButtonClickedListener != null) {
                mOnMenuButtonClickedListener!!.onMenuButtonClicked()
            }
        }
        mCameraReaderToolBarButton = findViewById(R.id.btn_menu_camera)
        mCameraReaderToolBarButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // TODO Version
                val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
                if (!hasPermissions(context, *PERMISSIONS)) {
                    ActivityCompat.requestPermissions((context as Activity), PERMISSIONS, REQUEST)
                } else {
                    openBarCodeReader(context)
                }
            } else {
                openBarCodeReader(context)
            }
        }
        updateStatus()
    }

    private fun openBarCodeReader(context: Context) {
        BarcodeUtil.openCamReader(context as Activity)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        EventBus.getDefault().register(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        EventBus.getDefault().unregister(this)
    }

    private fun updateStatus() {
        if (isInEditMode) {
            return
        }
        val status: StatusManager.Status = StatusManager.getInstance()!!.getStatus()
        var statusText = status.getText(status)
        if (!TextUtils.isEmpty(mDeviceId)) {
            statusText += " - $mDeviceId"
        }
        mStatusTextView.text = statusText
        var statusIconId = -1
        statusIconId = when (status) {
            StatusManager.Status.OFFLINE, StatusManager.Status.LOCAL_SCAN -> R.drawable.status_offline
            StatusManager.Status.ONLINE -> R.drawable.status_online
        }
        val statusIcon = ContextCompat.getDrawable(context, statusIconId)
        mStatusTextView.setCompoundDrawablesWithIntrinsicBounds(statusIcon, null, null, null)
    }

    fun setOnMenuButtonClickedListener(listener: OnMenuButtonClickedListener) {
        mOnMenuButtonClickedListener = listener
    }

    fun setDeviceId(deviceId: String?) {
        if (!TextUtils.equals(mDeviceId, deviceId)) {
            mDeviceId = deviceId
            updateStatus()
        }
    }

    fun setBorderName(name: String?) {
        if (!TextUtils.equals(mDeviceId, name)) {
            mBorderName = name
            mBorderNameTextView.text = name
        }
    }

    fun setMenuButtonVisible(isVisible: Boolean) {
        mMenuButton.visibility = if (isVisible) VISIBLE else GONE
    }

    fun setCameraButtonVisible(isVisible: Boolean) {
        var isVisible = isVisible
        if (!isVisible && !CommonUtils.hasBBApi()) {
            // fixme: force camera reader visibility until device has no hardware QR code reader
            isVisible = true
        }
        mCameraReaderToolBarButton.visibility = if (isVisible) VISIBLE else GONE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: StatusManager.StatusChangedEvent?) {
        updateStatus()
    }

    public override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(STATE_VIEW, super.onSaveInstanceState())
        bundle.putString(STATE_DEVICE_ID, mDeviceId)
        bundle.putString(STATE_BORDER_NAME, mBorderName)
        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val bundle = state as Bundle
        super.onRestoreInstanceState(bundle.getParcelable(STATE_VIEW))
        setDeviceId(bundle.getString(STATE_DEVICE_ID))
        setBorderName(bundle.getString(STATE_BORDER_NAME))
    }

    interface OnMenuButtonClickedListener {
        fun onMenuButtonClicked()
    }

    companion object {
        private const val STATE_VIEW = "state_view"
        private const val STATE_DEVICE_ID = "state_device_id"
        private const val STATE_BORDER_NAME = "state_border_name"
        const val REQUEST = 112
        private fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && context != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            permission
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return false
                    }
                }
            }
            return true
        }
    }
}